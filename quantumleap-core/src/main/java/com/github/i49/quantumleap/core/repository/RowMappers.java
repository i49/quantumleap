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
package com.github.i49.quantumleap.core.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.i49.quantumleap.api.base.ParameterSet;
import com.github.i49.quantumleap.api.base.WorkflowException;
import com.github.i49.quantumleap.api.tasks.Task;
import com.github.i49.quantumleap.api.workflow.JobStatus;
import com.github.i49.quantumleap.api.workflow.ParameterSetMapper;
import com.github.i49.quantumleap.api.workflow.WorkflowStatus;
import com.github.i49.quantumleap.core.workflow.JobLink;
import com.github.i49.quantumleap.core.workflow.ManagedJob;
import com.github.i49.quantumleap.core.workflow.ManagedJobBuilder;
import com.github.i49.quantumleap.core.workflow.ManagedWorkflow;
import com.github.i49.quantumleap.core.workflow.ManagedWorkflowBuilder;
import com.github.i49.quantumleap.core.workflow.WorkflowFactory;

/**
 * A collection of the ResultSet mappers.
 */
class RowMappers {
    
    private final WorkflowFactory factory;
 
    private final Marshaller<String> textMarshaller;
    private final Marshaller<byte[]> binaryMarshaller;
    
    RowMappers(WorkflowFactory factory) {
        this.factory = factory;
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
    ManagedWorkflow mapToWorkflow(ResultSet rs) throws SQLException {
        final String name = rs.getString(2);
        ManagedWorkflowBuilder builder = factory.createWorkflowBuilder(name);
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
    ManagedJob mapToJob(ResultSet rs) throws SQLException {
        final long id = rs.getLong(1);
        final String name = rs.getString(2);
        final JobStatus status = JobStatus.valueOf(rs.getString(3));
        final ParameterSet inputParameters = unmarshal(rs.getBytes(4), ParameterSet.class);
        final ParameterSet outputParameters = unmarshal(rs.getBytes(5), ParameterSet.class);
        final String standardOutput = rs.getString(6);
        ManagedJobBuilder builder = factory.createJobBuilder(name);
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
    
    Task mapToTask(ResultSet rs) throws SQLException {
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
    RowMapper<JobLink> mappingToJobLink(ManagedJob targetJob) {
        return rs->{
            ManagedJob sourceJob = mapToJob(rs);
            byte[] bytes = rs.getBytes(9); 
            ParameterSetMapper mapper = unmarshal(bytes, ParameterSetMapper.class);
            return factory.createJobLink(sourceJob, targetJob, mapper);
        };
    }
    
    private <T> T unmarshal(byte[] bytes, Class<T> type) {
        return this.binaryMarshaller.unmarshal(bytes, type);
    }

    private <T> T unmarshal(String str, Class<? extends T> type) {
        return this.textMarshaller.unmarshal(str, type);
    }
}
