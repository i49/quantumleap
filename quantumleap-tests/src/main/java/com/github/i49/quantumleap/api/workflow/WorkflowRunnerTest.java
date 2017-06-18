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
    private WorkflowRunner runner;

    @BeforeClass
    public static void setUpOnce() {
        engine = WorkflowEngine.get();
        repository = engine.createRepository();
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
        runner = engine.buildRunner(repository).get();
    }

    @Test
    public void runSingle_shouldRunSingleJob() {
        TaskFactory factory = engine.getTaskFactory();
        Task task1 = factory.createEchoTask("Hello");
        Task task2 = factory.createEchoTask("World");
        Job job1 = engine.buildJob("job1").start(task1).next(task2).get();
        Workflow workflow = engine.buildWorkflow("workflow1").jobs(job1).get();
        repository.addWorkflow(workflow);

        assertThat(repository.countJobsByStatus(JobStatus.READY)).isEqualTo(1);
        runner.runSingle();
        assertThat(repository.countJobsByStatus(JobStatus.READY)).isEqualTo(0);
    }
}
