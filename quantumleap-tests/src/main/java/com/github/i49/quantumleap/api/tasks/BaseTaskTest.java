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
package com.github.i49.quantumleap.api.tasks;

import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.github.i49.quantumleap.api.workflow.Job;
import com.github.i49.quantumleap.api.workflow.RunnerConfiguration;
import com.github.i49.quantumleap.api.workflow.Workflow;
import com.github.i49.quantumleap.api.workflow.WorkflowEngine;
import com.github.i49.quantumleap.api.workflow.WorkflowRepository;
import com.github.i49.quantumleap.api.workflow.WorkflowRunner;

/**
 * The base class for task testing classes.
 */
public class BaseTaskTest {

    protected static WorkflowEngine engine;
    protected static TaskFactory factory;
    protected static WorkflowRepository repository;
    protected WorkflowRunner runner;

    @BeforeClass
    public static void setUpOnce() {
        engine = WorkflowEngine.get();
        factory = engine.getTaskFactory();
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
        RunnerConfiguration config = engine.createRunnerConfiguration()
                .withDirectory(Paths.get("target/work"));
        runner = engine.createRunner(repository, config);
    }

    protected Job runTask(Task task) {
        Job job = engine.createJobBuilder("job1").tasks(task).build();
        Workflow workflow = engine.createWorkflowBuilder("workflow1").jobs(job).build();
        repository.addWorkflow(workflow);
        runner.runSingle();
        return repository.findJobById(job.getId());
    }
}
