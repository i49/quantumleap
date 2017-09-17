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

package io.github.i49.unite.core.storage;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import io.github.i49.unite.api.workflow.Job;
import io.github.i49.unite.api.workflow.JobStatus;
import io.github.i49.unite.api.workflow.Workflow;
import io.github.i49.unite.core.workflow.JobLink;
import io.github.i49.unite.core.workflow.ManagedJob;

/**
 * Low level interface for operating on workflow storage.
 */
public interface WorkflowStorage extends AutoCloseable {

    /**
     * Closes this storage.
     */
    @Override
    void close();
    
    /**
     * Clears all entries in this storage.
     */
    void clear();

    /**
     * Adds a workflow to this storage.
     * 
     * @param workflow the workflow to add.
     */
    void addWorkflow(Workflow workflow);

    /**
     * Counts the workflows stored in this storage.
     * 
     * @return the number of workflows in this storage.
     */
    long countWorkflows();

    /**
     * Counts the jobs stored in this storage.
     * 
     * @return the number of jobs in this storage.
     */
    long countJobs();

    /**
     * Counts jobs that have the specified status.
     * 
     * @param status the status of the jobs to count.
     * @return the number of the jobs.
     */
    long countJobsWithStatus(JobStatus status);

    /**
     * Returns the job specified by the ID.
     * 
     * @param id the identifier of the job.
     * @return retrieved job, never be {@code null}.
     * @throws NoSuchElementException if no job has the specified ID.  
     */
    Job findJobById(long id);

    List<Job> findJobsByStatus(JobStatus status);

    Optional<Job> findFirstJobByStatus(JobStatus status);
    
    /**
     * Returns the status of the job specified by the ID.
     * 
     * @param id the identifier of the job.
     * @return the status of the specified job, never be {@code null}.
     * @throws NoSuchElementException if no job has the specified ID.  
     */
    JobStatus getJobStatus(long id);

    Workflow getWorkflow(long id);
    
    //
    
    List<JobLink> findLinksByTarget(ManagedJob target);

    /**
     * Finds next jobs.
     * 
     * @param job the source job.
     * @return the list of next jobs, never be {@code null}.
     */
    List<Long> findNextJobs(Job job);
    
    /**
     * Stores the specified job with product of the execution.
     * 
     * @param job the job to store.
     * @param status the status of the job after the execution.
     * @param jobOutput the output of the job.
     * @param standardOutput the standard output produced by the execution.
     */
    void storeJob(Job job, JobStatus status, Map<String, Object> jobOutput, String[] standardOutput);

    int updateJobStatusIfReady(long jobId);
}
