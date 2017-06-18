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
import com.github.i49.quantumleap.api.workflow.WorkflowBuilder;
import com.github.i49.quantumleap.api.workflow.WorkflowEngine;
import com.github.i49.quantumleap.api.workflow.WorkflowRepository;
import com.github.i49.quantumleap.api.workflow.WorkflowRunner;
import com.github.i49.quantumleap.api.workflow.WorkflowRunnerBuilder;
import com.github.i49.quantumleap.core.repository.EnhancedWorkflowRepository;
import com.github.i49.quantumleap.core.repository.JdbcWorkflowRepository;
import com.github.i49.quantumleap.core.repository.SimpleDataSource;
import com.github.i49.quantumleap.core.tasks.DefaultTaskFactory;
import com.github.i49.quantumleap.core.workflow.BasicJob;
import com.github.i49.quantumleap.core.workflow.BasicWorkflow;
import com.github.i49.quantumleap.core.workflow.SerialWorkflowRunner;

/**
 * The workflow engine which is shared by all threads.
 */
public class SharedWorkflowEngine implements WorkflowEngine {

    private static final String DEFAULT_DATASOURCE_URL = "jdbc:hsqldb:mem:workflowdb;shutdown=true";

    private final TaskFactory taskFactory;

    public SharedWorkflowEngine() {
        this.taskFactory = new DefaultTaskFactory();
    }

    @Override
    public WorkflowRepository createRepository() {
        return new JdbcWorkflowRepository(createDefaultDataSource());
    }

    @Override
    public WorkflowBuilder buildWorkflow(String name) {
        checkNotNull(name, "name");
        return new BasicWorkflow.Builder(name);
    }

    @Override
    public JobBuilder buildJob(String name) {
        checkNotNull(name, "name");
        return new BasicJob.Builder(name);
    }

    @Override
    public WorkflowRunnerBuilder buildRunner(WorkflowRepository repository) {
        checkNotNull(repository, "repository");
        EnhancedWorkflowRepository real = checkRealType(repository, EnhancedWorkflowRepository.class, "repository");
        return new SerialWorkflowRunner.Builder(real);
    }

    @Override
    public WorkflowRunner createRunner(WorkflowRepository repository) {
        return buildRunner(repository).get();
    }

    @Override
    public TaskFactory getTaskFactory() {
        return taskFactory;
    }

    private static DataSource createDefaultDataSource() {
        return SimpleDataSource.at(DEFAULT_DATASOURCE_URL, "SA", "");
    }
}