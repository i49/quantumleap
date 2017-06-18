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
package com.github.i49.quantumleap.api.workflow;

import static org.assertj.core.api.Assertions.*;

import java.util.NoSuchElementException;

import org.junit.Test;

import com.github.i49.quantumleap.api.workflow.Job;
import com.github.i49.quantumleap.api.workflow.Workflow;
import com.github.i49.quantumleap.api.workflow.WorkflowEngine;

/**
 * Unit test of {@link Workflow}.
 */
public class WorkflowTest {

    private final WorkflowEngine engine = WorkflowEngine.get();

    @Test
    public void hasId_shouldReturnFalseByDefault() {
        Workflow workflow = this.engine.buildWorkflow("workflow1").get();
        assertThat(workflow.hasId()).isFalse();
    }

    @Test
    public void getId_shouldThrowExceptionByDefault() {
        Workflow workflow = engine.buildWorkflow("workflow1").get();
        Throwable thrown = catchThrowable(() -> {
            workflow.getId();
        });
        assertThat(thrown).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void getName_shouldReturnNameOfWorkflow() {
        Workflow workflow = engine.buildWorkflow("workflow1").get();
        assertThat(workflow.getName()).isEqualTo("workflow1");
    }

    @Test
    public void getJobs_shouldReturnAllJobsAssigned() {
        Job job1 = engine.buildJob("job1").get();
        Job job2 = engine.buildJob("job2").get();
        Workflow workflow = engine.buildWorkflow("workflow1").jobs(job1, job2).get();
        assertThat(workflow.getJobs()).containsExactly(job1, job2);
    }
}
