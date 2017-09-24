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

package io.github.i49.unite.server;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sql.DataSource;

import org.junit.rules.ExternalResource;

import io.github.i49.unite.server.runner.WorkflowRunner;
import io.github.i49.unite.server.runner.WorkflowRunnerBuilder;

/**
 * @author i49
 */
public class RunnerResource extends ExternalResource {
    
    private static final Path DIRECTORY = Paths.get("target/work");
    
    private WorkflowRunner runner;

    public WorkflowRunner getRunner() {
        return runner;
    }
    
    @Override
    protected void before() {
        DataSource dataSource = DataSources.get();
        this.runner = buildRunner(dataSource);
    }
    
    @Override
    protected void after() {
        this.runner = null;
    }
    
    private static WorkflowRunner buildRunner(DataSource dataSource) {
        return new WorkflowRunnerBuilder()
                .withDataSource(dataSource)
                .withDirectory(DIRECTORY)
                .build();
    }
}
