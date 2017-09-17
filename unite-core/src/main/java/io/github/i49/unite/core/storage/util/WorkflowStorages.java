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

package io.github.i49.unite.core.storage.util;

import io.github.i49.unite.core.storage.StorageConfiguration;
import io.github.i49.unite.core.storage.WorkflowStorage;
import io.github.i49.unite.core.storage.jdbc.JdbcWorkflowStorage;

/**
 * Utility class for {@link WorkflowStorage}.
 */
public final class WorkflowStorages {
    
    public static WorkflowStorage create(StorageConfiguration config) {
        return new JdbcWorkflowStorage(config);
    }

    private WorkflowStorages() {
    }
}