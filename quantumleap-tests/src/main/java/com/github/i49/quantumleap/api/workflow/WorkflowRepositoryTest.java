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
package com.github.i49.quantumleap.api.workflow;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.i49.quantumleap.api.workflow.Job;
import com.github.i49.quantumleap.api.workflow.JobStatus;
import com.github.i49.quantumleap.api.workflow.Workflow;
import com.github.i49.quantumleap.api.workflow.WorkflowEngine;
import com.github.i49.quantumleap.api.workflow.WorkflowRepository;

/**
 * Unit test of {@link WorkflowRepository}.
 */
public class WorkflowRepositoryTest {

    private final WorkflowEngine engine = WorkflowEngine.get();
    private WorkflowRepository repository;

    @Before
    public void setUp() {
        repository = engine.createRepository();
        repository.clear();
    }

    @After
    public void tearDown() {
        if (repository != null) {
            repository.close();
        }
    }

    @Test
    public void addWorkflow_shouldAddEmptyWorkflow() {
        Workflow workflow = this.engine.buildWorkflow("workflow1").get();
        assertThat(repository.countWorkflows()).isEqualTo(0);
        repository.addWorkflow(workflow);
        assertThat(repository.countWorkflows()).isEqualTo(1);
        assertThat(workflow.hasId());
    }

    @Test
    public void addWorkflow_shouldAddJobs() {
        Job job1 = this.engine.buildJob("job1").get();
        Job job2 = this.engine.buildJob("job2").get();
        Workflow workflow = this.engine.buildWorkflow("workflow1").jobs(job1, job2).get();
        repository.addWorkflow(workflow);

        assertThat(repository.countWorkflows()).isEqualTo(1);
        assertThat(repository.countJobs()).isEqualTo(2);
        assertThat(workflow.hasId());
        assertThat(job1.hasId());
        assertThat(job2.hasId());
    }

    @Test
    public void findJobsByStatus_shouldReturnReadyJobs() {
        Job job1 = this.engine.buildJob("job1").get();
        Job job2 = this.engine.buildJob("job2").get();
        Workflow workflow = this.engine.buildWorkflow("workflow1").jobs(job1, job2).get();
        repository.addWorkflow(workflow);

        List<Job> jobs = repository.findJobsByStatus(JobStatus.READY);
        assertThat(jobs).hasSize(2);
        assertThat(jobs).extracting(Job::getName).containsExactly("job1", "job2");
    }

    @Test
    public void findFirstJobByStatus_shouldReturnFirstReadyJob() {
        Job job1 = this.engine.buildJob("job1").get();
        Job job2 = this.engine.buildJob("job2").get();
        Workflow workflow = this.engine.buildWorkflow("workflow1").jobs(job1, job2).get();
        repository.addWorkflow(workflow);

        Optional<Job> job = repository.findFirstJobByStatus(JobStatus.READY);
        assertThat(job).isNotEmpty();
        assertThat(job.get().getName()).isEqualTo("job1");
    }
}
