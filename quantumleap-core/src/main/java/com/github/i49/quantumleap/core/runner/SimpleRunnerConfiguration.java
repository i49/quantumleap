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
package com.github.i49.quantumleap.core.runner;

import static com.github.i49.quantumleap.core.common.Preconditions.checkNotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.github.i49.quantumleap.api.workflow.RunnerConfiguration;

/**
 * A simple implementation of {@link RunnerConfiguration}.
 */
public class SimpleRunnerConfiguration implements RunnerConfiguration {

    private final Map<String, Object> properties;
    
    public SimpleRunnerConfiguration() {
        this.properties = new HashMap<>();
        setProperty(WORKING_DIRECTORY, Paths.get(".").toAbsolutePath().normalize());
        setProperty(WORKING_DIRECTORY_RESET, false);
    }
    
    @Override
    public Optional<Object> getProperty(String name) {
        checkNotNull(name, "name");
        return Optional.ofNullable(properties.get(name));
    }
 
    @Override
    public RunnerConfiguration setProperty(String name, Object value) {
        checkNotNull(name, "name");
        this.properties.put(name, value);
        return this;
    }
    
    @Override
    public RunnerConfiguration withDirectory(Path path) {
        return withDirectory(path, false);
    }

    @Override
    public RunnerConfiguration withDirectory(Path path, boolean reset) {
        checkNotNull(path, "path");
        setProperty(WORKING_DIRECTORY, path.toAbsolutePath().normalize());
        setProperty(WORKING_DIRECTORY_RESET, reset);
        return this;
    }
}
