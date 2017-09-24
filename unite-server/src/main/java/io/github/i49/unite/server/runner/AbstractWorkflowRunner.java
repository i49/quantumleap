/* 
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
package io.github.i49.unite.server.runner;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import io.github.i49.unite.api.base.ParameterSet;
import io.github.i49.unite.api.base.Platform;
import io.github.i49.unite.api.base.WorkflowException;
import io.github.i49.unite.api.tasks.TaskContext;
import io.github.i49.unite.api.workflow.JobStatus;
import io.github.i49.unite.api.workflow.ParameterSetMapper;
import io.github.i49.unite.core.storage.WorkflowStorage;
import io.github.i49.unite.core.workflow.JobLink;
import io.github.i49.unite.core.workflow.ManagedJob;
import io.github.i49.unite.core.workflow.ManagedWorkflow;
import io.github.i49.unite.core.workflow.SimpleParameterSet;
import io.github.i49.unite.server.base.Platforms;

/**
 * A skeletal implementation of {@link WorkflowRunner}.
 */
abstract class AbstractWorkflowRunner implements WorkflowRunner {
    
    private final Platform platform;
    private final WorkflowStorage storage;
    private final DirectoryLayoutStrategy layoutStrategy;

    protected AbstractWorkflowRunner(WorkflowStorage storage, Path directory) {
        this.platform = Platforms.getCurrent();
        this.storage = storage;
        this.layoutStrategy = new BasicDirectoryLayoutStrategy(directory);
    }
    
    @Override
    public Platform getPlatform() {
        return platform;
    }
    
    protected WorkflowStorage getStorage() {
        return storage;
    }
    
    protected JobContext prepareJob(ManagedJob job) {
        ManagedWorkflow workflow = (ManagedWorkflow)getStorage().getWorkflow(job.getWorkdlowId());
        Path jobDirectory = createDirectoryForJob(workflow, job);
        ParameterSet inputParameter = prepareInputParameters(job);
        return new JobContextImpl(jobDirectory, inputParameter);
    }
    
    protected void completeJob(ManagedJob job, JobContext context) {
        JobStatus status = JobStatus.COMPLETED;
        Map<String, Object> jobOutput = context.getOutputParameters();
        String[] lines = context.getStandardOutputLines();
        getStorage().storeJob(job, status, jobOutput, lines);
        for (long nextId: getStorage().findNextJobs(job)) {
            getStorage().updateJobStatusIfReady(nextId);
        }
    }
    
    private ParameterSet prepareInputParameters(ManagedJob job) {
        ParameterSet inputParameters = job.getInputParameters();
        for (JobLink link: getStorage().findLinksByTarget(job)) {
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
