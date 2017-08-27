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
package io.github.i49.unite.core.workflow;

import static io.github.i49.unite.core.common.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;

import io.github.i49.unite.api.base.ParameterSet;
import io.github.i49.unite.api.tasks.Task;
import io.github.i49.unite.api.workflow.JobBuilder;
import io.github.i49.unite.api.workflow.JobStatus;

/**
 * Builder interface for building an instance of {@link ManagedJob}.
 * This type is for internal use.
 */
public class ManagedJobBuilder implements JobBuilder {
   
    OptionalLong jobId;
    final String name;
    final List<Task> tasks = new ArrayList<>();
    final ParameterSet inputParameters;
    final ParameterSet outputParameters;
    JobStatus status;
    List<String> standardOutput;

    ManagedJobBuilder(String name) {
        this.jobId = OptionalLong.empty();
        this.name = name;
        this.status = JobStatus.INITIAL;
        this.inputParameters = new SimpleParameterSet();
        this.outputParameters = new SimpleParameterSet();
        this.standardOutput = Collections.emptyList();
    }
    
    public ManagedJobBuilder input(String name, Object value) {
        checkNotNull(name, "name");
        this.inputParameters.put(name, value);
        return this;
    }

    public ManagedJobBuilder input(Map<String, Object> parameters) {
        checkNotNull(parameters, "parameters");
        this.inputParameters.putAll(parameters);
        return this;
    }
    
    @Override
    public ManagedJobBuilder tasks(Task... tasks) {
        checkNotNull(tasks, "tasks");
        for (Task task: tasks) {
            this.tasks.add(task);
        }
        return this;
    }
    
    @Override
    public ManagedJob build() {
        return new ManagedJob(this);
    }
}
