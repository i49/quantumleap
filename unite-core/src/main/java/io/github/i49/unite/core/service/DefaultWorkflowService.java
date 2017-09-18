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

import io.github.i49.unite.api.repository.WorkflowRepositoryBuilder;
import io.github.i49.unite.api.spi.WorkflowService;
import io.github.i49.unite.api.tasks.TaskFactory;
import io.github.i49.unite.api.workflow.WorkflowFactory;
import io.github.i49.unite.core.repository.DefaultWorkflowRepositoryBuilder;
import io.github.i49.unite.core.tasks.DefaultTaskFactory;
import io.github.i49.unite.core.workflow.factory.DefaultWorkflowFactory;

/**
 * The implementation of {@link WorkflowService}.
 * The instances of this type are created per thread.
 */
public class DefaultWorkflowService implements WorkflowService {

    public DefaultWorkflowService() {
    }

    @Override
    public WorkflowFactory createWorkflowFactory() {
        return new DefaultWorkflowFactory();
    }

    @Override
    public TaskFactory createTaskFactory() {
        return new DefaultTaskFactory();
    }

    @Override
    public WorkflowRepositoryBuilder creteRepositoryBuilder() {
        return new DefaultWorkflowRepositoryBuilder();
    }
}
