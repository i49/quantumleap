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
package com.github.i49.quantumleap.api.tasks;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import com.github.i49.quantumleap.api.workflow.Job;
import com.github.i49.quantumleap.api.workflow.JobStatus;

/**
 * Unit test of {@link EchoTask}.
 */
public class EchoTaskTest extends BaseTaskTest {

    @Test
    public void run_shouldEchoMessage() {
        String message = "The quick brown fox jumps over the lazy dog";
        Task task = factory.createEchoTask(message);
        Job job = runTask(task);
        
        job = repository.findJobById(job.getId()).get();
        assertThat(job.getStatus()).isSameAs(JobStatus.COMPLETED);
        assertThat(job.getStandardOutput()).containsExactly(message);
    }
}
