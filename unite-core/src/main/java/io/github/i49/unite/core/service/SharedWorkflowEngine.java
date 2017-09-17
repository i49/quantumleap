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
package io.github.i49.unite.core.service;

import static io.github.i49.unite.core.common.Preconditions.checkNotNull;

import io.github.i49.unite.api.tasks.TaskFactory;
import io.github.i49.unite.api.workflow.JobBuilder;
import io.github.i49.unite.api.workflow.ParameterSetMapperFactory;
import io.github.i49.unite.api.workflow.WorkflowBuilder;
import io.github.i49.unite.api.workflow.WorkflowEngine;
import io.github.i49.unite.api.workflow.WorkflowRepository;
import io.github.i49.unite.core.mappers.DefaultParameterSetMapperFactory;
import io.github.i49.unite.core.repository.DefaultWorkflowRepository;
import io.github.i49.unite.core.storage.StorageConfiguration;
import io.github.i49.unite.core.storage.WorkflowStorage;
import io.github.i49.unite.core.storage.util.WorkflowStorages;
import io.github.i49.unite.core.tasks.DefaultTaskFactory;
import io.github.i49.unite.core.workflow.WorkflowFactory;

/**
 * The workflow engine which is shared by all threads.
 */
public class SharedWorkflowEngine implements WorkflowEngine {

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
        WorkflowStorage storage = createStorage();
        return new DefaultWorkflowRepository(storage);
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
    public ParameterSetMapperFactory getParameterSetMapperFactory() {
        return parameterSetMapperFactory;
    }

    @Override
    public TaskFactory getTaskFactory() {
        return taskFactory;
    }
    
    private WorkflowStorage createStorage() {
        StorageConfiguration config = StorageConfiguration.getDefault();
        return WorkflowStorages.create(config);
    }
}
