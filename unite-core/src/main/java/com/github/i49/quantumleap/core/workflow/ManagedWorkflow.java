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
package com.github.i49.quantumleap.core.workflow;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.github.i49.quantumleap.api.workflow.Job;
import com.github.i49.quantumleap.api.workflow.Workflow;
import com.github.i49.quantumleap.api.workflow.WorkflowStatus;

/**
 * The workflow managed by the workflow engine.
 */
public class ManagedWorkflow extends WorkflowComponent implements Workflow {

    private final String name;
    private final Set<ManagedJob> jobs;
    private final Set<JobLink> jobLinks;
    private final Map<ManagedJob, Set<ManagedJob>> dependencyMap;

    private WorkflowStatus status;

    ManagedWorkflow(ManagedWorkflowBuilder builder) {
        this.name = builder.name;
        this.jobs = Collections.unmodifiableSet(builder.jobs);
        this.dependencyMap = Collections.unmodifiableMap(builder.dependencyMap);
        this.jobLinks = Collections.unmodifiableSet(builder.links);

        this.status = WorkflowStatus.INITIAL;
    }

    @Override
    public String getName() {
        return name;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterable<Job> getJobs() {
        Set<? extends Job> jobs = this.jobs;
        return (Iterable<Job>)jobs;
    }
    
    @Override
    public WorkflowStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return getName();
    }
    
    public Iterable<ManagedJob> getManagedJobs() {
        return jobs;
    }
    
    public Iterable<JobLink> getJobLinks() {
        return jobLinks;
    }
    
    public Set<ManagedJob> getDependenciesOf(ManagedJob job) {
        Set<ManagedJob> dependencies = dependencyMap.get(job);
        if (dependencies == null) {
            return Collections.emptySet();
        }
        return dependencies;
    }
    
    public void setStatus(WorkflowStatus status) {
        this.status = status;
    }
}
