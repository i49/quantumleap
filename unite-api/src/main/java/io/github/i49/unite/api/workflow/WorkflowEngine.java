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
package io.github.i49.unite.api.workflow;

import io.github.i49.unite.api.tasks.TaskFactory;

/**
 * The central interface for the API to manipulate the workflow engine.
 */
public interface WorkflowEngine {

    /**
     * Returns the default workflow engine found. The instance will be loaded
     * via Service Provider Interface.
     * 
     * @return the default workflow engine loaded.
     */
    static WorkflowEngine get() {
        return WorkflowEngineLoader.getEngine();
    }
    
    /**
     * Creates a builder to build a workflow.
     * 
     * @param name the name of the workflow, cannot be {@code null}.
     * @return a builder to build a workflow.
     * @throws NullPointerException if given {@code name} is {@code null}.
     */
    WorkflowBuilder createWorkflowBuilder(String name);

    /**
     * Creates a builder to build a job.
     * 
     * @param name the name of the job, cannot be {@code null}.
     * @return a builder to build a job.
     * @throws NullPointerException if given {@code name} is {@code null}.
     */
    JobBuilder createJobBuilder(String name);

    ParameterSetMapperFactory getParameterSetMapperFactory();
    
    /**
     * Returns the instance of {@link TaskFactory}.
     * 
     * @return the instance of {@link TaskFactory}.
     */
    TaskFactory getTaskFactory();
}
