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

package io.github.i49.unite.core.repository;

import java.util.List;
import java.util.Optional;

import io.github.i49.unite.api.repository.WorkflowRepository;
import io.github.i49.unite.api.workflow.Job;
import io.github.i49.unite.api.workflow.JobStatus;
import io.github.i49.unite.api.workflow.Workflow;
import io.github.i49.unite.core.storage.WorkflowStorage;

/**
 * The default implementation of {@link WorkdlowRepository}.
 * 
 * @author i49
 */
public class DefaultWorkflowRepository implements WorkflowRepository {
    
    private final WorkflowStorage storage;
    
    public DefaultWorkflowRepository(WorkflowStorage storage) {
        this.storage = storage;
    }

    @Override
    public void close() {
        storage.close();
    }

    @Override
    public void clear() {
        storage.clear();
    }

    @Override
    public void addWorkflow(Workflow workflow) {
        storage.addWorkflow(workflow);
    }

    @Override
    public long countWorkflows() {
        return storage.countWorkflows();
    }

    @Override
    public long countJobs() {
        return storage.countJobs();
    }

    @Override
    public long countJobsWithStatus(JobStatus status) {
        return storage.countJobsWithStatus(status);
    }

    @Override
    public Job findJobById(long id) {
        return storage.findJobById(id);
    }

    @Override
    public List<Job> findJobsByStatus(JobStatus status) {
        return storage.findJobsByStatus(status);
    }

    @Override
    public Optional<Job> findFirstJobByStatus(JobStatus status) {
        return storage.findFirstJobByStatus(status);
    }

    @Override
    public JobStatus getJobStatus(long id) {
        return storage.getJobStatus(id);
    }

    @Override
    public Workflow getWorkflow(long id) {
        return storage.getWorkflow(id);
    }
}
