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
import com.github.i49.quantumleap.api.workflow.JobStatus;
import com.github.i49.quantumleap.api.workflow.WorkflowEngine;

/**
 * Unit test of {@link Job}.
 */
public class JobTest {

    private final WorkflowEngine engine = WorkflowEngine.get();

    @Test
    public void hasId_shouldReturnFalseByDefault() {
        Job job = this.engine.buildJob("job1").get();
        assertThat(job.hasId()).isFalse();
    }

    @Test
    public void getId_shouldThrowExceptionByDefault() {
        Job job = this.engine.buildJob("job1").get();
        Throwable thrown = catchThrowable(() -> {
            job.getId();
        });
        assertThat(thrown).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void getName_shouldReturnNameOfJob() {
        Job job = this.engine.buildJob("job1").get();
        assertThat(job.getName()).isEqualTo("job1");
    }

    @Test
    public void getStatus_shouldReturnInitialByDefault() {
        Job job = this.engine.buildJob("job1").get();
        assertThat(job.getStatus()).isSameAs(JobStatus.INITIAL);
    }
}
