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
import java.util.List;

/**
 * A task for executing given command or script.
 */
public interface ShellTask extends Task {

    /**
     * Returns the commands to be executed by shell.
     * 
     * @return the commands to be executed, can be {@code null}.
     */
    List<String> getCommands();
    
    /**
     * Returns the path to the script to be executed by shell.
     * 
     * @return the path to the script, can be {@code null}.
     */
    Path getScriptPath();
}
