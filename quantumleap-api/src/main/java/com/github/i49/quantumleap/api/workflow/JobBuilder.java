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

import com.github.i49.quantumleap.api.tasks.Task;

/**
 * Builder interface for building an instance of {@link Job}.
 */
public interface JobBuilder {

    /**
     * Specifies the first task of the job.
     * 
     * @param task
     *            the task to execute.
     * @return this builder.
     */
    JobBuilder start(Task task);

    /**
     * Specifies the next task of the job.
     * 
     * @param task
     *            the task to execute.
     * @return this builder.
     */
    JobBuilder next(Task task);

    /**
     * Returns the {@link Job} built by this builder.
     * 
     * @return an instance of {@link Job}.
     */
    Job get();
}
