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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import io.github.i49.unite.core.storage.WorkflowStorage;
import io.github.i49.unite.core.workflow.JobLink;
import io.github.i49.unite.core.workflow.ManagedJob;
import io.github.i49.unite.core.workflow.ManagedWorkflow;

/**
 * Connected JDBC storage.
 * 
 * @author i49
 */
public class ActiveWorkflowStorage extends JdbcSession implements WorkflowStorage {
    
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(ActiveWorkflowStorage.class.getName());
 
    private final Marshaller<String> textMarshaller;
    private final Marshaller<byte[]> binaryMarshaller;

    private final RowMappers mappers;
    
    public ActiveWorkflowStorage(Connection connection) {
        super(connection);
        this.textMarshaller = JsonBindingMarshaller.getInstance();
        this.binaryMarshaller = BinaryMarshaller.getInstance();
        this.mappers = new RowMappers();
    }

    @Override
    public void format() {
        if (checkSchemaExistence()) {
            return;
        }
        SqlScriptRunner runner = new SqlScriptRunner(getConnection());
        runner.runScript("create-schema.sql");
    }
    
    @Override
    public void clear() {
        execute(SqlCommand.DELETE_TASKS);
        execute(SqlCommand.DELETE_JOB_LINKS);
        execute(SqlCommand.DELETE_JOBS);
        execute(SqlCommand.DELETE_WORKFLOWS);
    }

    @Override
    public void addWorkflow(Workflow workflow) {
        addWorkflow((ManagedWorkflow)workflow);
    }

    @Override
    public long countWorkflows() {
        try (Query q = createQuery(SqlCommand.COUNT_WORKFLOWS)) {
            return q.queryForLong();
        }
    }

    @Override
    public long countJobs() {
        try (Query q = createQuery(SqlCommand.COUNT_JOBS)) {
            return q.queryForLong();
        }
    }

    @Override
    public long countJobsWithStatus(JobStatus status) {
        try (Query q = createQuery(SqlCommand.COUNT_JOBS_BY_STATUS)) {
            return q.setEnum(1, status).queryForLong();
        }
    }

    @Override
    public List<Job> findJobsByStatus(JobStatus status) {
        try (Query q = createQuery(SqlCommand.FIND_JOBS_BY_STATUS)) {
            q.setEnum(1, status);
            List<ManagedJob> jobs = q.queryForList(mappers::mapToJob);
            return jobs.stream().map(job->{
                job.setTasks(findTasks(job.getId()));
                return job;
            }).collect(Collectors.toList());
        }
    }

    @Override
    public Optional<Job> findFirstJobByStatus(JobStatus status) {
        try (Query q = createQuery(SqlCommand.FIND_FIRST_JOB_BY_STATUS)) {
            q.setEnum(1, status);
            return q.queryForObject(mappers::mapToJob).map(job->{
                job.setTasks(findTasks(job.getId()));
                return job;
            });
        }
    }
    
    @Override
    public Job findJobById(long jobId) {
        try (Query q = createQuery(SqlCommand.FIND_JOB_BY_ID)) {
            q.setLong(1, jobId);
            return q.queryForObject(mappers::mapToJob).get();
        }
    }
    
    @Override
    public JobStatus getJobStatus(long jobId) {
        try (Query q = createQuery(SqlCommand.FIND_JOB_STATUS_BY_ID)) {
            q.setLong(1, jobId);
            return q.queryForObject(rs->JobStatus.valueOf(rs.getString(1))).get();
        }
    }
    
    @Override
    public Workflow getWorkflow(long workflowId) {
        try (Query q = createQuery(SqlCommand.FIND_WORKFLOW_BY_ID)) {
            q.setLong(1, workflowId);
            return q.queryForObject(mappers::mapToWorkflow).get();
        }
    }
    
    @Override
    public List<JobLink> findLinksByTarget(ManagedJob target) {
        try (Query q = createQuery(SqlCommand.FIND_LINKS_BY_TARGET)) {
            q.setLong(1, target.getId());
            return q.queryForList(mappers.mappingToJobLink(target));
        }
    }
    
    @Override
    public List<Long> findNextJobs(Job job) {
        try (Query q = createQuery(SqlCommand.FIND_NEXT_JOBS)) {
            q.setLong(1, job.getId());
            return q.queryForList(rs->rs.getLong(1));
        }
    }
    
    @Override
    public void storeJob(Job job, JobStatus status, Map<String, Object> jobOutput, String[] standardOutput) {
        try (Query q = createQuery(SqlCommand.UPDATE_JOB)) {
            q.setEnum(1, status);
            q.setBytes(2, marshal(jobOutput));
            q.setString(3, marshalToString(standardOutput));
            q.setLong(4, job.getId());
            q.update();
        }
    }
    
    @Override
    public int updateJobStatusIfReady(long jobId) {
        try (Query q = createQuery(SqlCommand.UPDATE_JOB_STATUS_IF_READY)) {
            q.setLong(1, jobId);
            return q.update();
        }
    }
  
    // helper methods
    
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
        try (Query q = createQuery(SqlCommand.INSERT_WORKFLOW)) {
            q.setString(1, workflow.getName());
            q.setEnum(2, workflow.getStatus());
            long id = q.updateAndGenerateLong();
            workflow.setId(id);
            return id;
        }
    }

    private long insertJob(ManagedJob job, JobStatus status) {
        try (Query q = createQuery(SqlCommand.INSERT_JOB)) {
            q.setString(1, job.getName());
            q.setEnum(2, status);
            q.setBytes(3, marshal(job.getInputParameters()));
            q.setLong(4, job.getWorkdlowId());
            long jobId = q.updateAndGenerateLong();
            job.setId(jobId);
            return jobId;
        }
    }
    
    private void insertJobLink(JobLink link) {
        try (Query q = createQuery(SqlCommand.INSERT_JOB_LINK)) {
            q.setLong(1, link.getSource().getId());
            q.setLong(2, link.getTarget().getId());
            q.setString(3, link.getMapper().getClass().getName());
            q.setBytes(4, marshal(link.getMapper()));
            q.update();
        }
    }
    
    private void insertTask(Task task, long jobId, int sequenceNumber) {
        try (Query q = createQuery(SqlCommand.INSERT_TASK)) {
            q.setLong(1, jobId);
            q.setInt(2, sequenceNumber);
            q.setString(3, task.getClass().getName());
            String params = marshalToString(task);
            q.setString(4, params);
            q.update();
        }
    }

    private Query createQuery(SqlCommand command) {
        try {
            return command.createQuery(getConnection());
        } catch (SQLException e) {
            throw new WorkflowException(REPOSITORY_ACCESS_ERROR_OCCURRED.toString(), e);
        }
    }
    
    private void execute(SqlCommand command) {
        try (Query q = createQuery(command)) {
            q.execute();
        }
    }
    
    private boolean checkSchemaExistence() {
        try {
            DatabaseMetaData meta = getConnection().getMetaData();
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

    private List<Task> findTasks(long jobId) {
        try (Query q = createQuery(SqlCommand.FIND_TASK)) {
            q.setLong(1, jobId);
            return q.queryForList(mappers::mapToTask);
        }
    }
    
    private byte[] marshal(Object object) {
        return this.binaryMarshaller.marshal(object);
    }
    
    private String marshalToString(Object object) {
        return this.textMarshaller.marshal(object);
    }
}
