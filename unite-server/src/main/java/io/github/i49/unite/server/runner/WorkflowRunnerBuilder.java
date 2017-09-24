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

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sql.DataSource;

import io.github.i49.unite.core.storage.WorkflowStorage;
import io.github.i49.unite.core.storage.util.WorkflowStorageBuilder;

/**
 * @author i49
 */
public class WorkflowRunnerBuilder {
    
    private final WorkflowStorageBuilder storageBuilder = new WorkflowStorageBuilder();
    
    private Path directory;
    
    public WorkflowRunnerBuilder() {
        this.directory = Paths.get(".");
    }
    
    public WorkflowRunnerBuilder withDataSource(DataSource dataSource) {
        this.storageBuilder.withDataSource(dataSource);
        return this;
    }
    
    public WorkflowRunnerBuilder withDirectory(Path directory) {
        this.directory = directory;
        return this;
    }
    
    public WorkflowRunner build() {
        WorkflowStorage storage = this.storageBuilder.build();
        return new SerialWorkflowRunner(storage, getNormalizedDirectory());
    }
    
    private Path getNormalizedDirectory() {
        return this.directory.toAbsolutePath().normalize();
    }
}
