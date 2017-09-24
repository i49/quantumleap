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

package io.github.i49.unite.server.bootstrap;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuration for workflow runner.
 * 
 * @author i49
 */
public class ServerConfiguration {

    private StorageConfiguration repository;
    private RunnerConfiguration runner;
    
    public StorageConfiguration getRepository() {
        return repository;
    }
    
    public void setRepository(StorageConfiguration config) {
        this.repository = config;
    }
    
    public RunnerConfiguration getRunner() {
        return runner;
    }
    
    public void setRunner(RunnerConfiguration config) {
        this.runner = config;
    }
    
    public static class RunnerConfiguration {
        
        private String directory;
        
        public RunnerConfiguration() {
            this.directory = ".";
        }
        
        public String getDirectory() {
            return directory;
        }
        
        public Path getDirectoryAsPath() {
            return Paths.get(getDirectory()).toAbsolutePath().normalize();
        }
        
        public void setDirectory(String directory) {
            this.directory = directory;
        }
    }
}
