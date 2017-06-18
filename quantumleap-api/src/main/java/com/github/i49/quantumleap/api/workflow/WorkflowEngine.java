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
     * Creates the repository which will store workflows and jobs.
     * 
     * @return the workflow repository.
     */
    WorkflowRepository createRepository();

    /**
     * Creates a builder to build a workflow.
     * 
     * @param name
     *            the name of the workflow, cannot be {@code null}.
     * @return a builder to build a workflow.
     * @throws NullPointerException
     *             if given {@code name} is {@code null}.
     */
    WorkflowBuilder buildWorkflow(String name);

    /**
     * Creates a builder to build a job.
     * 
     * @param name
     *            the name of the job, cannot be {@code null}.
     * @return a builder to build a job.
     * @throws NullPointerException
     *             if given {@code name} is {@code null}.
     */
    JobBuilder buildJob(String name);

    /**
     * Creates a builder to build a workflow runner.
     * 
     * @param repository
     *            the repository where workflows to run are stored.
     * @return a builder to build a workflow runner.
     * @throws NullPointerException
     *             if given {@code repository} is {@code null}.
     */
    WorkflowRunnerBuilder buildRunner(WorkflowRepository repository);

    /**
     * Creates a workflow runner.
     * 
     * @param repository
     *            the repository where workflows to run are stored.
     * @return an instance of {@link WorkflowRunner}.
     * @throws NullPointerException
     *             if given {@code repository} is {@code null}.
     */
    WorkflowRunner createRunner(WorkflowRepository repository);

    /**
     * Returns the instance of {@link TaskFactory}.
     * 
     * @return the instance of {@link TaskFactory}.
     */
    TaskFactory getTaskFactory();

    /**
     * Returns the default workflow engine found. The instance will be loaded
     * via Service Provider Interface.
     * 
     * @return the default workflow engine loaded.
     */
    static WorkflowEngine get() {
        return WorkflowEngineLoader.getEngine();
    }
}
