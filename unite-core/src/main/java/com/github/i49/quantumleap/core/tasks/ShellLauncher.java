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

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.github.i49.quantumleap.api.base.Platform;
import com.github.i49.quantumleap.api.base.WorkflowException;

/**
 * A helper class for launching platform-dependent shell.
 */
abstract class ShellLauncher {

    static ShellLauncher get(Platform platform) {
        switch (platform) {
        case WINDOWS:
            return new WindowsShellLauncher();
        default:
            break;
        }
        // TODO:
        throw new UnsupportedOperationException();
    }
    
    private Path directory;
    
    /**
     * Assigns the current working directory.
     * 
     * @param directory the current working directory
     * @return this launcher.
     */
    ShellLauncher setDirectory(Path directory) {
        this.directory = directory;
        return this;
    }
    
    void launchScript(String scriptPath, List<String> arguments) {
        List<String> commands = buildCommands(scriptPath, arguments);
        ProcessBuilder builder = new ProcessBuilder(commands);
        builder.directory(this.directory.toFile());
        try {
            Process process = builder.start();
            process.waitFor();
        } catch (IOException e) {
            // TODO
            throw new WorkflowException("", e);
        } catch (InterruptedException e) {
            // TODO
            throw new WorkflowException("", e);
        }
    }
    
    protected abstract List<String> buildCommands(String scriptPath, List<String> arguments);

    /**
     * {@link ShellLauncher} for Windows platform.
     */
    private static class WindowsShellLauncher extends ShellLauncher {
        
        @Override
        protected List<String> buildCommands(String scriptPath, List<String> arguments) {
            List<String> commands = new ArrayList<>();
            commands.add("cmd.exe");
            commands.add("/C");
            commands.add(scriptPath.toString());
            if (arguments.size() > 0) {
                commands.addAll(arguments);
            }
            return commands;
        }
    }
 }
