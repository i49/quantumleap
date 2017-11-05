/* 
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

package io.github.i49.unite.api.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

import io.github.i49.unite.api.base.WorkflowException;

/**
 * The provider of {@link WorkflowService} instance.
 * This type should not be directly used by the API users.
 * 
 * @author i49
 */
public final class WorkflowServiceProvider {

    private static ThreadLocal<WorkflowService> services = new ThreadLocalService();
    
    /**
     * Provides the instance of {@link WorkflowService}.
     * 
     * @return the instance of {@link WorkflowService}.
     * @throws WorkflowException if no service providers were found.
     */
    public static WorkflowService provide() {
        WorkflowService service = services.get();
        if (service != null) {
            return service;
        } else {
            throw new WorkflowException("No service providers were found.");
        }
    }
    
    private static class ThreadLocalService extends ThreadLocal<WorkflowService> {
        
        @Override
        protected WorkflowService initialValue() {
            ServiceLoader<WorkflowService> loader = ServiceLoader.load(WorkflowService.class);
            Iterator<WorkflowService> it = loader.iterator();
            return it.hasNext() ? it.next() : null;
        }
    }
    
    private WorkflowServiceProvider() {
    }
}
