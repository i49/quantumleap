/* 
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.i49.unite.core.storage.jdbc;

import static io.github.i49.unite.core.common.Message.*;
import static io.github.i49.unite.core.common.Preconditions.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import io.github.i49.unite.api.base.WorkflowException;
import io.github.i49.unite.api.tasks.Task;
import io.github.i49.unite.api.workflow.Job;
import io.github.i49.unite.api.workflow.JobStatus;
import io.github.i49.unite.api.workflow.Workflow;
import io.github.i49.unite.api.workflow.WorkflowStatus;
import io.github.i49.unite.core.storage.BinaryMarshaller;
import io.github.i49.unite.core.storage.JsonBindingMarshaller;
import io.github.i49.unite.core.storage.Marshaller;
import io.github.i49.unite.core.storage.StorageConfiguration;
import io.github.i49.unite.core.storage.WorkflowStorage;
import io.github.i49.unite.core.workflow.JobLink;
import io.github.i49.unite.core.workflow.ManagedJob;
import io.github.i49.unite.core.workflow.ManagedWorkflow;

/**
 * The storage which connects to JDBC drivers.
 * 
 * @author i49
 */
public class JdbcWorkflowStorage implements WorkflowStorage {
    
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(JdbcWorkflowStorage.class.getName());
 
    private final Connection connection;
    private final Map<SqlCommand, Query> queries;

    private final Marshaller<String> textMarshaller;
    private final Marshaller<byte[]> binaryMarshaller;

    private final RowMappers mappers;
    
    public JdbcWorkflowStorage(StorageConfiguration config) {
        this.textMarshaller = JsonBindingMarshaller.getInstance();
        this.binaryMarshaller = BinaryMarshaller.getInstance();
        this.mappers = new RowMappers();
        try {
            this.connection = connect(config);
            createSchema(this.connection);
            this.queries = prepareAllQueries(this.connection);
        } catch (WorkflowException e) {
            forceToClose();
            throw e;
        }
    }

    @Override
    public void close() {
        if (connection == null) {
            return;
        }
        try {
            this.connection.close();
        } catch (SQLException e) {
            throw new WorkflowException(REPOSITORY_ACCESS_ERROR_OCCURRED.toString(), e);
        }
    }
 
    @Override
    public void addWorkflow(Workflow workflow) {
        checkNotNull(workflow, "workflow");
        checkRealType(workflow, ManagedWorkflow.class, "workflow");
        addWorkflow((ManagedWorkflow)workflow);
    }

    @Override
    public void clear() {
        getQuery(SqlCommand.DELETE_TASKS).execute();
        getQuery(SqlCommand.DELETE_JOB_LINKS).execute();
        getQuery(SqlCommand.DELETE_JOBS).execute();
        getQuery(SqlCommand.DELETE_WORKFLOWS).execute();
    }

    @Override
    public long countWorkflows() {
        return getQuery(SqlCommand.COUNT_WORKFLOWS).queryForLong();
    }

    @Override
    public long countJobs() {
        return getQuery(SqlCommand.COUNT_JOBS).queryForLong();
    }

    @Override
    public long countJobsWithStatus(JobStatus status) {
        return getQuery(SqlCommand.COUNT_JOBS_BY_STATUS).setEnum(1, status).queryForLong();
    }

    @Override
    public List<Job> findJobsByStatus(JobStatus status) {
        Query q = getQuery(SqlCommand.FIND_JOBS_BY_STATUS);
        q.setEnum(1, status);
        List<ManagedJob> jobs = q.queryForList(mappers::mapToJob);
        return jobs.stream().map(job->{
            job.setTasks(findTasks(job.getId()));
            return job;
        }).collect(Collectors.toList());
    }

    @Override
    public Optional<Job> findFirstJobByStatus(JobStatus status) {
        Query q = getQuery(SqlCommand.FIND_FIRST_JOB_BY_STATUS);
        q.setEnum(1, status);
        return q.queryForObject(mappers::mapToJob).map(job->{
            job.setTasks(findTasks(job.getId()));
            return job;
        });
    }
    
    @Override
    public Job findJobById(long jobId) {
        Query q = getQuery(SqlCommand.FIND_JOB_BY_ID);
        q.setLong(1, jobId);
        return q.queryForObject(mappers::mapToJob).get();
    }
    
    @Override
    public JobStatus getJobStatus(long jobId) {
        Query q = getQuery(SqlCommand.FIND_JOB_STATUS_BY_ID);
        q.setLong(1, jobId);
        return q.queryForObject(rs->JobStatus.valueOf(rs.getString(1))).get();
    }
    
    @Override
    public Workflow getWorkflow(long workflowId) {
        Query q = getQuery(SqlCommand.FIND_WORKFLOW_BY_ID);
        q.setLong(1, workflowId);
        return q.queryForObject(mappers::mapToWorkflow).get();
    }
    
    /* EnhancedWorkflowRepository interface */
  
    @Override
    public List<JobLink> findLinksByTarget(ManagedJob target) {
        Query q = getQuery(SqlCommand.FIND_LINKS_BY_TARGET);
        q.setLong(1, target.getId());
        return q.queryForList(mappers.mappingToJobLink(target));
    }
    
    @Override
    public List<Long> findNextJobs(Job job) {
        Query q = getQuery(SqlCommand.FIND_NEXT_JOBS);
        q.setLong(1, job.getId());
        return q.queryForList(rs->rs.getLong(1));
    }
    
    @Override
    public void storeJob(Job job, JobStatus status, Map<String, Object> jobOutput, String[] standardOutput) {
        Query q = getQuery(SqlCommand.UPDATE_JOB);
        q.setEnum(1, status);
        q.setBytes(2, marshal(jobOutput));
        q.setString(3, marshalToString(standardOutput));
        q.setLong(4, job.getId());
        q.update();
    }
    
    @Override
    public int updateJobStatusIfReady(long jobId) {
        Query q = getQuery(SqlCommand.UPDATE_JOB_STATUS_IF_READY);
        q.setLong(1, jobId);
        return q.update();
    }
  
    // helper methods
    
    private Connection connect(StorageConfiguration config) {
        try {
            return DriverManager.getConnection(
                    config.getUrl(), config.getUsername(), config.getPassword());
        } catch (SQLException e) {
            throw new WorkflowException(REPOSITORY_ACCESS_ERROR_OCCURRED.toString(), e);
        }
    }

    private void forceToClose() {
        if (this.connection == null) {
            return;
        }
        try {
            this.connection.close();
        } catch (SQLException e) {
            // ignores
        }
    }

    private void addWorkflow(ManagedWorkflow workflow) {
        workflow.setStatus(WorkflowStatus.READY);
        long workflowId = insertWorkflow(workflow);
        for (ManagedJob job: workflow.getManagedJobs()) {
            job.setWorkflowId(workflowId);
            addJob(job, workflow.getDependenciesOf(job));
        }
        for (JobLink link: workflow.getJobLinks()) {
            insertJobLink(link);
        }
    }

    private void addJob(ManagedJob job, Set<ManagedJob> dependencies) {
        JobStatus status = dependencies.isEmpty() ? JobStatus.READY : JobStatus.WAITING;
        long jobId = insertJob(job, status);
        int sequence = 0;
        for (Task task : job.getTasks()) {
            insertTask(task, jobId, sequence++);
        }
    }
    
    private long insertWorkflow(ManagedWorkflow workflow) {
        Query q = getQuery(SqlCommand.INSERT_WORKFLOW);
        q.setString(1, workflow.getName());
        q.setEnum(2, workflow.getStatus());
        long id = q.updateAndGenerateLong();
        workflow.setId(id);
        return id;
    }

    private long insertJob(ManagedJob job, JobStatus status) {
        Query q = getQuery(SqlCommand.INSERT_JOB);
        q.setString(1, job.getName());
        q.setEnum(2, status);
        q.setBytes(3, marshal(job.getInputParameters()));
        q.setLong(4, job.getWorkdlowId());
        long jobId = q.updateAndGenerateLong();
        job.setId(jobId);
        return jobId;
    }
    
    private void insertJobLink(JobLink link) {
        Query q = getQuery(SqlCommand.INSERT_JOB_LINK);
        q.setLong(1, link.getSource().getId());
        q.setLong(2, link.getTarget().getId());
        q.setString(3, link.getMapper().getClass().getName());
        q.setBytes(4, marshal(link.getMapper()));
        q.update();
    }
    
    private void insertTask(Task task, long jobId, int sequenceNumber) {
        Query q = getQuery(SqlCommand.INSERT_TASK);
        q.setLong(1, jobId);
        q.setInt(2, sequenceNumber);
        q.setString(3, task.getClass().getName());
        String params = marshalToString(task);
        q.setString(4, params);
        q.update();
    }

    private Query getQuery(SqlCommand command) {
        return queries.get(command);
    }

    private void createSchema(Connection connection) {
        if (checkSchemaExistence(connection)) {
            return;
        }
        SqlScriptRunner runner = new SqlScriptRunner(connection);
        runner.runScript("create-schema.sql");
    }
    
    private boolean checkSchemaExistence(Connection connection) {
        try {
            DatabaseMetaData meta = connection.getMetaData();
            try (ResultSet resultSet = meta.getTables(null, null, "WORKFLOW", null)) {
                if (resultSet.next()) {
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new WorkflowException(REPOSITORY_ACCESS_ERROR_OCCURRED.toString(), e);
        }
    }

    private Map<SqlCommand, Query> prepareAllQueries(Connection connection) {
        try {
            Map<SqlCommand, Query> map = new EnumMap<SqlCommand, Query>(SqlCommand.class);
            for (SqlCommand c : SqlCommand.values()) {
                map.put(c, c.getQuery(connection));
            }
            return map;
        } catch (SQLException e) {
            throw new WorkflowException(REPOSITORY_ACCESS_ERROR_OCCURRED.toString(), e);
        }
    }
    
    private List<Task> findTasks(long jobId) {
        Query q = getQuery(SqlCommand.FIND_TASK);
        q.setLong(1, jobId);
        return q.queryForList(mappers::mapToTask);
    }
    
    private byte[] marshal(Object object) {
        return this.binaryMarshaller.marshal(object);
    }
    
    private String marshalToString(Object object) {
        return this.textMarshaller.marshal(object);
    }
}
