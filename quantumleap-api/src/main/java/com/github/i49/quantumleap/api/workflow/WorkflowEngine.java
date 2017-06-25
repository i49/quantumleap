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
package com.github.i49.quantumleap.api.workflow;

import com.github.i49.quantumleap.api.tasks.TaskFactory;

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
     * Creates the repository which will store workflows and jobs.
     * 
     * @return the workflow repository.
     */
    WorkflowRepository createRepository();

    /**
     * Creates a builder to build a workflow.
     * 
     * @param name the name of the workflow, cannot be {@code null}.
     * @return a builder to build a workflow.
     * @throws NullPointerException if given {@code name} is {@code null}.
     */
    WorkflowBuilder buildWorkflow(String name);

    /**
     * Creates a builder to build a job.
     * 
     * @param name the name of the job, cannot be {@code null}.
     * @return a builder to build a job.
     * @throws NullPointerException if given {@code name} is {@code null}.
     */
    JobBuilder buildJob(String name);

    /**
     * Creates a configuration of a workflow runner.
     * 
     * @return a configuration of a workflow runner.
     */
    RunnerConfiguration createRunnerConfiguration();
    
    /**
     * Creates a workflow runner with default configuration.
     * 
     * @param repository the repository where workflows to run are stored, cannot be {@code null}.
     * @return newly created instance of {@link WorkflowRunner}.
     * @throws NullPointerException if given {@code repository} is {@code null}.
     * @throws WorkflowException if given {@code repository} is not instantiated by this engine.
     */
    WorkflowRunner createRunner(WorkflowRepository repository);

    /**
     * Creates a workflow runner with specified configuration.
     * 
     * @param repository the repository where workflows to run are stored, cannot be {@code null}.
     * @param configuration the configuration of the runner, cannot be {@code null}.
     * @return newly created instance of {@link WorkflowRunner}.
     * @throws NullPointerException if one or more parameters are {@code null}.
     * @throws WorkflowException if given {@code repository} is not instantiated by this engine.
     */
    WorkflowRunner createRunner(WorkflowRepository repository, RunnerConfiguration configuration);

    /**
     * Returns the instance of {@link TaskFactory}.
     * 
     * @return the instance of {@link TaskFactory}.
     */
    TaskFactory getTaskFactory();
}
