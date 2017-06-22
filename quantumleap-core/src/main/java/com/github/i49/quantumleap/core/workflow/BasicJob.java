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
import java.util.Collections;
import java.util.List;

import com.github.i49.quantumleap.api.tasks.Task;
import com.github.i49.quantumleap.api.workflow.Job;
import com.github.i49.quantumleap.api.workflow.JobBuilder;
import com.github.i49.quantumleap.api.workflow.JobStatus;

/**
 * An implementation of {@link Job}.
 */
public class BasicJob extends WorkflowComponent implements Job {

    private final String name;
    private final List<Task> tasks;
    private JobStatus status;

    private BasicJob(Builder builder) {
        this.name = builder.name;
        this.tasks = Collections.unmodifiableList(builder.tasks);
        this.status = JobStatus.INITIAL;
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
    public List<Task> getTasks() {
        return tasks;
    }

    @Override
    public boolean hasPredecessor() {
        // TODO:
        return false;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return getName();
    }

    public static class Builder implements JobBuilder {

        private final String name;
        private final List<Task> tasks = new ArrayList<>();

        public Builder(String name) {
            this.name = name;
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
        public Builder tasks(List<Task> tasks) {
            checkNotNull(tasks, "tasks");
            this.tasks.addAll(tasks);
            return this;
        }

        @Override
        public BasicJob get() {
            return new BasicJob(this);
        }
    }
}
