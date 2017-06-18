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
 * An implementation of {@link Workflow}.
 */
public class BasicWorkflow extends WorkflowComponent implements Workflow {

    private final String name;
    private final Set<BasicJob> jobs;

    private BasicWorkflow(Builder builder) {
        this.name = builder.name;
        this.jobs = builder.jobs;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Iterable<Job> getJobs() {
        return Collections.unmodifiableCollection(jobs);
    }

    @Override
    public String toString() {
        return getName();
    }

    public static class Builder implements WorkflowBuilder {

        private final String name;
        private final Set<BasicJob> jobs = new LinkedHashSet<>();

        public Builder(String name) {
            this.name = name;
        }

        @Override
        public Builder jobs(Job... jobs) {
            for (Job job : jobs) {
                if (job == null) {
                    continue;
                }
                BasicJob realJob = checkRealType(job, BasicJob.class, "jobs");
                this.jobs.add(realJob);
            }
            return this;
        }

        @Override
        public Workflow get() {
            return new BasicWorkflow(this);
        }
    }
}
