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

import java.nio.file.Path;

/**
 * A factory interface for producing predefined tasks.
 */
public interface TaskFactory {

    /**
     * Creates an instance of {@link EchoTask}.
     * 
     * @param message the message to echo, cannot be {@code null}.
     * @return newly created task.
     * @throws NullPointerException if given message is {@code null}.
     */
    EchoTask createEchoTask(String message);
    
    /**
     * Creates a builder for building {@link ScriptTask}.
     * The path must be visible for the workflow runner.
     * 
     * @param scriptPath the path to the script to run.
     * @return newly created builder.
     * @throws NullPointerException if given scriptPath is {@code null}.
     */
    ScriptTaskBuilder buildShellTask(Path scriptPath);
}
