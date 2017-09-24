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

import javax.sql.DataSource;

import org.junit.rules.ExternalResource;

import io.github.i49.unite.api.repository.RepositoryOption;
import io.github.i49.unite.api.repository.WorkflowRepository;
import io.github.i49.unite.api.repository.WorkflowRepositoryBuilder;

/**
 * @author i49
 */
public class RepositoryResource extends ExternalResource {

    private WorkflowRepository repository;
    
    public WorkflowRepository getRepository() {
        return repository;
    }

    @Override
    protected void before() {
        DataSource dataSource = DataSources.get();
        this.repository = buildRepository(dataSource);
    }
    
    @Override
    protected void after() {
        this.repository = null;
    }
    
    private static WorkflowRepository buildRepository(DataSource dataSource) {
        return WorkflowRepositoryBuilder.newInstance()
            .withDataSource(dataSource)
            .withOption(RepositoryOption.FORMAT)
            .build();
    }
}
