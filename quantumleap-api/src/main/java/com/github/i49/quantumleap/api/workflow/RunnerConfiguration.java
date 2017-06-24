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

import java.nio.file.Path;

/**
 * Configuration of {@link WorkflowRunner}.
 */
public interface RunnerConfiguration {
    
    /**
     * Specifies the working directory for the workflow runner.
     * 
     * @param path the path to the working directory.
     * @return this configuration.
     * @throws NullPointerException if given {@code path} is {@code null}.
     */
    RunnerConfiguration withDirectory(Path path);

    /**
     * Specifies the working directory for the workflow runner.
     * 
     * @param path the path to the working directory.
     * @param clean {@code true} if all contents of the directory are to be cleaned.  
     * @return this configuration.
     * @throws NullPointerException if given {@code path} is {@code null}.
     */
    RunnerConfiguration withDirectory(Path path, boolean clean);
}
