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

package io.github.i49.unite.core.repository;

import static io.github.i49.unite.core.common.Preconditions.*;

import javax.sql.DataSource;

import io.github.i49.unite.api.repository.WorkflowRepository;
import io.github.i49.unite.api.repository.WorkflowRepositoryBuilder;
import io.github.i49.unite.core.storage.WorkflowStorage;
import io.github.i49.unite.core.storage.util.WorkflowStorageBuilder;

/**
 * @author i49
 */
public class DefaultWorkflowRepositoryBuilder implements WorkflowRepositoryBuilder {

    private final WorkflowStorageBuilder storageBuilder = new WorkflowStorageBuilder();
    
    @Override
    public WorkflowRepositoryBuilder withDataSource(DataSource dataSource) {
        checkNotNull(dataSource, "dataSource");
        storageBuilder.withDataSource(dataSource);
        return this;
    }

    @Override
    public WorkflowRepositoryBuilder withUrl(String url) {
        checkNotNull(url, "url");
        return this;
    }
    
    @Override
    public WorkflowRepositoryBuilder withCredential(String username, String password) {
        checkNotNull(username, "username");
        checkNotNull(password, "password");
        return this;
    }
    
    @Override
    public WorkflowRepository build() {
        WorkflowStorage storage = storageBuilder.build();
        return new DefaultWorkflowRepository(storage);
    }
}
