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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.github.i49.quantumleap.api.workflow.Job;
import com.github.i49.quantumleap.api.workflow.WorkflowException;
import com.github.i49.quantumleap.api.workflow.WorkflowRunner;
import com.github.i49.quantumleap.core.repository.EnhancedWorkflowRepository;

/**
 * A skeletal implementation of {@link WorkflowRunner}.
 */
abstract class AbstractWorkflowRunner implements WorkflowRunner {
    
    private final EnhancedWorkflowRepository repository;
    private final Path workDirectory;
    private final Path jobsDirectory;

    protected AbstractWorkflowRunner(EnhancedWorkflowRepository repository, DefaultRunnerConfiguration configuration) {
        this.repository = repository;
        this.workDirectory = configuration.workDirectory;
        this.jobsDirectory = this.workDirectory.resolve("jobs");
        
        try {
            prepareDirectory(configuration.clean);
        } catch (IOException e) {
            // TODO
            throw new WorkflowException("", e);
        }
    }
    
    protected EnhancedWorkflowRepository getRepository() {
        return repository;
    }
    
    protected Path getWorkDirectory() {
        return workDirectory;
    }
    
    protected Path createDirectoryForJob(Job job) {
        String name = String.valueOf(job.getId());
        Path path = jobsDirectory.resolve(name);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            // TODO
            throw new WorkflowException("", e);
        }
        return path;
    }
    
    private void prepareDirectory(boolean clean) throws IOException {
        Files.createDirectories(workDirectory);
        Files.createDirectories(jobsDirectory);
    }
}
