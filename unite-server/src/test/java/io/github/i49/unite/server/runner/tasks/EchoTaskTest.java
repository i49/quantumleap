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

import static org.assertj.core.api.Assertions.*;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import io.github.i49.unite.api.tasks.EchoTask;
import io.github.i49.unite.api.tasks.Task;
import io.github.i49.unite.api.workflow.Job;
import io.github.i49.unite.api.workflow.JobStatus;

/**
 * Unit test of {@link EchoTask}.
 */
public class EchoTaskTest {
    
    @ClassRule
    public static final TaskRunner runner = new TaskRunner();
    
    @Before
    public void setUp() {
        runner.reset();
    }

    @Test
    public void run_shouldEchoMessage() {
        // given
        String message = "The quick brown fox jumps over the lazy dog";
        Task task = runner.getFactory().createEchoTask(message);
        
        // when
        Job job = runner.runTask(task);
        
        // then
        assertThat(job.getStatus()).isSameAs(JobStatus.COMPLETED);
        assertThat(job.getStandardOutput()).containsExactly(message);
    }
}
