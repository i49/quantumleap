/* 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
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
package com.github.i49.quantumleap.core.repository;

import static com.github.i49.quantumleap.core.common.Preconditions.checkNotNull;
import static com.github.i49.quantumleap.core.common.Preconditions.checkRealType;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.sql.DataSource;

import com.github.i49.quantumleap.api.base.WorkflowException;
import com.github.i49.quantumleap.api.tasks.Task;
import com.github.i49.quantumleap.api.workflow.Job;
import com.github.i49.quantumleap.api.workflow.JobStatus;
import com.github.i49.quantumleap.api.workflow.ParameterSetMapper;
import com.github.i49.quantumleap.api.workflow.Workflow;
import com.github.i49.quantumleap.core.workflow.JobLink;
import com.github.i49.quantumleap.core.workflow.ManagedJob;
import com.github.i49.quantumleap.core.workflow.ManagedJobBuilder;
import com.github.i49.quantumleap.core.workflow.ManagedWorkflow;
import com.github.i49.quantumleap.core.workflow.WorkflowFactory;

/**
 * A repository which can be manipulated by JDBC interface.
 */
public class JdbcWorkflowRepository implements EnhancedRepository {

    private final Connection connection;
    private final Map<SqlCommand, Query> queries;

    private final Marshaller<String> textMarshaller;
    private final Marshaller<byte[]> binaryMarshaller;
    
    private final WorkflowFactory workflowFactory;

    public JdbcWorkflowRepository(DataSource dataSource, WorkflowFactory workflowFactory) {
        this.textMarshaller = new JsonBindingMarshaller();
        this.binaryMarshaller = new BinaryMarshaller();
        this.workflowFactory = workflowFactory;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            createSchema(connection);
            this.queries = prepareAllQueries(connection);
            this.connection = connection;
        } catch (WorkflowException e) {
            closeConnectionIgnoringError(connection);
            throw e;
        } catch (Exception e) {
            closeConnectionIgnoringError(connection);
            // TODO: add message
            throw new WorkflowException("", e);
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
    public void close() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            // TODO: add message
            throw new WorkflowException("", e);
        }
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
        return q.queryForList(this::mapToJobWithTasks);
    }

    @Override
    public Optional<Job> findFirstJobByStatus(JobStatus status) {
        Query q = getQuery(SqlCommand.FIND_FIRST_JOB_BY_STATUS);
        q.setEnum(1, status);
        return q.queryForObject(this::mapToJobWithTasks);
    }
    
    @Override
    public Job findJobById(long jobId) {
        Query q = getQuery(SqlCommand.FIND_JOB_BY_ID);
        q.setLong(1, jobId);
        return q.queryForObject(this::mapToJob).get();
    }
    
    @Override
    public JobStatus getJobStatus(long jobId) {
        Query q = getQuery(SqlCommand.FIND_JOB_STATUS_BY_ID);
        q.setLong(1, jobId);
        return q.queryForObject(rs->JobStatus.valueOf(rs.getString(1))).get();
    }
    
    /* EnhancedWorkflowRepository interface */
  
    @Override
    public List<JobLink> findLinksByTarget(ManagedJob target) {
        Query q = getQuery(SqlCommand.FIND_LINKS_BY_TARGET);
        q.setLong(1, target.getId());
        return q.queryForList(rs->{
            ManagedJob source = mapToJob(rs);
            byte[] bytes = rs.getBytes(9); 
            ParameterSetMapper mapper = unmarshal(bytes, ParameterSetMapper.class);
            return workflowFactory.createJobLink(source, target, mapper);
        });
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
    
    private void closeConnectionIgnoringError(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            // TODO: logging
        }
    }

    private void addWorkflow(ManagedWorkflow workflow) {
        long workflowId = insertWorkflow(workflow);
        for (ManagedJob job: workflow.getManagedJobs()) {
            addJob(job, workflowId, workflow.getDependenciesOf(job));
        }
        for (JobLink link: workflow.getJobLinks()) {
            insertJobLink(link);
        }
    }

    private void addJob(ManagedJob job, long workflowId, Set<ManagedJob> dependencies) {
        JobStatus status = dependencies.isEmpty() ? JobStatus.READY : JobStatus.WAITING;
        long jobId = insertJob(job, status, workflowId);
        int sequence = 0;
        for (Task task : job.getTasks()) {
            insertTask(task, jobId, sequence++);
        }
    }
    
    private long insertWorkflow(ManagedWorkflow workflow) {
        Query q = getQuery(SqlCommand.INSERT_WORKFLOW);
        q.setString(1, workflow.getName());
        long id = q.updateAndGenerateLong();
        workflow.setId(id);
        return id;
    }

    private long insertJob(ManagedJob job, JobStatus status, long workflowId) {
        Query q = getQuery(SqlCommand.INSERT_JOB);
        q.setString(1, job.getName());
        q.setEnum(2, status);
        q.setBytes(3, marshal(job.getInputParameters()));
        q.setLong(4, workflowId);
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

    private void createSchema(Connection connection) throws IOException, SQLException {
        if (checkSchemaExistence(connection)) {
            return;
        }
        SqlScriptRunner runner = new SqlScriptRunner(connection);
        runner.runScript("create-schema.sql");
    }
    
    private boolean checkSchemaExistence(Connection connection) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        try (ResultSet resultSet = meta.getTables(null, null, "WORKFLOW", null)) {
            if (resultSet.next()) {
                return true;
            }
        }
        return false;
    }

    private Map<SqlCommand, Query> prepareAllQueries(Connection connection) throws SQLException {
        Map<SqlCommand, Query> map = new EnumMap<SqlCommand, Query>(SqlCommand.class);
        for (SqlCommand c : SqlCommand.values()) {
            map.put(c, c.getQuery(connection));
        }
        return map;
    }
    
    private ManagedJob mapToJob(ResultSet resultSet) throws SQLException {
        return buildJob(resultSet).get();
    }
    
    private Job mapToJobWithTasks(ResultSet resultSet) throws SQLException {
        long jobId = resultSet.getLong(1);
        ManagedJobBuilder builder = buildJob(resultSet);
        buildTasks(jobId, builder);
        return builder.get();
    }
    
    private ManagedJobBuilder buildJob(ResultSet resultSet) throws SQLException {
        final long id = resultSet.getLong(1);
        final String name = resultSet.getString(2);
        final JobStatus status = JobStatus.valueOf(resultSet.getString(3));
        @SuppressWarnings("unchecked")
        final Map<String, Object> jobParameters = unmarshal(resultSet.getBytes(4), Map.class);
        @SuppressWarnings("unchecked")
        final Map<String, Object> jobOutput = unmarshal(resultSet.getBytes(5), Map.class);
        final String standardOutput = resultSet.getString(6);
        ManagedJobBuilder builder = this.workflowFactory.createJobBuilder(name);
        builder.jobId(id);
        if (jobParameters != null) {
            builder.input(jobParameters);
        }
        if (jobOutput != null) {
            builder.jobOutput(jobOutput);
        }
        builder.status(status);
        if (standardOutput != null) {
            builder.standardOutput(unmarshalFromString(standardOutput, String[].class));
        }
        return builder;
    }
    
    private void buildTasks(long jobId, ManagedJobBuilder builder) throws SQLException {
        Query q = getQuery(SqlCommand.FIND_TASK);
        q.setLong(1, jobId);
        List<Task> tasks = q.queryForList(this::mapToTask);
        builder.tasks(tasks.toArray(new Task[0]));
    }

    private Task mapToTask(ResultSet rs) throws SQLException {
        final String className = rs.getString(3);
        final String params = rs.getString(4);
        try {
            Class<?> type = Class.forName(className);
            if (Task.class.isAssignableFrom(type)) {
                Object task = unmarshalFromString(params, type);
                return (Task) task;
            } else {
                // TODO
                throw new WorkflowException("");
            }
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            throw new WorkflowException("", e);
        }
    }
    
    private JobLink mapToLink(ResultSet rs) {
        // TODO:
        return null;
    }
    
    private byte[] marshal(Optional<?> object) {
        if (object.isPresent()) {
            return this.binaryMarshaller.marshal(object.get());
        } else {
            return null;
        }
    }
    
    private byte[] marshal(Object object) {
        return this.binaryMarshaller.marshal(object);
    }
    
    private <T> T unmarshal(byte[] bytes, Class<T> type) {
        return this.binaryMarshaller.unmarshal(bytes, type);
    }
    
    private String marshalToString(Object object) {
        return this.textMarshaller.marshal(object);
    }
    
    private String marshalToString(Optional<?> object) {
        if (object.isPresent()) {
            return this.textMarshaller.marshal(object.get());
        } else {
            return null;
        }
    }
    
    private <T> T unmarshalFromString(String str, Class<? extends T> type) {
        return this.textMarshaller.unmarshal(str, type);
    }
}
