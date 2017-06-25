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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalLong;
import java.util.Set;

import com.github.i49.quantumleap.api.tasks.Task;
import com.github.i49.quantumleap.api.workflow.Job;
import com.github.i49.quantumleap.api.workflow.JobBuilder;
import com.github.i49.quantumleap.api.workflow.JobStatus;

/**
 * The implementation of {@link Job} provided by this engine.
 */
class JobImpl extends WorkflowComponent implements ManagedJob {

    private final String name;
    private final Set<Job> dependencies;
    private final List<Task> tasks;
    private final JobStatus status;
    private final List<String> standardOutput;

    private JobImpl(Builder builder) {
        super(builder.id);
        this.name = builder.name;
        this.dependencies = Collections.unmodifiableSet(builder.dependencies);
        this.tasks = Collections.unmodifiableList(builder.tasks);
        this.status = builder.status;
        this.standardOutput = builder.standardOutput;
    }

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public JobStatus getStatus() {
        return status;
    }
    
    @Override
    public List<String> getStandardOutput() {
        return standardOutput;   
    }

    @Override
    public List<Task> getTasks() {
        return tasks;
    }

    @Override
    public boolean hasDependencies() {
        return dependencies.size() > 0;
    }

    @Override
    public String toString() {
        return getName();
    }
    
    @Override
    public Set<Job> getDependencies() {
        return dependencies;
    }

    /**
     * The implementation of {@link JobBuilder} provided by this engine.
     */
    public static class Builder implements ManagedJobBuilder {

        private OptionalLong id;
        private final String name;
        private final Set<Job> dependencies = new HashSet<>();
        private final List<Task> tasks = new ArrayList<>();
        private JobStatus status;
        private List<String> standardOutput;

        public Builder(String name) {
            this.id = OptionalLong.empty();
            this.name = name;
            this.status = JobStatus.INITIAL;
            this.standardOutput = Collections.emptyList();
        }
        
        @Override
        public Builder dependOn(Job... jobs) {
            checkNotNull(jobs, "jobs");
            for (Job job: jobs) {
                this.dependencies.add(job);
            }
            return this;
        }

        @Override
        public Builder tasks(Task... tasks) {
            checkNotNull(tasks, "tasks");
            for (Task task: tasks) {
                this.tasks.add(task);
            }
            return this;
        }
        
        @Override
        public JobImpl get() {
            return new JobImpl(this);
        }

        /* ManagedJobBuilder */
        
        @Override
        public Builder jobId(long id) {
            this.id = OptionalLong.of(id);
            return this;
        }
        
        @Override
        public Builder status(JobStatus status) {
            this.status = status;
            return this;
        }
        
        @Override
        public Builder standardOutput(String[] lines) {
            this.standardOutput = Arrays.asList(lines);
            return this;
        }
    }
}
