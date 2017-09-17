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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.yaml.snakeyaml.Yaml;

import io.github.i49.unite.core.storage.StorageConfiguration;
import io.github.i49.unite.core.storage.WorkflowStorage;
import io.github.i49.unite.core.storage.util.WorkflowStorages;

/**
 * Factory for creating workflow runner.
 * 
 * @author i49
 */
public class RunnerFactory {
    
    private static final String DEFAULT_CONFIGURATION_NAME = "unite.yaml";

    public RunnerFactory() {
    }
    
    /**
     * Creates a workflow runner with loaded configuration.
     *
     * @return newly created workflow.
     */
    public WorkflowRunner createRunner() {
        ServerConfiguration config = findAndLoadConfiguration();
        WorkflowStorage storage = createStorage(config.getRepository());
        return new SerialWorkflowRunner(config, storage);
    }
    
    private WorkflowStorage createStorage(StorageConfiguration config) {
        return WorkflowStorages.create(config);
    }
    
    private ServerConfiguration findAndLoadConfiguration() {
        Path path = Paths.get(DEFAULT_CONFIGURATION_NAME);
        try {
            if (Files.exists(path)) {
                return loadConfiguration(Files.newInputStream(path));
            } else {
                ClassLoader loader = ServerConfiguration.class.getClassLoader();
                InputStream stream = loader.getResourceAsStream(DEFAULT_CONFIGURATION_NAME);
                if (stream != null) {
                    return loadConfiguration(stream);
                }
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }
    
    private ServerConfiguration loadConfiguration(InputStream stream) throws IOException {
        Yaml yaml = new Yaml();
        try (InputStream input = stream) {
          return yaml.loadAs(input, ServerConfiguration.class);  
        }
    }
}
