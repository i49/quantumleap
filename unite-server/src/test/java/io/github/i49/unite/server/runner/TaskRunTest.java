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

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.github.i49.unite.api.repository.WorkflowRepository;
import io.github.i49.unite.api.repository.WorkflowRepositoryBuilder;
import io.github.i49.unite.api.tasks.Task;
import io.github.i49.unite.api.tasks.TaskFactory;
import io.github.i49.unite.api.workflow.Job;
import io.github.i49.unite.api.workflow.JobStatus;
import io.github.i49.unite.api.workflow.Workflow;
import io.github.i49.unite.api.workflow.WorkflowFactory;
import io.github.i49.unite.server.TestDataSource;

/**
 *
 */
public class TaskRunTest {

    private static final DataSource dataSource = new TestDataSource();
    
    private static WorkflowRepository repository;
    private static WorkflowRunner runner;

    private WorkflowFactory workflowFactory;
    private TaskFactory taskFactory;
    
    @BeforeClass
    public static void setUpOnce() {
        repository = WorkflowRepositoryBuilder.newInstance()
                .withDataSource(dataSource).build();
        runner = new RunnerFactory().createRunner();
    }
    
    @Before
    public void setUp() {
        workflowFactory = WorkflowFactory.newInstance();
        taskFactory = TaskFactory.newInstance();
        
        repository.clear();
    }
    
    @Test
    public void echoTask_shouldEchoMessage() {
        // given
        String message = "The quick brown fox jumps over the lazy dog";
        Task task = taskFactory.createEchoTask(message);
        
        // when
        Job job = runTask(task);
        
        // then
        assertThat(job.getStatus()).isSameAs(JobStatus.COMPLETED);
        assertThat(job.getStandardOutput()).containsExactly(message);
    }
    
    @Test
    public void scriptTask_shouldRunScript() {
        // given
        Path dir = Paths.get("target/test-classes");
        Path path = dir.resolve("hello" + getScriptExtension());
        Task task = taskFactory.createShellTaskBuilder(path)
                .arguments("John Smith")
                .build();
        
        // when
        Job job = runTask(task);

        // then
        assertThat(job.getStatus()).isSameAs(JobStatus.COMPLETED);
    }

    private Job runTask(Task task) {
        Job job = workflowFactory.createJobBuilder("job1").tasks(task).build();
        Workflow workflow = workflowFactory.createWorkflowBuilder("workflow1").jobs(job).build();
        repository.addWorkflow(workflow);
        runner.runSingle();
        return repository.findJobById(job.getId());
    }

    private static String getScriptExtension() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            return ".bat";
        } else {
            return ".sh";
        }
    }
}
