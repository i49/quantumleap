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
package io.github.i49.unite.core.storage.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import io.github.i49.unite.api.base.ParameterSet;
import io.github.i49.unite.api.base.WorkflowException;
import io.github.i49.unite.api.tasks.Task;
import io.github.i49.unite.api.workflow.JobStatus;
import io.github.i49.unite.api.workflow.ParameterSetMapper;
import io.github.i49.unite.api.workflow.WorkflowStatus;
import io.github.i49.unite.core.storage.BinaryMarshaller;
import io.github.i49.unite.core.storage.JsonBindingMarshaller;
import io.github.i49.unite.core.storage.Marshaller;
import io.github.i49.unite.core.workflow.JobLink;
import io.github.i49.unite.core.workflow.ManagedJob;
import io.github.i49.unite.core.workflow.ManagedJobBuilder;
import io.github.i49.unite.core.workflow.ManagedWorkflow;
import io.github.i49.unite.core.workflow.ManagedWorkflowBuilder;

/**
 * A collection of the ResultSet mappers.
 */
public class RowMappers {
    
    private final Marshaller<String> textMarshaller;
    private final Marshaller<byte[]> binaryMarshaller;
    
    public RowMappers() {
        this.textMarshaller = JsonBindingMarshaller.getInstance();
        this.binaryMarshaller = BinaryMarshaller.getInstance();
    }
    
    /**
     * Maps a {@link ResultSet} to a {@link ManagedWorkflow}.
     * 
     * @param rs the result set.
     * @return the newly created workflow.
     * @throws SQLException if a data access error has occurred.
     */
    public ManagedWorkflow mapToWorkflow(ResultSet rs) throws SQLException {
        final String name = rs.getString(2);
        ManagedWorkflowBuilder builder = new ManagedWorkflowBuilder(name);
        ManagedWorkflow workflow = builder.build();
        workflow.setId(rs.getLong(1));
        workflow.setStatus(WorkflowStatus.valueOf(rs.getString(3)));
        return workflow;
    }
    
    /**
     * Maps a {@link ResultSet} to a {@link ManagedJob}.
     * 
     * @param rs the result set.
     * @return the newly created job.
     * @throws SQLException if a data access error has occurred.
     */
    public ManagedJob mapToJob(ResultSet rs) throws SQLException {
        final long id = rs.getLong(1);
        final String name = rs.getString(2);
        final JobStatus status = JobStatus.valueOf(rs.getString(3));
        final ParameterSet inputParameters = unmarshal(rs.getBytes(4), ParameterSet.class);
        final ParameterSet outputParameters = unmarshal(rs.getBytes(5), ParameterSet.class);
        final String standardOutput = rs.getString(6);
        ManagedJobBuilder builder = new ManagedJobBuilder(name);
        if (inputParameters != null) {
            builder.input(inputParameters);
        }
        ManagedJob job = builder.build();
        job.setId(id);
        job.setStatus(status);
        job.setWorkflowId(rs.getLong(7));
        if (outputParameters != null) {
            job.setOutputParamters(outputParameters);
        }
        if (standardOutput != null) {
            job.setStandardOutput(unmarshal(standardOutput, String[].class));
        }
        return job;
    }
    
    public Task mapToTask(ResultSet rs) throws SQLException {
        final String className = rs.getString(3);
        final String params = rs.getString(4);
        try {
            Class<?> type = Class.forName(className);
            if (Task.class.isAssignableFrom(type)) {
                Object task = unmarshal(params, type);
                return (Task) task;
            } else {
                // TODO
                throw new WorkflowException("");
            }
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            throw new WorkflowException("", e);
        }
    }
    
    /**
     * Returns the mapper that maps a {@link ResultSet} to a {@link JobLink}.
     * 
     * @param targetJob the target job of the link.
     * @return the mapper.
     */
    public RowMapper<JobLink> mappingToJobLink(ManagedJob targetJob) {
        return rs->{
            ManagedJob sourceJob = mapToJob(rs);
            byte[] bytes = rs.getBytes(9); 
            ParameterSetMapper mapper = unmarshal(bytes, ParameterSetMapper.class);
            return new JobLink(sourceJob, targetJob, mapper);
        };
    }
    
    private <T> T unmarshal(byte[] bytes, Class<T> type) {
        return this.binaryMarshaller.unmarshal(bytes, type);
    }

    private <T> T unmarshal(String str, Class<? extends T> type) {
        return this.textMarshaller.unmarshal(str, type);
    }
}
