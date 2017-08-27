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
package io.github.i49.unite.core.repository;

import java.util.List;
import java.util.Map;

import io.github.i49.unite.api.workflow.Job;
import io.github.i49.unite.api.workflow.JobStatus;
import io.github.i49.unite.api.workflow.WorkflowRepository;

import io.github.i49.unite.core.workflow.JobLink;
import io.github.i49.unite.core.workflow.ManagedJob;

/**
 * The enhanced {@link WorkflowRepository} interface.
 */
public interface EnhancedRepository extends WorkflowRepository {

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
