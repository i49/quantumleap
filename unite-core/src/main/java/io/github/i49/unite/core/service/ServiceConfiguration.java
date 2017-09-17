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

package io.github.i49.unite.core.service;

import io.github.i49.unite.core.storage.StorageConfiguration;

/**
 * Configuration for workflow service.
 * 
 * @author i49
 */
public class ServiceConfiguration {
    
    private StorageConfiguration repository;

    public StorageConfiguration getRepository() {
        return repository;
    }

    public void setRepository(StorageConfiguration repository) {
        this.repository = repository;
    }
}
