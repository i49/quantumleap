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
package io.github.i49.unite.core.service;

import io.github.i49.unite.api.tasks.TaskFactory;
import io.github.i49.unite.api.workflow.JobBuilder;
import io.github.i49.unite.api.workflow.ParameterSetMapperFactory;
import io.github.i49.unite.api.workflow.RunnerConfiguration;
import io.github.i49.unite.api.workflow.WorkflowBuilder;
import io.github.i49.unite.api.workflow.WorkflowEngine;
import io.github.i49.unite.api.workflow.WorkflowRepository;
import io.github.i49.unite.api.workflow.WorkflowRunner;

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
    public WorkflowBuilder createWorkflowBuilder(String name) {
        return singleton.createWorkflowBuilder(name);
    }

    @Override
    public JobBuilder createJobBuilder(String name) {
        return singleton.createJobBuilder(name);
    }

    @Override
    public RunnerConfiguration createRunnerConfiguration() {
        return singleton.createRunnerConfiguration();
    }

    @Override
    public WorkflowRunner createRunner(WorkflowRepository repository) {
        return singleton.createRunner(repository);
    }

    @Override
    public WorkflowRunner createRunner(WorkflowRepository repository, RunnerConfiguration configuration) {
        return singleton.createRunner(repository, configuration);
    }

    @Override
    public ParameterSetMapperFactory getParameterSetMapperFactory() {
        return singleton.getParameterSetMapperFactory();
    }
 
    @Override
    public TaskFactory getTaskFactory() {
        return singleton.getTaskFactory();
    }
}
