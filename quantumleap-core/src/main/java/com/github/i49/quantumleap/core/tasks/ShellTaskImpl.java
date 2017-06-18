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

import com.github.i49.quantumleap.api.tasks.ShellTask;
import com.github.i49.quantumleap.api.tasks.ShellTaskBuilder;
import com.github.i49.quantumleap.api.tasks.TaskContext;

/**
 *
 */
public class ShellTaskImpl implements ShellTask {
    
    private final String[] commands;
    
    public ShellTaskImpl(Builder builder) {
        this.commands = builder.commands;
    }

    @Override
    public void run(TaskContext context) {
        // TODO Auto-generated method stub
    }

    @Override
    public String[] getCommands() {
        return commands;
    }

    public static class Builder implements ShellTaskBuilder {
        
        private String[] commands;

        @Override
        public ShellTaskBuilder commands(String... commands) {
            // TODO Auto-generated method stub
            this.commands = commands;
            return this;
        }

        @Override
        public ShellTask get() {
            return new ShellTaskImpl(this);
        }
    }
}
