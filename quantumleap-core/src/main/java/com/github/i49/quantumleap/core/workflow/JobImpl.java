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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    private final Map<String, Object> jobInput;
    private final Map<String, Object> jobOutput;
    private final List<Task> tasks;
    private final JobStatus status;
    private final List<String> standardOutput;

    private JobImpl(Builder builder) {
        super(builder.id);
        this.name = builder.name;
        this.dependencies = Collections.unmodifiableSet(builder.dependencies);
        this.jobInput = builder.jobInput;
        this.jobOutput = Collections.unmodifiableMap(builder.jobOutput);
        this.tasks = Collections.unmodifiableList(builder.tasks);
        this.status = builder.status;
        this.standardOutput = builder.standardOutput;
    }

    @Override
    public String getName() {
        return name;
    }
   
    @Override
    public Map<String, Object> getJobInput() {
        return jobInput;
    }
    
    @Override
    public Map<String, Object> getJobOutput() {
        return jobOutput;
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
        private Map<String, Object> jobInput;
        private Map<String, Object> jobOutput;
        private JobStatus status;
        private List<String> standardOutput;

        public Builder(String name) {
            this.id = OptionalLong.empty();
            this.name = name;
            this.status = JobStatus.INITIAL;
            this.jobInput = new HashMap<>();
            this.jobOutput = new HashMap<>();
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
        
        public JobBuilder input(String name, Object value) {
            checkNotNull(name, "name");
            this.jobInput.put(name, value);
            return this;
        }

        public JobBuilder input(Map<String, Object> parameters) {
            checkNotNull(parameters, "parameters");
            this.jobInput.putAll(parameters);
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

        /* 
         * methods for ManagedJobBuilder interface
         */
        
        @Override
        public Builder jobId(long id) {
            this.id = OptionalLong.of(id);
            return this;
        }

        @Override
        public Builder jobOutput(Map<String, Object> jobOutput) {
            checkNotNull(jobOutput, "jobOutput");
            this.jobOutput = jobOutput;
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
