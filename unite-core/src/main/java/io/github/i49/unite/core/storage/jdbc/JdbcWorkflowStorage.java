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

import static io.github.i49.unite.core.message.Message.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import javax.sql.DataSource;

import io.github.i49.unite.api.base.WorkflowException;
import io.github.i49.unite.api.workflow.Job;
import io.github.i49.unite.api.workflow.JobStatus;
import io.github.i49.unite.api.workflow.Workflow;
import io.github.i49.unite.core.storage.WorkflowStorage;
import io.github.i49.unite.core.workflow.JobLink;
import io.github.i49.unite.core.workflow.ManagedJob;

/**
 * The storage which connects to JDBC drivers.
 * 
 * @author i49
 */
public class JdbcWorkflowStorage implements WorkflowStorage {
    
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(JdbcWorkflowStorage.class.getName());
 
    private final DataSource dataSource;
    
    public JdbcWorkflowStorage(DataSource dataSource) {
        this.dataSource = dataSource;
        format();
    }

    @Override
    public void format() {
        try (ActiveWorkflowStorage s = connect()) {
            s.format();
        }
    }
    
    @Override
    public void clear() {
        try (ActiveWorkflowStorage s = connect()) {
            s.clear();
        }
    }

    @Override
    public void addWorkflow(Workflow workflow) {
        try (ActiveWorkflowStorage s = connect()) {
            s.addWorkflow(workflow);
        }
    }

    @Override
    public long countWorkflows() {
        try (ActiveWorkflowStorage s = connect()) {
            return s.countWorkflows();
        }
    }

    @Override
    public long countJobs() {
        try (ActiveWorkflowStorage s = connect()) {
            return s.countJobs();
        }
    }

    @Override
    public long countJobsWithStatus(JobStatus status) {
        try (ActiveWorkflowStorage s = connect()) {
            return s.countJobsWithStatus(status);
        }
    }

    @Override
    public List<Job> findJobsByStatus(JobStatus status) {
        try (ActiveWorkflowStorage s = connect()) {
            return s.findJobsByStatus(status);
        }
    }

    @Override
    public Optional<Job> findFirstJobByStatus(JobStatus status) {
        try (ActiveWorkflowStorage s = connect()) {
            return s.findFirstJobByStatus(status);
        }
    }
    
    @Override
    public Job findJobById(long jobId) {
        try (ActiveWorkflowStorage s = connect()) {
            return s.findJobById(jobId);
        }
    }
    
    @Override
    public JobStatus getJobStatus(long jobId) {
        try (ActiveWorkflowStorage s = connect()) {
            return s.getJobStatus(jobId);
        }
    }
    
    @Override
    public Workflow getWorkflow(long workflowId) {
        try (ActiveWorkflowStorage s = connect()) {
            return s.getWorkflow(workflowId);
        }
    }
    
    @Override
    public List<JobLink> findLinksByTarget(ManagedJob target) {
        try (ActiveWorkflowStorage s = connect()) {
            return s.findLinksByTarget(target);
        }
    }
    
    @Override
    public List<Long> findNextJobs(Job job) {
        try (ActiveWorkflowStorage s = connect()) {
            return s.findNextJobs(job);
        }
    }
    
    @Override
    public void storeJob(Job job, JobStatus status, Map<String, Object> jobOutput, String[] standardOutput) {
        try (ActiveWorkflowStorage s = connect()) {
            s.storeJob(job, status, jobOutput, standardOutput);
        }
    }
    
    @Override
    public int updateJobStatusIfReady(long jobId) {
        try (ActiveWorkflowStorage s = connect()) {
            return s.updateJobStatusIfReady(jobId);
        }
    }
  
    // helper methods
    
    /**
     * Connects to the data source.
     */
    private ActiveWorkflowStorage connect() {
        try {
            Connection connection = this.dataSource.getConnection();
            return new ActiveWorkflowStorage(connection);
        } catch (SQLException e) {
            throw new WorkflowException(REPOSITORY_ACCESS_ERROR_OCCURRED.toString(), e);
        }
    }
}
