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

import io.github.i49.unite.core.workflow.ManagedJob;
import io.github.i49.unite.core.workflow.ManagedWorkflow;

public class BasicDirectoryLayoutStrategy implements DirectoryLayoutStrategy {
    
    private final Path basePath;
    
    private static final String WORKFLOW_DIRECTORY = "workflows";
    private static final String LOG_DIRECTORY = "logs";

    private static final String WORKFLOW_PREFIX = "w";
    private static final String JOB_PREFIX = "j";
    
    public BasicDirectoryLayoutStrategy(Path basePath) {
        this.basePath = basePath;
    }

    @Override
    public Path getJobDirectory(ManagedWorkflow workflow, ManagedJob job) {
        Path path = Paths.get(
                WORKFLOW_DIRECTORY,
                WORKFLOW_PREFIX + String.valueOf(workflow.getId()), 
                JOB_PREFIX + String.valueOf(job.getId()));
        return basePath.resolve(path);
    }

    @Override
    public Path getLogDirectory() {
        return basePath.resolve(LOG_DIRECTORY);
    }
}
