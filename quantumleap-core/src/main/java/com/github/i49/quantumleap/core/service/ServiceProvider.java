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
package com.github.i49.quantumleap.core.service;

import com.github.i49.quantumleap.api.tasks.TaskFactory;
import com.github.i49.quantumleap.api.workflow.JobBuilder;
import com.github.i49.quantumleap.api.workflow.WorkflowBuilder;
import com.github.i49.quantumleap.api.workflow.WorkflowEngine;
import com.github.i49.quantumleap.api.workflow.WorkflowRepository;
import com.github.i49.quantumleap.api.workflow.WorkflowRunner;
import com.github.i49.quantumleap.api.workflow.WorkflowRunnerBuilder;

/**
 * An implementation of {@link WorkflowEngine}. This class just delegates all
 * method invocations to {@link SharedWorkflowEngine}.
 */
public class ServiceProvider implements WorkflowEngine {

    /**
     * The singleton which is shared by all threads.
     */
    private static final SharedWorkflowEngine singleton = new SharedWorkflowEngine();

    @Override
    public WorkflowRepository createRepository() {
        return singleton.createRepository();
    }

    @Override
    public WorkflowBuilder buildWorkflow(String name) {
        return singleton.buildWorkflow(name);
    }

    @Override
    public JobBuilder buildJob(String name) {
        return singleton.buildJob(name);
    }

    @Override
    public WorkflowRunnerBuilder buildRunner(WorkflowRepository repository) {
        return singleton.buildRunner(repository);
    }

    @Override
    public WorkflowRunner createRunner(WorkflowRepository repository) {
        return singleton.createRunner(repository);
    }

    @Override
    public TaskFactory getTaskFactory() {
        return singleton.getTaskFactory();
    }
}
