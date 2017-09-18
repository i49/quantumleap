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
package io.github.i49.unite.api.workflow;

import static org.assertj.core.api.Assertions.*;

import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import io.github.i49.unite.api.workflow.Job;
import io.github.i49.unite.api.workflow.Workflow;

/**
 * Unit test of {@link Workflow}.
 * 
 * @author i49
 */
public class WorkflowTest {

    private WorkflowFactory workflowFactory;
    
    @Before
    public void setUp() {
        workflowFactory = WorkflowFactory.newInstance();
    }

    @Test
    public void hasId_shouldReturnFalseByDefault() {
        Workflow workflow = workflowFactory.createWorkflowBuilder("workflow1").build();
        assertThat(workflow.hasId()).isFalse();
    }

    @Test
    public void getId_shouldThrowExceptionByDefault() {
        Workflow workflow = workflowFactory.createWorkflowBuilder("workflow1").build();
        Throwable thrown = catchThrowable(() -> {
            workflow.getId();
        });
        assertThat(thrown).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void getName_shouldReturnNameOfWorkflow() {
        Workflow workflow = workflowFactory.createWorkflowBuilder("workflow1").build();
        assertThat(workflow.getName()).isEqualTo("workflow1");
    }

    @Test
    public void getJobs_shouldReturnAllJobsAssigned() {
        Job job1 = workflowFactory.createJobBuilder("job1").build();
        Job job2 = workflowFactory.createJobBuilder("job2").build();
        Workflow workflow = workflowFactory.createWorkflowBuilder("workflow1").jobs(job1, job2).build();
        assertThat(workflow.getJobs()).containsExactly(job1, job2);
    }
}
