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
package io.github.i49.unite.api.workflow;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Configuration of {@link WorkflowRunner}.
 */
public interface RunnerConfiguration {
    
    static final String WORKING_DIRECTORY = "runner.working.directory";
    static final String WORKING_DIRECTORY_RESET = "runner.working.directory.reset";
    
    /**
     * Returns the value of the specified configuration property.
     * 
     * @param name the name of the property to retrieve.
     * @return the value of the requested property.
     * @throws NullPointerException if given {@code name} is {@code null}.
     */
    Optional<Object> getProperty(String name);
    
    /**
     * Sets the specified configuration property to a given value.
     * 
     * @param name the name of the property to be set.
     * @param value the new value of the property to be set.
     * @return this configuration.
     * @throws NullPointerException if given {@code name} is {@code null}.
     */
    RunnerConfiguration setProperty(String name, Object value);
    
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
     * @param reset {@code true} if the directory are to be reset.  
     * @return this configuration.
     * @throws NullPointerException if given {@code path} is {@code null}.
     */
    RunnerConfiguration withDirectory(Path path, boolean reset);
}
