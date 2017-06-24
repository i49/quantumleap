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

import java.util.List;
import java.util.NoSuchElementException;

import com.github.i49.quantumleap.api.tasks.Task;

/**
 * A job which composes a workflow.
 */
public interface Job {

    /**
     * Returns the identifier of this job.
     * 
     * @return the identifier of this job.
     * @throws NoSuchElementException
     *             if identifier is not assigned.
     */
    long getId();

    /**
     * Return the name of this job.
     * 
     * @return the name of this job, cannot be {@code null}.
     */
    String getName();

    /**
     * Returns the status of this job.
     * 
     * @return the status of this job, cannot be {@code null}.
     */
    JobStatus getStatus();

    /**
     * Returns the list of tasks assigned to this job.
     * 
     * @return the list of tasks.
     */
    List<Task> getTasks();

    /**
     * Checks if this job has an identifier or not.
     * 
     * @return {@code true} if this job has identifier assigned, {@code false}
     *         otherwise.
     */
    boolean hasId();

    /**
     * Checks if this job has any dependencies or not.
     * 
     * @return {@code true} if this job has dependencies, {@code false}
     *         otherwise.
     */
    boolean hasDependencies();
}
