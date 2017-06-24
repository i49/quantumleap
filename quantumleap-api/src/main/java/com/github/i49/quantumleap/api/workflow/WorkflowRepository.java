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
import java.util.Optional;

/**
 * A repository that manages workflows to execute.
 */
public interface WorkflowRepository extends AutoCloseable {

    /**
     * Closes this repository.
     */
    @Override
    void close();

    /**
     * Clears all entries in this repository.
     */
    void clear();

    /**
     * Adds a workflow to this repository.
     * 
     * @param workflow the workflow to add.
     * @throws NullPointerException if given {@code workflow} is {@code null}.
     */
    void addWorkflow(Workflow workflow);

    /**
     * Counts the workflows stored in this repository.
     * 
     * @return the number of workflows in this repository.
     */
    long countWorkflows();

    /**
     * Counts the jobs stored in this repository.
     * 
     * @return the number of jobs in this repository.
     */
    long countJobs();

    /**
     * Counts jobs that have the specified status.
     * 
     * @param status the status of the jobs to count.
     * @return the number of the jobs.
     */
    long countJobsWithStatus(JobStatus status);

    List<Job> findJobsByStatus(JobStatus status);

    Optional<Job> findFirstJobByStatus(JobStatus status);
    
    /**
     * Updates the status of the specified job. 
     * 
     * @param job the job to update its status.
     * @return {@code true} if the job is updated, {@code false} otherwise.
     * @throws NullPointerException if given {@code job} is {@code null}.
     */
    boolean updateJobStatus(Job job); 
}
