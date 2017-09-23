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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.github.i49.unite.api.TestDataSource;
import io.github.i49.unite.api.repository.WorkflowRepository;
import io.github.i49.unite.api.repository.WorkflowRepositoryBuilder;
import io.github.i49.unite.api.workflow.Job;
import io.github.i49.unite.api.workflow.JobStatus;

/**
 * Unit test of {@link Job}.
 * 
 * @author i49
 */
public class JobTest {
    
    private static final DataSource dataSource = new TestDataSource();

    private static WorkflowRepository repository;
    private WorkflowFactory workflowFactory;
    
    @BeforeClass
    public static void setUpOnce() {
        repository = WorkflowRepositoryBuilder.newInstance()
                .withDataSource(dataSource).build();
    }

    @Before
    public void setUp() {
        workflowFactory = WorkflowFactory.newInstance();
        repository.clear();
    }
  
    /**
     * Registers a job with repository.
     * 
     * @param job the job to register.
     */
    private void registerJob(Job job) {
        Workflow workflow = workflowFactory.createWorkflowBuilder("workflow1").jobs(job).build();
        repository.addWorkflow(workflow);
    }
    
    /* hasId() */
    
    @Test
    public void hasId_shouldReturnFalseByDefault() {
        Job job = workflowFactory.createJobBuilder("job1").build();
        assertThat(job.hasId()).isFalse();
    }

    @Test
    public void hasId_shouldReturnAfterRegistration() {
        Job job = workflowFactory.createJobBuilder("job1").build();
        registerJob(job);
        assertThat(job.hasId()).isTrue();
    }
    
    /* getId() */

    @Test
    public void getId_shouldThrowExceptionByDefault() {
        Job job = workflowFactory.createJobBuilder("job1").build();
        Throwable thrown = catchThrowable(() -> {
            job.getId();
        });
        assertThat(thrown).isInstanceOf(NoSuchElementException.class);
    }
    
    @Test
    public void getId_shouldReturnValidIdAfterRegistration() {
        Job job = workflowFactory.createJobBuilder("job1").build();
        registerJob(job);
        assertThat(job.getId()).isGreaterThanOrEqualTo(0);
    }

    /* getName() */
    
    @Test
    public void getName_shouldReturnNameOfJob() {
        Job job = workflowFactory.createJobBuilder("job1").build();
        assertThat(job.getName()).isEqualTo("job1");
        
        registerJob(job);
        job = repository.findJobById(job.getId());
        assertThat(job.getName()).isEqualTo("job1");
    }
    
    /* getParameters() */
    
    @Test
    public void getParameters_shouldReturnEmptyMapByDefault() {
        Job job = workflowFactory.createJobBuilder("job1").build();
        assertThat(job.getInputParameters()).isEmpty();

        registerJob(job);
        job = repository.findJobById(job.getId());
        assertThat(job.getInputParameters()).isEmpty();
    }
    
    private Map<String, Object> createJobParameters() {
        Map<String, Object> p = new HashMap<>();
        p.put("firstName", "John");
        p.put("lastName", "Smith");
        p.put("isAlive", true);
        p.put("age", 25);
        p.put("spouse", null);
        p.put("hobbies", Arrays.asList("ice skating", "jigsaw puzzles"));
        Map<String, Object> address = new HashMap<>();
        address.put("city", "New York");
        address.put("state", "NY");
        p.put("address", address);
        return p;
    }
    
    @Test
    public void getParameters_shouldReturnStoredParameters() {
        Job job = workflowFactory.createJobBuilder("job1").input(createJobParameters()).build();
        assertOnJobParameters(job);

        registerJob(job);
        job = repository.findJobById(job.getId());
        assertOnJobParameters(job);
    }
    
    private void assertOnJobParameters(Job job) {
        Map<String, Object> p = job.getInputParameters();
        assertThat(p).isNotNull().isNotEmpty();
        assertThat(p.get("firstName")).isInstanceOf(String.class).isEqualTo("John");
        assertThat(p.get("lastName")).isInstanceOf(String.class).isEqualTo("Smith");
        assertThat(p.get("isAlive")).isInstanceOf(Boolean.class).isEqualTo(Boolean.TRUE);
        assertThat(p.get("age")).isInstanceOf(Integer.class).isEqualTo(Integer.valueOf(25));
        assertThat(p.get("spouse")).isNull();
        
        assertThat(p.get("hobbies")).isInstanceOf(List.class);
        @SuppressWarnings("unchecked")
        List<String> hobbies = (List<String>)p.get("hobbies");
        assertThat(hobbies).hasSize(2).containsExactly("ice skating", "jigsaw puzzles");
        
        assertThat(p.get("address")).isInstanceOf(Map.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> address = (Map<String, Object>)p.get("address");
        assertThat(address.get("city")).isInstanceOf(String.class).isEqualTo("New York");
        assertThat(address.get("state")).isInstanceOf(String.class).isEqualTo("NY");
    }
    
    /* getStatus() */

    @Test
    public void getStatus_shouldReturnInitialByDefault() {
        // given
        Job job = workflowFactory.createJobBuilder("job1").build();
        // when
        JobStatus status = job.getStatus();
        // then
        assertThat(status).isSameAs(JobStatus.INITIAL);
    }
    
    @Test
    public void getStatus_shouldReturnReadyBeforeRun() {
        // given
        Job job = workflowFactory.createJobBuilder("job1").build();
        registerJob(job);
        job = repository.findJobById(job.getId());
        // when
        JobStatus status = job.getStatus();
        // then
        assertThat(status).isSameAs(JobStatus.READY);
    }
}
