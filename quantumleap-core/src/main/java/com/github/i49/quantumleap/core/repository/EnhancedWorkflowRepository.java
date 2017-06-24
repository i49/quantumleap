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
package com.github.i49.quantumleap.core.repository;

import java.util.List;

import com.github.i49.quantumleap.api.workflow.Job;
import com.github.i49.quantumleap.api.workflow.JobStatus;
import com.github.i49.quantumleap.api.workflow.WorkflowRepository;

/**
 * The enhanced {@link WorkflowRepository} interface.
 */
public interface EnhancedWorkflowRepository extends WorkflowRepository {

    List<Long> findDependants(Job job);

    
    /**
     * Stores the status of the job.
     * 
     * @param job the job to store.
     */
    void storeJobStatus(Job job);
    
    void storeJobStatus(long jobId, JobStatus status);
    
    int updateJobStatusIfReady(long jobId);
}
