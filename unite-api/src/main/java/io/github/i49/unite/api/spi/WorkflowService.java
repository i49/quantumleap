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

package io.github.i49.unite.api.spi;

import io.github.i49.unite.api.repository.WorkflowRepositoryBuilder;
import io.github.i49.unite.api.tasks.TaskFactory;
import io.github.i49.unite.api.workflow.WorkflowFactory;

/**
 * Service Provider Interface for the API.
 * This type should not be directly used by the API users.
 * 
 * @author i49
 */
public interface WorkflowService {
    
    /**
     * Creates an instance of {@link WorkflowFactory}.
     * 
     * @return newly created instance of {@link WorkflowFactory}.
     */
    WorkflowFactory createWorkflowFactory();
    
    /**
     * Creates an instance of {@link TaskFactory}.
     * 
     * @return newly created instance of {@link TaskFactory}.
     */
    TaskFactory createTaskFactory();

    /**
     * Creates a builder to build a workflow repository.
     * 
     * @return a builder to build a workflow repository.
     */
    WorkflowRepositoryBuilder creteRepositoryBuilder();
}
