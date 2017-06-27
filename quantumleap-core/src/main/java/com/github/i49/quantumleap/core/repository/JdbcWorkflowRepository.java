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

import static com.github.i49.quantumleap.core.common.Message.*;
import static com.github.i49.quantumleap.core.common.Preconditions.checkNotNull;
import static com.github.i49.quantumleap.core.common.Preconditions.checkRealType;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import com.github.i49.quantumleap.api.tasks.Task;
import com.github.i49.quantumleap.api.workflow.Job;
import com.github.i49.quantumleap.api.workflow.JobStatus;
import com.github.i49.quantumleap.api.workflow.Workflow;
import com.github.i49.quantumleap.api.workflow.WorkflowException;
import com.github.i49.quantumleap.core.workflow.ManagedJob;
import com.github.i49.quantumleap.core.workflow.ManagedJobBuilder;
import com.github.i49.quantumleap.core.workflow.ManagedWorkflow;
import com.github.i49.quantumleap.core.workflow.WorkflowFactory;

/**
 * A repository which can be manipulated by JDBC interface.
 */
public class JdbcWorkflowRepository implements EnhancedRepository {

    private final Connection connection;
    private final Map<SqlCommand, PreparedStatement> statements;

    private final JsonMarshaller marshaller;
    private final WorkflowFactory workflowFactory;

    public JdbcWorkflowRepository(DataSource dataSource, WorkflowFactory workflowFactory) {
        this.marshaller = new JsonbMarshaller();
        this.workflowFactory = workflowFactory;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            createSchema(connection);
            this.statements = prepareStatements(connection);
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
        try {
            getStatement(SqlCommand.DELETE_TASKS).execute();
            getStatement(SqlCommand.DELETE_JOB_DEPENDENCY).execute();
            getStatement(SqlCommand.DELETE_JOBS).execute();
            getStatement(SqlCommand.DELETE_WORKFLOWS).execute();
        } catch (SQLException e) {
            throw new WorkflowException(REPOSITORY_FAILED_TO_CLEAR.toString(), e);
        }
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
        PreparedStatement s = getStatement(SqlCommand.COUNT_WORKFLOWS);
        return countHits(s);
    }

    @Override
    public long countJobs() {
        PreparedStatement s = getStatement(SqlCommand.COUNT_JOBS);
        return countHits(s);
    }

    @Override
    public long countJobsWithStatus(JobStatus status) {
        PreparedStatement s = getStatement(SqlCommand.COUNT_JOBS_BY_STATUS);
        try {
            s.setString(1, status.name());
        } catch (SQLException e) {
            // TODO: add message
            throw new WorkflowException("", e);
        }
        return countHits(s);
    }

    @Override
    public List<Job> findJobsByStatus(JobStatus status) {
        PreparedStatement s = getStatement(SqlCommand.FIND_JOBS_BY_STATUS);
        try {
            s.setString(1, status.name());
            try (ResultSet rs = s.executeQuery()) {
                List<Job> jobs = new ArrayList<>();
                while (rs.next()) {
                    jobs.add(mapToJobWithTasks(rs));
                }
                return jobs;
            }
        } catch (SQLException e) {
            // TODO: add message
            throw new WorkflowException("", e);
        }
    }

    @Override
    public Optional<Job> findFirstJobByStatus(JobStatus status) {
        PreparedStatement s = getStatement(SqlCommand.FIND_FIRST_JOB_BY_STATUS);
        try {
            s.setString(1, status.name());
            try (ResultSet rs = s.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToJobWithTasks(rs));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            // TODO: add message
            throw new WorkflowException("", e);
        }
    }
    
    @Override
    public Optional<Job> findJobById(long jobId) {
        PreparedStatement s = getStatement(SqlCommand.FIND_JOB_BY_ID);
        try {
            s.setLong(1, jobId);
            try (ResultSet resultSet = s.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapToJob(resultSet));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            // TODO: add message
            throw new WorkflowException("", e);
        }
    }
    
    @Override
    public Optional<JobStatus> getJobStatus(long jobId) {
        PreparedStatement s = getStatement(SqlCommand.FIND_JOB_STATUS_BY_ID);
        try {
            s.setLong(1, jobId);
            try (ResultSet rs = s.executeQuery()) {
                if (rs.next()) {
                    JobStatus status = JobStatus.valueOf(rs.getString(1));
                    return Optional.of(status);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            // TODO: add message
            throw new WorkflowException("", e);
        }
    }
    
    /* EnhancedWorkflowRepository interface */
   
    @Override
    public List<Long> findDependants(Job job) {
        PreparedStatement s = getStatement(SqlCommand.FIND_DEPENDANT_JOBS);
        try {
            s.setLong(1, job.getId());
            List<Long> dependants = new ArrayList<>();
            try (ResultSet rs = s.executeQuery()) {
                while (rs.next()) {
                    dependants.add(rs.getLong(1));
                }
            }
            return dependants;
        } catch (SQLException e) {
            // TODO: add message
            throw new WorkflowException("", e);
        }
    }
    
    @Override
    public void storeJob(Job job, JobStatus status, String[] standardOutput) {
        PreparedStatement s = getStatement(SqlCommand.UPDATE_JOB);
        try {
            s.setString(1, status.name());
            s.setString(2, marshalToString(standardOutput));
            s.setLong(3, job.getId());
            s.executeUpdate();
        } catch (SQLException e) {
            // TODO: add message
            throw new WorkflowException("", e);
        }
    }
    
    @Override
    public int updateJobStatusIfReady(long jobId) {
        PreparedStatement s = getStatement(SqlCommand.UPDATE_JOB_STATUS_IF_READY);
        try {
            s.setLong(1, jobId);
            return s.executeUpdate();
        } catch (SQLException e) {
            // TODO: add message
            throw new WorkflowException("", e);
        }
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
        // TODO: reorder jobs
        for (ManagedJob job : workflow.getManagedJobs()) {
            addJob(job, workflowId);
        }
    }

    private void addJob(ManagedJob job, long workflowId) {
        JobStatus status = job.hasDependencies() ? JobStatus.WAITING : JobStatus.READY;
        long jobId = insertJob(job, status, workflowId);
        int sequence = 0;
        for (Task task : job.getTasks()) {
            insertTask(task, jobId, sequence++);
        }
    }

    private long insertWorkflow(ManagedWorkflow workflow) {
        try {
            PreparedStatement s = getStatement(SqlCommand.INSERT_WORKFLOW);
            s.setString(1, workflow.getName());
            s.executeUpdate();
            long id = getGeneratedKey(s);
            workflow.setId(id);
            return id;
        } catch (SQLException e) {
            // TODO: add message
            throw new WorkflowException("", e);
        }
    }

    private long insertJob(ManagedJob job, JobStatus status, long workflowId) {
        try {
            PreparedStatement s = getStatement(SqlCommand.INSERT_JOB);
            s.setString(1, job.getName());
            s.setString(2, status.name());
            s.setString(3, marshalToString(job.getParameters()));
            s.setLong(4, workflowId);
            s.executeUpdate();
            long jobId = getGeneratedKey(s);
            job.setId(jobId);
            if (job.hasDependencies()) {
                insertJobDependencies(jobId, job.getDependencies());
            }
            return jobId;
        } catch (SQLException e) {
            // TODO: add message
            throw new WorkflowException("", e);
        }
    }
    
    private void insertJobDependencies(long jobId, Iterable<Job> dependencies) {
        PreparedStatement s = getStatement(SqlCommand.INSERT_JOB_DEPENDENCY);
        try {
            s.setLong(1, jobId);
            for (Job dependency: dependencies) {
                s.setLong(2, dependency.getId());
                s.executeUpdate();
            }
        } catch (SQLException e) {
            // TODO: add message
            throw new WorkflowException("", e);
        }
    }
    
    private void insertTask(Task task, long jobId, int sequenceNumber) {
        try {
            PreparedStatement s = getStatement(SqlCommand.INSERT_TASK);
            s.setLong(1, jobId);
            s.setInt(2, sequenceNumber);
            s.setString(3, task.getClass().getName());
            String params = marshalToString(task);
            s.setString(4, params);
            s.executeUpdate();
        } catch (SQLException e) {
            // TODO: add message
            throw new WorkflowException("", e);
        }
    }

    private long countHits(PreparedStatement statement) {
        try (ResultSet rs = statement.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            // TODO: add message
            throw new WorkflowException("", e);
        }
    }

    private PreparedStatement getStatement(SqlCommand command) {
        return statements.get(command);
    }

    private long getGeneratedKey(PreparedStatement statement) throws SQLException {
        try (ResultSet rs = statement.getGeneratedKeys()) {
            rs.next();
            return rs.getLong(1);
        }
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

    private Map<SqlCommand, PreparedStatement> prepareStatements(Connection connection) throws SQLException {
        Map<SqlCommand, PreparedStatement> map = new EnumMap<SqlCommand, PreparedStatement>(SqlCommand.class);
        for (SqlCommand c : SqlCommand.values()) {
            map.put(c, c.prepare(connection));
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
        final Map<String, Object> parameters = unmarshalFromString(resultSet.getString(4), HashMap.class);
        final String standardOutput = resultSet.getString(6);
        ManagedJobBuilder builder = this.workflowFactory.createJobBuilder(name);
        builder.jobId(id);
        builder.parameters(parameters);
        builder.status(status);
        if (standardOutput != null) {
            builder.standardOutput(unmarshalFromString(standardOutput, String[].class));
        }
        return builder;
    }
    
    private void buildTasks(long jobId, ManagedJobBuilder builder) throws SQLException {
        PreparedStatement s = getStatement(SqlCommand.FIND_TASK);
        s.setLong(1, jobId);
        List<Task> tasks = new ArrayList<>();
        try (ResultSet resultSet = s.executeQuery()) {
            while (resultSet.next()) {
                tasks.add(mapToTask(resultSet));
            }
        }
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
    
    private String marshalToString(Object object) {
        return this.marshaller.marshal(object);
    }
    
    private <T> T unmarshalFromString(String str, Class<? extends T> type) {
        return this.marshaller.unmarshal(str, type);
    }
}
