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

package io.github.i49.unite.server.runner;

import javax.sql.DataSource;

import io.github.i49.unite.core.common.Configurations;
import io.github.i49.unite.core.storage.StorageConfiguration;
import io.github.i49.unite.core.storage.WorkflowStorage;
import io.github.i49.unite.core.storage.util.DirectDataSource;
import io.github.i49.unite.core.storage.util.WorkflowStorageBuilder;

/**
 * Factory for creating workflow runner.
 * 
 * @author i49
 */
public class RunnerFactory {
    
    /**
     * Creates a workflow runner with loaded configuration.
     *
     * @return newly created workflow.
     */
    public WorkflowRunner createRunner() {
        ServerConfiguration config = Configurations.load(ServerConfiguration.class);
        WorkflowStorage storage = createStorage(config.getRepository());
        return new SerialWorkflowRunner(config, storage);
    }
    
    private WorkflowStorage createStorage(StorageConfiguration config) {
        DataSource dataSource = new DirectDataSource(
                config.getUrl(), config.getUsername(), config.getPassword()
                );
        WorkflowStorageBuilder builder = new WorkflowStorageBuilder();
        return builder
            .withDataSource(dataSource)
            .build();
    }
}
