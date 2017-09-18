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

/**
 * Builder interface for building an instance of {@link Workflow}.
 */
public interface WorkflowBuilder {

    /**
     * Adds one or more jobs to the workflow.
     * 
     * @param jobs list of jobs to be added to the workflow.
     * @return this builder.
     * @throws NullPointerException if one or more parameters are {@code null}.
     * @throws IllegalArgumentException if one of given jobs is not instantiated by this engine.
     */
    WorkflowBuilder jobs(Job... jobs);
    
    /**
     * Adds a link between two jobs composing the workflow.
     * 
     * @param source the source job of the link.
     * @param target the target job of the link. 
     * @return this builder.
     * @throws NullPointerException if one or more parameters are {@code null}.
     * @throws IllegalArgumentException if one of given jobs is not instantiated by this engine.
     */
    WorkflowBuilder link(Job source, Job target);
    
    WorkflowBuilder link(Job source, Job target, ParameterSetMapper mapper);

    /**
     * Returns the {@link Workflow} built by this builder.
     * 
     * @return the instance of {@link Workflow}.
     */
    Workflow build();
}
