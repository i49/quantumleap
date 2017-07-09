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
import java.util.Map;

import com.github.i49.quantumleap.api.base.ParameterSet;
import com.github.i49.quantumleap.api.base.Platform;
import com.github.i49.quantumleap.api.base.WorkflowException;
import com.github.i49.quantumleap.api.tasks.TaskContext;
import com.github.i49.quantumleap.api.workflow.JobStatus;
import com.github.i49.quantumleap.api.workflow.ParameterSetMapper;
import com.github.i49.quantumleap.api.workflow.RunnerConfiguration;
import com.github.i49.quantumleap.api.workflow.WorkflowRunner;
import com.github.i49.quantumleap.core.common.Platforms;
import com.github.i49.quantumleap.core.repository.EnhancedRepository;
import com.github.i49.quantumleap.core.workflow.JobLink;
import com.github.i49.quantumleap.core.workflow.ManagedJob;
import com.github.i49.quantumleap.core.workflow.ManagedWorkflow;
import com.github.i49.quantumleap.core.workflow.SimpleParameterSet;

/**
 * A skeletal implementation of {@link WorkflowRunner}.
 */
abstract class AbstractWorkflowRunner implements WorkflowRunner {
    
    private final Platform platform;
    private final EnhancedRepository repository;
    private final DirectoryLayoutStrategy layoutStrategy;

    protected AbstractWorkflowRunner(EnhancedRepository repository, RunnerConfiguration config) {

        this.platform = Platforms.getCurrent();
        this.repository = repository;
        Path baseDirectory = (Path)config.getProperty(RunnerConfiguration.WORKING_DIRECTORY).get();
        this.layoutStrategy = new BasicDirectoryLayoutStrategy(baseDirectory);
    }
    
    @Override
    public Platform getPlatform() {
        return platform;
    }
    
    protected EnhancedRepository getRepository() {
        return repository;
    }
    
    protected JobContext prepareJob(ManagedJob job) {
        ManagedWorkflow workflow = (ManagedWorkflow)getRepository().getWorkflow(job.getWorkdlowId());
        Path jobDirectory = createDirectoryForJob(workflow, job);
        ParameterSet inputParameter = prepareInputParameters(job);
        return new JobContextImpl(jobDirectory, inputParameter);
    }
    
    protected void completeJob(ManagedJob job, JobContext context) {
        JobStatus status = JobStatus.COMPLETED;
        Map<String, Object> jobOutput = context.getOutputParameters();
        String[] lines = context.getStandardOutputLines();
        getRepository().storeJob(job, status, jobOutput, lines);
        for (long nextId: getRepository().findNextJobs(job)) {
            getRepository().updateJobStatusIfReady(nextId);
        }
    }
    
    private ParameterSet prepareInputParameters(ManagedJob job) {
        ParameterSet inputParameters = job.getInputParameters();
        for (JobLink link: getRepository().findLinksByTarget(job)) {
            ParameterSetMapper mapper = link.getMapper();
            mapper.mapParameterSet(link.getSource().getOutputParameters(), inputParameters);
        }
        return inputParameters;
    }
    
    private Path createDirectoryForJob(ManagedWorkflow workflow, ManagedJob job) {
        Path path = layoutStrategy.getJobDirectory(workflow, job);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            // TODO
            throw new WorkflowException("", e);
        }
        return path;
    }
    
    /**
     * The implementation of {@link TaskContext} for this runner.
     */
    private class JobContextImpl implements JobContext {
        
        private final Path jobDirectory;
        private final ParameterSet inputParameters;
        private final ParameterSet outputParameters;
        private final JobPrintStream standardStream;
        
        private JobContextImpl(Path jobDirectory, ParameterSet inputParameters) {
            this.jobDirectory = jobDirectory;
            this.inputParameters = inputParameters;
            this.outputParameters = new SimpleParameterSet();
            this.standardStream = new JobPrintStream();
        }

        @Override
        public Path getJobDirectory() {
            return jobDirectory;
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
