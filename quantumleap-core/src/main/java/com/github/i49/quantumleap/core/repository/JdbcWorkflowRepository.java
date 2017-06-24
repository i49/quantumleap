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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.json.bind.Jsonb;
import javax.sql.DataSource;

import com.github.i49.quantumleap.api.tasks.Task;
import com.github.i49.quantumleap.api.workflow.Job;
import com.github.i49.quantumleap.api.workflow.JobBuilder;
import com.github.i49.quantumleap.api.workflow.JobStatus;
import com.github.i49.quantumleap.api.workflow.Workflow;
import com.github.i49.quantumleap.api.workflow.WorkflowException;
import com.github.i49.quantumleap.core.workflow.BasicJob;
import com.github.i49.quantumleap.core.workflow.BasicWorkflow;

/**
 * A repository which can be manipulated by JDBC interface.
 */
public class JdbcWorkflowRepository implements EnhancedWorkflowRepository {

    private final Connection connection;

    private final Map<SqlCommand, PreparedStatement> statements;
    private final Jsonb jsonb;

    public JdbcWorkflowRepository(DataSource dataSource) {
        this.jsonb = CustomJsonbBuilder.create();
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
        if (workflow instanceof BasicWorkflow) {
            addWorkflow((BasicWorkflow) workflow);
        } else {
            // TODO:
            throw new IllegalArgumentException("");
        }
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
                    jobs.add(mapToJob(rs));
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
                    return Optional.of(mapToJob(rs));
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
    public boolean updateJobStatus(Job job) {
        checkNotNull(job, "job");
        BasicJob basicJob = checkRealType(job, BasicJob.class, "job");
        PreparedStatement s = getStatement(SqlCommand.FIND_JOB_STATUS_BY_ID);
        try {
            s.setLong(1, job.getId());
            try (ResultSet rs = s.executeQuery()) {
                if (rs.next()) {
                    JobStatus status = JobStatus.valueOf(rs.getString(1));
                    basicJob.setStatus(status);
                    return true;
                } else {
                    return false;
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
    public void storeJobStatus(Job job) {
        storeJobStatus(job.getId(), job.getStatus());
    }
    
    @Override
    public void storeJobStatus(long jobId, JobStatus status) {
        PreparedStatement s = getStatement(SqlCommand.UPDATE_JOB_STATUS);
        try {
            s.setString(1, status.name());
            s.setLong(2, jobId);
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

    private void addWorkflow(BasicWorkflow workflow) {
        long workflowId = insertWorkflow(workflow);
        // TODO: reorder jobs
        for (Job job : workflow.getJobs()) {
            addJob((BasicJob) job, workflowId);
        }
    }

    private void addJob(BasicJob job, long workflowId) {
        JobStatus status = job.hasDependencies() ? JobStatus.WAITING : JobStatus.READY;
        job.setStatus(status);
        long jobId = insertJob(job, workflowId);
        int sequence = 0;
        for (Task task : job.getTasks()) {
            insertTask(task, jobId, sequence++);
        }
    }

    private long insertWorkflow(BasicWorkflow workflow) {
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

    private long insertJob(BasicJob job, long workflowId) {
        try {
            PreparedStatement s = getStatement(SqlCommand.INSERT_JOB);
            s.setString(1, job.getName());
            s.setString(2, job.getStatus().name());
            s.setLong(3, workflowId);
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
            String params = jsonb.toJson(task);
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
        try (ResultSet rs = meta.getTables(null, null, "WORKFLOW", null)) {
            if (rs.next()) {
                return true;
            }
        }
        return false;
    }

    private Map<SqlCommand, PreparedStatement> prepareStatements(Connection connection) throws SQLException {
        Map<SqlCommand, PreparedStatement> map = new EnumMap<SqlCommand, PreparedStatement>(SqlCommand.class);
        ;
        for (SqlCommand c : SqlCommand.values()) {
            map.put(c, c.prepare(connection));
        }
        return map;
    }

    private Job mapToJob(ResultSet rs) throws SQLException {
        final long id = rs.getLong(1);
        final String name = rs.getString(2);
        BasicJob.Builder b = new BasicJob.Builder(name);
        buildTasks(id, b);
        BasicJob job = b.get();
        job.setId(id);
        return job;
    }

    private void buildTasks(long jobId, JobBuilder builder) throws SQLException {
        PreparedStatement s = getStatement(SqlCommand.FIND_TASK);
        s.setLong(1, jobId);
        List<Task> tasks = new ArrayList<>();
        try (ResultSet rs = s.executeQuery()) {
            while (rs.next()) {
                tasks.add(mapToTask(rs));
            }
        }
        builder.tasks(tasks);
    }

    private Task mapToTask(ResultSet rs) throws SQLException {
        final String className = rs.getString(3);
        final String params = rs.getString(4);
        try {
            Class<?> type = Class.forName(className);
            if (Task.class.isAssignableFrom(type)) {
                Object task = this.jsonb.fromJson(params, type);
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
}
