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
package io.github.i49.unite.core.tasks;

import static io.github.i49.unite.core.common.Preconditions.checkNotNull;

import java.nio.file.Path;

import io.github.i49.unite.api.tasks.EchoTask;
import io.github.i49.unite.api.tasks.ScriptTaskBuilder;
import io.github.i49.unite.api.tasks.TaskFactory;

/**
 * The default implementation of {@link TaskFactory}.
 */
public class DefaultTaskFactory implements TaskFactory {
    
    /**
     * Constructs this factory.
     */
    public DefaultTaskFactory() {
    }

    @Override
    public EchoTask createEchoTask(String message) {
        checkNotNull(message, "message");
        return new EchoTaskImpl(message);
    }

    @Override
    public ScriptTaskBuilder createShellTaskBuilder(Path scriptPath) {
        checkNotNull(scriptPath, "scriptPath");
        return new ScriptTaskImpl.Builder(scriptPath);
    }
}
