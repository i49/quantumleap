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
package com.github.i49.quantumleap.core.tasks;

import static com.github.i49.quantumleap.core.common.Preconditions.checkNotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.i49.quantumleap.api.tasks.ScriptTask;
import com.github.i49.quantumleap.api.tasks.ScriptTaskBuilder;
import com.github.i49.quantumleap.api.tasks.TaskContext;

/**
 * An implementation of {@link ScriptTask}.
 */
public class ScriptTaskImpl implements ScriptTask {
    
    private Path scriptPath;
    private List<String> arguments;
    
    public ScriptTaskImpl() {
    }
    
    @Override
    public void run(TaskContext context) {
        ShellLauncher launcher = ShellLauncher.get(context.getPlatform());
        launcher.setDirectory(context.getJobDirectory());
        Path scriptPath = getScriptPath().toAbsolutePath();
        launcher.launchScript(scriptPath.toString(), this.arguments);
    }

    @Override
    public List<String> getArguments() {
        return arguments;
    }

    @Override
    public Path getScriptPath() {
        return scriptPath;
    }
    
    public void setScriptPath(Path scriptPath) {
        assert(scriptPath != null);
        this.scriptPath = scriptPath;
    }
    
    public void setArguments(List<String> arguments) {
        this.arguments = Collections.unmodifiableList(arguments);
    }

    /**
     * An implementation of {@link ScriptTaskBuilder}.
     */
    public static class Builder implements ScriptTaskBuilder {
        
        private final Path scriptPath;
        private final List<String> arguments = new ArrayList<>();
        
        Builder(Path scriptPath) {
            assert(scriptPath != null);
            this.scriptPath = scriptPath;
        }
        
        @Override
        public ScriptTaskBuilder arguments(String... arguments) {
            checkNotNull(arguments, "arguments");
            for (String argument: arguments) {
                this.arguments.add(argument);
            }
            return this;
        }
        
        @Override
        public ScriptTask build() {
            ScriptTaskImpl task = new ScriptTaskImpl();
            task.setScriptPath(this.scriptPath);
            task.setArguments(this.arguments);
            return task;
        }
    }
}
