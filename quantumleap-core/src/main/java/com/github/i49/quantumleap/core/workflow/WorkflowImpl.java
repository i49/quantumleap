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

import static com.github.i49.quantumleap.core.common.Preconditions.checkRealType;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.github.i49.quantumleap.api.workflow.Job;
import com.github.i49.quantumleap.api.workflow.Workflow;
import com.github.i49.quantumleap.api.workflow.WorkflowBuilder;

/**
 * The implementation of {@link Workflow} provided by this engine.
 */
class WorkflowImpl extends WorkflowComponent implements ManagedWorkflow {

    private final String name;
    private final Set<ManagedJob> jobs;

    private WorkflowImpl(Builder builder) {
        this.name = builder.name;
        this.jobs = Collections.unmodifiableSet(builder.jobs);
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

        public Builder(String name) {
            this.name = name;
        }

        @Override
        public Builder jobs(Job... jobs) {
            for (Job job : jobs) {
                if (job == null) {
                    continue;
                }
                ManagedJob realJob = checkRealType(job, ManagedJob.class, "jobs");
                this.jobs.add(realJob);
            }
            return this;
        }

        @Override
        public ManagedWorkflow get() {
            return new WorkflowImpl(this);
        }
    }
}
