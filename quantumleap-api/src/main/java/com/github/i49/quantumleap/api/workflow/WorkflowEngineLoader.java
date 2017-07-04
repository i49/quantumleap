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
package com.github.i49.quantumleap.api.workflow;

import java.util.Iterator;
import java.util.ServiceLoader;

import com.github.i49.quantumleap.api.base.WorkflowException;

/**
 * A helper class which loads the {@link WorkflowEngine}.
 */
class WorkflowEngineLoader {

    private static final ThreadLocalEngine engines = new ThreadLocalEngine();

    /**
     * Loads the workflow engine.
     * 
     * @return the loaded workflow engine.
     */
    public static WorkflowEngine getEngine() {
        WorkflowEngine engine = engines.get();
        if (engine != null) {
            return engine;
        } else {
            throw new WorkflowException("No service providers were found.");
        }
    }

    private static class ThreadLocalEngine extends ThreadLocal<WorkflowEngine> {

        @Override
        protected WorkflowEngine initialValue() {
            ServiceLoader<WorkflowEngine> loader = ServiceLoader.load(WorkflowEngine.class);
            Iterator<WorkflowEngine> it = loader.iterator();
            if (it.hasNext()) {
                return it.next();
            } else {
                return null;
            }
        }
    }
}
