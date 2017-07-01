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
package com.github.i49.quantumleap.core.runner;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.github.i49.quantumleap.api.tasks.TaskContext;
import com.github.i49.quantumleap.api.workflow.JobStatus;
import com.github.i49.quantumleap.api.workflow.Platform;
import com.github.i49.quantumleap.api.workflow.RunnerConfiguration;
import com.github.i49.quantumleap.api.workflow.WorkflowException;
import com.github.i49.quantumleap.api.workflow.WorkflowRunner;
import com.github.i49.quantumleap.core.common.Platforms;
import com.github.i49.quantumleap.core.repository.EnhancedRepository;
import com.github.i49.quantumleap.core.workflow.ManagedJob;

/**
 * A skeletal implementation of {@link WorkflowRunner}.
 */
abstract class AbstractWorkflowRunner implements WorkflowRunner {
    
    private final Platform platform;
    private final EnhancedRepository repository;
    private final Path workDirectory;
    private final Path jobsDirectory;

    protected AbstractWorkflowRunner(EnhancedRepository repository, RunnerConfiguration config) {

        this.platform = Platforms.getCurrent();
        this.repository = repository;
        this.workDirectory = (Path)config.getProperty(RunnerConfiguration.WORKING_DIRECTORY).get();
        this.jobsDirectory = this.workDirectory.resolve("jobs");
                
        try {
            prepareDirectory((Boolean)config.getProperty(RunnerConfiguration.WORKING_DIRECTORY_RESET).get());
        } catch (IOException e) {
            // TODO
            throw new WorkflowException("", e);
        }
    }
    
    @Override
    public Platform getPlatform() {
        return platform;
    }
    
    protected EnhancedRepository getRepository() {
        return repository;
    }
    
    protected Path getWorkDirectory() {
        return workDirectory;
    }
    
    protected JobContext createJobContext(ManagedJob job) {
        Path jobDirectory = createDirectoryForJob(job);
        return new JobContextImpl(job, jobDirectory);
    }
    
    protected void completeJob(ManagedJob job, JobContext context) {
        JobStatus status = JobStatus.COMPLETED;
        Map<String, Object> jobOutput = context.getJobOutput();
        String[] lines = context.getStandardOutputLines();
        getRepository().storeJob(job, status, jobOutput, lines);
        for (long dependant: getRepository().findDependants(job)) {
            getRepository().updateJobStatusIfReady(dependant);
        }
    }
    
    private Path createDirectoryForJob(ManagedJob job) {
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
    
    /**
     * Prepares working directory of this runner.
     * 
     * @param clean
     * @throws IOException if an I/O error has occurred.
     */
    private void prepareDirectory(boolean clean) throws IOException {
        Files.createDirectories(workDirectory);
        Files.createDirectories(jobsDirectory);
    }
    
    /**
     * The implementation of {@link TaskContext} for this runner.
     */
    private class JobContextImpl implements JobContext {
        
        private final ManagedJob job;
        private final Path jobDirectory;
        private final Map<String, Object> jobOutput;
        private final JobPrintStream standardStream;
        
        private JobContextImpl(ManagedJob job, Path jobDirectory) {
            this.job = job;
            this.jobDirectory = jobDirectory;
            this.jobOutput = new HashMap<String, Object>();
            this.standardStream = new JobPrintStream();
        }

        @Override
        public Path getJobDirectory() {
            return jobDirectory;
        }

        @Override
        public Map<String, Object> getJobInput() {
            return job.getJobInput();
        }
        
        @Override
        public Map<String, Object> getJobOutput() {
            return jobOutput;
        }
        
        @Override
        public Platform getPlatform() {
            return platform;
        }

        @Override
        public PrintStream getStandardStream() {
            return standardStream;
        }

        @Override
        public String[] getStandardOutputLines() {
            standardStream.flush();
            return standardStream.getLines();
        }
    }
}
