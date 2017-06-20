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

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.i49.quantumleap.api.workflow.Job;
import com.github.i49.quantumleap.api.workflow.Workflow;
import com.github.i49.quantumleap.api.workflow.WorkflowEngine;
import com.github.i49.quantumleap.api.workflow.WorkflowRepository;
import com.github.i49.quantumleap.api.workflow.WorkflowRunner;

/**
 *
 */
public class ShellTaskTest {

    private static WorkflowEngine engine;
    private static TaskFactory factory;
    private static WorkflowRepository repository;
    private WorkflowRunner runner;

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
        runner = engine.buildRunner(repository).get();
    }

    @Test
    public void run_shouldExecuteGivenCommand() {
        Task task = factory.buildShellTask().commands("echo", "hello world", ">shelltask-command.txt").get();
        runTask(task);
    }
    
    @Test
    public void run_shouldExecuteGivenScript() {
        Path path = Paths.get("target/classes/hello.bat");
        Task task = factory.buildShellTask().script(path).get();
        runTask(task);
    }
    
    private void runTask(Task task) {
        Job job1 = engine.buildJob("job1").start(task).get();
        Workflow workflow = engine.buildWorkflow("workflow1").jobs(job1).get();
        repository.addWorkflow(workflow);
        runner.runSingle();
    }
}

