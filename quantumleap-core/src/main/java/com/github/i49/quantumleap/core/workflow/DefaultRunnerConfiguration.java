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
package com.github.i49.quantumleap.core.workflow;

import static com.github.i49.quantumleap.core.common.Preconditions.checkNotNull;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.i49.quantumleap.api.workflow.RunnerConfiguration;

public class DefaultRunnerConfiguration implements RunnerConfiguration {

    Path workDirectory;
    boolean clean;
    
    public DefaultRunnerConfiguration() {
        this.workDirectory = Paths.get(".").toAbsolutePath().normalize();
        this.clean = false;
    }
    
    @Override
    public RunnerConfiguration withDirectory(Path path) {
        return withDirectory(path, false);
    }

    @Override
    public RunnerConfiguration withDirectory(Path path, boolean clean) {
        checkNotNull(path, "path");
        this.workDirectory = path.toAbsolutePath().normalize();
        this.clean = clean;
        return this;
    }
}
