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

import static com.github.i49.quantumleap.core.common.Preconditions.checkNotNull;
import static com.github.i49.quantumleap.core.common.Preconditions.checkRealType;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.github.i49.quantumleap.api.workflow.Job;
import com.github.i49.quantumleap.api.workflow.ParameterSetMapper;
import com.github.i49.quantumleap.api.workflow.Workflow;
import com.github.i49.quantumleap.api.workflow.WorkflowBuilder;
import com.github.i49.quantumleap.core.mappers.MergingParameterSetMapper;

/**
 * The implementation of {@link Workflow} provided by this engine.
 */
class WorkflowImpl extends WorkflowComponent implements ManagedWorkflow {

    private final String name;
    private final Set<ManagedJob> jobs;
    private final Set<JobLink> jobLinks;
    private final Map<ManagedJob, Set<ManagedJob>> dependencyMap;

    private WorkflowImpl(Builder builder) {
        this.name = builder.name;
        this.jobs = Collections.unmodifiableSet(builder.jobs);
        this.dependencyMap = Collections.unmodifiableMap(builder.dependencyMap);
        this.jobLinks = Collections.unmodifiableSet(builder.links);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Iterable<Job> getJobs() {
        return castSet(this.jobs);
    }

    @Override
    public String toString() {
        return getName();
    }
    
    /* ManagedWorkflow interface */
 
    @Override
    public Iterable<ManagedJob> getManagedJobs() {
        return jobs;
    }
    
    @Override
    public Iterable<JobLink> getJobLinks() {
        return jobLinks;
    }
  
    @Override
    public Set<ManagedJob> getDependenciesOf(ManagedJob job) {
        Set<ManagedJob> dependencies = dependencyMap.get(job);
        if (dependencies == null) {
            return Collections.emptySet();
        }
        return dependencies;
    }
  
    @SuppressWarnings("unchecked")
    private static <T> Set<T> castSet(Set<? extends T> set) {
        return (Set<T>)set;
    }

    /**
     * The implementation of {@link WorkflowBuilder} provided by this engine.
     */
    public static class Builder implements ManagedWorkflowBuilder {

        private final String name;
        private final Set<ManagedJob> jobs = new LinkedHashSet<>();
        private final Set<JobLink> links = new HashSet<>();
        private final Map<ManagedJob, Set<ManagedJob>> dependencyMap = new HashMap<>();
        
        private static final ParameterSetMapper DEFAULT_MAPPER = MergingParameterSetMapper.INSTANCE;
        
        public Builder(String name) {
            this.name = name;
        }

        @Override
        public Builder jobs(Job... jobs) {
            checkNotNull(jobs, "jobs");
            checkRealType(jobs, ManagedJob.class, "jobs");
            for (Job job : jobs) {
                addJob((ManagedJob)job);
            }
            return this;
        }
        
        @Override
        public Builder link(Job source, Job target) {
            link(source, target, DEFAULT_MAPPER);
            return this;
        }

        @Override
        public Builder link(Job source, Job target, ParameterSetMapper mapper) {
            checkNotNull(source, "source");
            checkNotNull(target, "target");
            checkNotNull(mapper, "mapper");
            link(checkRealType(source, ManagedJob.class, "source"),
                 checkRealType(target, ManagedJob.class, "target"),
                 mapper);  
            return this;
        }

        @Override
        public ManagedWorkflow build() {
            return new WorkflowImpl(this);
        }
        
        private void addJob(ManagedJob job) {
            this.jobs.add(job);
        }
        
        private void link(ManagedJob source, ManagedJob target, ParameterSetMapper mapper) {
            addJob(source);
            addJob(target);
            this.links.add(new JobLinkImpl(source, target, mapper));
            
            Set<ManagedJob> dependencies = this.dependencyMap.get(target);
            if (dependencies == null) {
                dependencies = new HashSet<>();
                this.dependencyMap.put(target, dependencies);
            }
            dependencies.add(source);
        }
    }
}
