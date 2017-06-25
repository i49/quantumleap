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
package com.github.i49.quantumleap.api.workflow;

import java.util.Map;

import com.github.i49.quantumleap.api.tasks.Task;

/**
 * Builder interface for building an instance of {@link Job}.
 */
public interface JobBuilder {
    
    /**
     * Specifies the dependencies of the job.
     * 
     * @param jobs the dependencies.
     * @return this builder.
     * @throws NullPointerException if one or more {@code jobs} are {@code null}.
     */
    JobBuilder dependOn(Job... jobs);
    
    /**
     * Adds a parameter of this job.
     * 
     * @param name the name of the parameter, cannot be {@code null}.
     * @param value the value of the parameter cannot be {@code null}.
     * @return this builder.
     * @throws NullPointerException if one or more parameters are {@code null}.
     */
    JobBuilder parameter(String name, Object value);

    /**
     * Adds parameters of this job.
     * 
     * @param parameters the map containing names and values of the job parameters.
     * @return this builder.
     * @throws NullPointerException if given {@code parameters} is {@code null}.
     */
    JobBuilder parameters(Map<String, Object> parameters);
    
    /**
     * Specifies the tasks executed in the job.
     * 
     * @param tasks the tasks executed in the order as specified.
     * @return this builder.
     * @throws NullPointerException if one or more {@code tasks} are {@code null}.
     */
    JobBuilder tasks(Task... tasks);
    
    /**
     * Returns the {@link Job} built by this builder.
     * 
     * @return the instance of {@link Job}.
     */
    Job get();
}
