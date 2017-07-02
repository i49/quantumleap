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

import java.io.PrintStream;
import java.nio.file.Path;

import com.github.i49.quantumleap.api.base.ParameterSet;
import com.github.i49.quantumleap.api.base.Platform;

/**
 * The context of a running task.
 */
public interface TaskContext {
    
    /**
     * Returns the directory for the current job.
     * 
     * @return the directory for the current job, never be {@code null}. 
     */
    Path getJobDirectory();
    
    /**
     * Returns the input for the current job.
     * 
     * @return the input for the current job, never be {@code null}.
     */
    ParameterSet getInputParameters();

    /**
     * Returns the output of the current job.
     * 
     * @return the output of the current job, never be {@code null} and can be modified.
     */
    ParameterSet getOutputParameters();

    /**
     * Returns the current platform.
     * 
     * @return the current platform, never be {@code null}.
     */
    Platform getPlatform();
    
    /**
     * Returns the standard output stream assigned to the current job.
     * 
     * @return the standard output stream, never be {@code null}.
     */
    PrintStream getStandardStream();
}
