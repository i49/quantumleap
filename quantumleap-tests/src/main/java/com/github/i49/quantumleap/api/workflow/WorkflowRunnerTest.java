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
package com.github.i49.quantumleap.api.workflow;

import static org.assertj.core.api.Assertions.*;

import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.i49.quantumleap.api.tasks.Task;
import com.github.i49.quantumleap.api.tasks.TaskFactory;
import com.github.i49.quantumleap.api.workflow.Job;
import com.github.i49.quantumleap.api.workflow.JobStatus;
import com.github.i49.quantumleap.api.workflow.Workflow;
import com.github.i49.quantumleap.api.workflow.WorkflowEngine;
import com.github.i49.quantumleap.api.workflow.WorkflowRepository;
import com.github.i49.quantumleap.api.workflow.WorkflowRunner;

/**
 * Unit test of {@link WorkflowRunner}.
 */
public class WorkflowRunnerTest {

    private static WorkflowEngine engine;
    private static WorkflowRepository repository;
    private static TaskFactory taskFactory;
    private WorkflowRunner runner;

    @BeforeClass
    public static void setUpOnce() {
        engine = WorkflowEngine.get();
        repository = engine.createRepository();
        taskFactory = engine.getTaskFactory();
    }

    @AfterClass
    public static void tearDown() {
        if (repository != null) {
            repository.close();
        }
    }

    @Before
    public void setUp() {
        repository.clear();
        RunnerConfiguration config = engine.createRunnerConfiguration()
                .withDirectory(Paths.get("target/work"));
        runner = engine.createRunner(repository, config);
    }

    @Test
    public void runSingle_shouldRunSingleJob() {
        Task task1 = taskFactory.createEchoTask("Hello");
        Task task2 = taskFactory.createEchoTask("World");
        Job job1 = engine.buildJob("job1").tasks(task1, task2).get();
        Workflow workflow = engine.buildWorkflow("workflow1").jobs(job1).get();
        repository.addWorkflow(workflow);

        assertThat(repository.countJobsWithStatus(JobStatus.READY)).isEqualTo(1);
        runner.runSingle();
        assertThat(repository.countJobsWithStatus(JobStatus.READY)).isEqualTo(0);
    }
    
    @Test
    public void runSingle_shouldRunJobWithDependencies() {
        Job job1 = engine.buildJob("job1")
                .tasks(taskFactory.createEchoTask("Running job1"))
                .get();
        Job job2 = engine.buildJob("job2")
                .tasks(taskFactory.createEchoTask("Running job2"))
                .dependOn(job1)
                .get();
        
        Workflow workflow1 = engine.buildWorkflow("workflow1")
                .jobs(job1, job2)
                .get();
        
        repository.addWorkflow(workflow1);
        
        assertThat(repository.getJobStatus(job1.getId())).hasValue(JobStatus.READY);
        assertThat(repository.getJobStatus(job2.getId())).hasValue(JobStatus.WAITING);

        runner.runSingle();
        assertThat(repository.getJobStatus(job1.getId())).hasValue(JobStatus.COMPLETED);
        assertThat(repository.getJobStatus(job2.getId())).hasValue(JobStatus.READY);
       
        runner.runSingle();
        assertThat(repository.getJobStatus(job1.getId())).hasValue(JobStatus.COMPLETED);
        assertThat(repository.getJobStatus(job2.getId())).hasValue(JobStatus.COMPLETED);
    }

    @Test
    public void runSingle_shouldRunJobInDiamondDependencies() {
        Job job1 = engine.buildJob("job1")
                .tasks(taskFactory.createEchoTask("Running job1"))
                .get();
        Job job2 = engine.buildJob("job2")
                .tasks(taskFactory.createEchoTask("Running job2"))
                .dependOn(job1)
                .get();
        Job job3 = engine.buildJob("job3")
                .tasks(taskFactory.createEchoTask("Running job3"))
                .dependOn(job1)
                .get();
        Job job4 = engine.buildJob("job4")
                .tasks(taskFactory.createEchoTask("Running job4"))
                .dependOn(job2, job3)
                .get();
        
        Workflow workflow1 = engine.buildWorkflow("workflow1")
                .jobs(job1, job2, job3, job4)
                .get();
        
        repository.addWorkflow(workflow1);
        
        assertThat(repository.getJobStatus(job1.getId())).hasValue(JobStatus.READY);
        assertThat(repository.getJobStatus(job2.getId())).hasValue(JobStatus.WAITING);
        assertThat(repository.getJobStatus(job3.getId())).hasValue(JobStatus.WAITING);
        assertThat(repository.getJobStatus(job4.getId())).hasValue(JobStatus.WAITING);

        runner.runSingle();
        assertThat(repository.getJobStatus(job1.getId())).hasValue(JobStatus.COMPLETED);
        assertThat(repository.getJobStatus(job2.getId())).hasValue(JobStatus.READY);
        assertThat(repository.getJobStatus(job3.getId())).hasValue(JobStatus.READY);
        assertThat(repository.getJobStatus(job4.getId())).hasValue(JobStatus.WAITING);
       
        runner.runSingle();
        runner.runSingle();
        assertThat(repository.getJobStatus(job1.getId())).hasValue(JobStatus.COMPLETED);
        assertThat(repository.getJobStatus(job2.getId())).hasValue(JobStatus.COMPLETED);
        assertThat(repository.getJobStatus(job3.getId())).hasValue(JobStatus.COMPLETED);
        assertThat(repository.getJobStatus(job4.getId())).hasValue(JobStatus.READY);

        runner.runSingle();
        assertThat(repository.getJobStatus(job1.getId())).hasValue(JobStatus.COMPLETED);
        assertThat(repository.getJobStatus(job2.getId())).hasValue(JobStatus.COMPLETED);
        assertThat(repository.getJobStatus(job3.getId())).hasValue(JobStatus.COMPLETED);
        assertThat(repository.getJobStatus(job4.getId())).hasValue(JobStatus.COMPLETED);
    }
}
