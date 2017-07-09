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

import static com.github.i49.quantumleap.core.common.Preconditions.checkNotNull;
import static com.github.i49.quantumleap.core.common.Preconditions.checkRealType;

import javax.sql.DataSource;

import com.github.i49.quantumleap.api.tasks.TaskFactory;
import com.github.i49.quantumleap.api.workflow.JobBuilder;
import com.github.i49.quantumleap.api.workflow.ParameterSetMapperFactory;
import com.github.i49.quantumleap.api.workflow.RunnerConfiguration;
import com.github.i49.quantumleap.api.workflow.WorkflowBuilder;
import com.github.i49.quantumleap.api.workflow.WorkflowEngine;
import com.github.i49.quantumleap.api.workflow.WorkflowRepository;
import com.github.i49.quantumleap.api.workflow.WorkflowRunner;
import com.github.i49.quantumleap.core.mappers.DefaultParameterSetMapperFactory;
import com.github.i49.quantumleap.core.repository.EnhancedRepository;
import com.github.i49.quantumleap.core.repository.JdbcWorkflowRepository;
import com.github.i49.quantumleap.core.repository.SimpleDataSource;
import com.github.i49.quantumleap.core.runner.SimpleRunnerConfiguration;
import com.github.i49.quantumleap.core.runner.SerialWorkflowRunner;
import com.github.i49.quantumleap.core.tasks.DefaultTaskFactory;
import com.github.i49.quantumleap.core.workflow.WorkflowFactory;

/**
 * The workflow engine which is shared by all threads.
 */
public class SharedWorkflowEngine implements WorkflowEngine {

    private static final String DEFAULT_DATASOURCE_URL = "jdbc:hsqldb:mem:workflowdb;shutdown=true";

    private final WorkflowFactory workflowFactory;
    private final TaskFactory taskFactory;
    private final ParameterSetMapperFactory parameterSetMapperFactory;

    public SharedWorkflowEngine() {
        this.workflowFactory = WorkflowFactory.getInstance();
        this.taskFactory = new DefaultTaskFactory();
        this.parameterSetMapperFactory = new DefaultParameterSetMapperFactory();
    }

    @Override
    public WorkflowRepository createRepository() {
        return new JdbcWorkflowRepository(createDefaultDataSource(), this.workflowFactory);
    }

    @Override
    public WorkflowBuilder createWorkflowBuilder(String name) {
        checkNotNull(name, "name");
        return workflowFactory.createWorkflowBuilder(name);
    }

    @Override
    public JobBuilder createJobBuilder(String name) {
        checkNotNull(name, "name");
        return workflowFactory.createJobBuilder(name);
    }

    @Override
    public RunnerConfiguration createRunnerConfiguration() {
        return new SimpleRunnerConfiguration();
    }

    @Override
    public WorkflowRunner createRunner(WorkflowRepository repository) {
        return createRunner(repository, createRunnerConfiguration());
    }
    
    @Override
    public WorkflowRunner createRunner(WorkflowRepository repository, RunnerConfiguration configuration) {
        checkNotNull(repository, "repository");
        checkNotNull(configuration, "configuration");
        EnhancedRepository enhanced = checkRealType(repository, EnhancedRepository.class, "repository"); 
        return new SerialWorkflowRunner(enhanced, configuration);
    }
    
    @Override
    public ParameterSetMapperFactory getParameterSetMapperFactory() {
        return parameterSetMapperFactory;
    }

    @Override
    public TaskFactory getTaskFactory() {
        return taskFactory;
    }

    private static DataSource createDefaultDataSource() {
        return SimpleDataSource.at(DEFAULT_DATASOURCE_URL, "SA", "");
    }
}
