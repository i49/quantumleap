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

package io.github.i49.unite.server.runner.tasks;

import org.junit.rules.ExternalResource;

import io.github.i49.unite.api.tasks.Task;
import io.github.i49.unite.api.tasks.TaskFactory;
import io.github.i49.unite.api.workflow.Job;
import io.github.i49.unite.api.workflow.Workflow;
import io.github.i49.unite.api.workflow.WorkflowEngine;
import io.github.i49.unite.api.workflow.WorkflowRepository;
import io.github.i49.unite.api.workflow.WorkflowRunner;
import io.github.i49.unite.server.runner.RunnerFactory;

/**
 *
 */
public class TaskRunner extends ExternalResource {

    private WorkflowEngine engine;
    private TaskFactory factory;
    private WorkflowRepository repository;
    private WorkflowRunner runner;
    
    @Override
    public void before() {
        engine = WorkflowEngine.get();
        factory = engine.getTaskFactory();
        repository = engine.createRepository();
        runner = new RunnerFactory().createRunner();
    }
    
    @Override
    public void after() {
        if (repository != null) {
            repository.close();
        }
    }

    public void reset() {
        repository.clear();
    }
    
    public TaskFactory getFactory() {
        return factory;
    }
    
    public Job runTask(Task task) {
        Job job = engine.createJobBuilder("job1").tasks(task).build();
        Workflow workflow = engine.createWorkflowBuilder("workflow1").jobs(job).build();
        repository.addWorkflow(workflow);
        runner.runSingle();
        return repository.findJobById(job.getId());
    }
}
