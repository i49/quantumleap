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

import java.util.Optional;

import com.github.i49.quantumleap.api.tasks.Task;
import com.github.i49.quantumleap.api.tasks.TaskContext;
import com.github.i49.quantumleap.api.workflow.Job;
import com.github.i49.quantumleap.api.workflow.JobStatus;
import com.github.i49.quantumleap.api.workflow.WorkflowRunner;
import com.github.i49.quantumleap.core.repository.EnhancedWorkflowRepository;

/**
 * An implementation of {@link WorkflowRunner} which executes jobs sequentially.
 */
public class SerialWorkflowRunner extends AbstractWorkflowRunner implements WorkflowRunner {

    private long totalJobsDone;
    private boolean running;
    @SuppressWarnings("unused")
    private boolean canceled;

    public SerialWorkflowRunner(EnhancedWorkflowRepository repository, DefaultRunnerConfiguration configuration) {
        super(repository, configuration);
        this.totalJobsDone = 0;
        this.running = false;
        this.canceled = false;
    }

    @Override
    public long getTotalNumberOfJobsDone() {
        return totalJobsDone;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public long runSingle() {
        Optional<Job> job = getRepository().findFirstJobByStatus(JobStatus.READY);
        if (job.isPresent()) {
            launchJob((BasicJob) job.get());
            this.totalJobsDone++;
            return 1L;
        } else {
            return 0L;
        }
    }

    @Override
    public long runAll() {
        long jobsDone = 0;
        while (runSingle() > 0) {
            jobsDone++;
        }
        return jobsDone;
    }

    @Override
    public long runInfinite() {
        // TODO:
        return 0L;
    }

    @Override
    public void stop() {
        if (isRunning()) {
            // TODO Auto-generated method stub
            canceled = true;
        }
    }

    private void launchJob(BasicJob job) {
        executeJob(job);
        getRepository().storeJobStatus(job);
    }

    private void executeJob(BasicJob job) {
        TaskContext context = createTaskContext(job);
        for (Task task : job.getTasks()) {
            task.run(context);
        }
        completeJob(job);
    }
}
