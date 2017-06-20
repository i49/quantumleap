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

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.i49.quantumleap.api.tasks.ShellTask;
import com.github.i49.quantumleap.api.tasks.ShellTaskBuilder;
import com.github.i49.quantumleap.api.tasks.TaskContext;
import com.github.i49.quantumleap.core.common.Platform;

/**
 * An implementation of {@link ShellTask}.
 */
public class ShellTaskImpl implements ShellTask {
    
    private List<String> commands;
    private Path scriptPath;
    
    public ShellTaskImpl() {
    }
    
    @Override
    public void run(TaskContext context) {
        List<String> commands = buildCommands();
        ProcessBuilder builder = new ProcessBuilder(commands);
        startProcess(builder);
    }

    @Override
    public List<String> getCommands() {
        return commands;
    }
    
    @Override
    public Path getScriptPath() {
        return scriptPath;
    }

    public void setCommands(List<String> commands) {
        if (commands != null) {
            this.commands = Collections.unmodifiableList(commands);
        } else {
            this.commands = null;
        }
    }
    
    public void setScriptPath(Path scriptPath) {
        this.scriptPath = scriptPath;
    }

    private List<String> buildCommands() {
        Platform platform = Platform.getCurrent();
        if (platform == Platform.UNIX) {
            return buildCommandsForUnixPlatform();
        } else if (platform == Platform.WINDOWS) {
            return buildCommandsForWindowsPlatform();
        } else {
            // TODO:
            throw new UnsupportedOperationException();
        }
    }
    
    private List<String> buildCommandsForWindowsPlatform() {
        List<String> commands = new ArrayList<>();
        commands.add("cmd.exe");
        commands.add("/C");
        if (this.commands != null) {
            commands.addAll(this.commands);
        } else if (this.scriptPath != null){
            commands.add(this.scriptPath.toAbsolutePath().toString());
        }
        return commands;
    }

    private List<String> buildCommandsForUnixPlatform() {
        // TODO:
        return null;
    }
    
    private void startProcess(ProcessBuilder builder) {
        try {
            Process process = builder.start();
            process.waitFor();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static class Builder implements ShellTaskBuilder {
        
        private List<String> commands;
        private Path scriptPath;
        
        @Override
        public ShellTaskBuilder commands(String... commands) {
            this.commands = Arrays.asList(commands);
            return this;
        }
        
        @Override
        public ShellTaskBuilder script(Path scriptPath) {
            checkNotNull(scriptPath, "scriptPath");
            this.scriptPath = scriptPath;
            return this;
        }
        
        @Override
        public ShellTask get() {
            ShellTaskImpl task = new ShellTaskImpl();
            task.setCommands(commands);
            task.setScriptPath(scriptPath);
            return task;
        }
    }
}
