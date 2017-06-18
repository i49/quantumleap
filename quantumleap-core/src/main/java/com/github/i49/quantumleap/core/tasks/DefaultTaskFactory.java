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

import com.github.i49.quantumleap.api.tasks.EchoTask;
import com.github.i49.quantumleap.api.tasks.TaskFactory;

/**
 * The default implementation of {@link TaskFactory}.
 */
public class DefaultTaskFactory implements TaskFactory {

    @Override
    public EchoTask createEchoTask(String message) {
        checkNotNull(message, "message");
        return new EchoTaskImpl(message);
    }
}
