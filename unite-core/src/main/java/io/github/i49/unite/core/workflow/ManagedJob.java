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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.OptionalLong;

import io.github.i49.unite.api.base.ParameterSet;
import io.github.i49.unite.api.tasks.Task;
import io.github.i49.unite.api.workflow.Job;
import io.github.i49.unite.api.workflow.JobStatus;

/**
 * The job managed by the workflow engine.
 */
public class ManagedJob extends WorkflowComponent implements Job {
    
    private final String name;
    private final ParameterSet inputParameters;

    private OptionalLong workflowId;
    private JobStatus status;
    private List<Task> tasks;
    private ParameterSet outputParameters;
    private List<String> standardOutput;
    
    ManagedJob(ManagedJobBuilder builder) {
        super(builder.jobId);
        this.name = builder.name;
        this.inputParameters = builder.inputParameters;
        this.outputParameters = builder.outputParameters;
        this.tasks = Collections.unmodifiableList(builder.tasks);
        this.status = builder.status;
        this.standardOutput = builder.standardOutput;

        this.workflowId = OptionalLong.empty();
    }
 
    @Override
    public String getName() {
        return name;
    }
   
    @Override
    public ParameterSet getInputParameters() {
        return inputParameters;
    }
    
    @Override
    public ParameterSet getOutputParameters() {
        return outputParameters;
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
    public String toString() {
        return getName();
    }
    
    public long getWorkdlowId() {
        return workflowId.getAsLong();
    }

    public void setOutputParamters(ParameterSet parameters) {
        this.outputParameters = parameters;
    }
    
    public void setStandardOutput(String[] lines) {
        standardOutput = Arrays.asList(lines);
    }
    
    public void setStatus(JobStatus status) {
        this.status = status;
    }
    
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
    
    public void setWorkflowId(long id) {
        this.workflowId = OptionalLong.of(id);
    }
}
