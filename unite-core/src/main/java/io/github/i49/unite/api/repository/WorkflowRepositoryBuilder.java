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

package io.github.i49.unite.api.repository;

import javax.sql.DataSource;

import io.github.i49.unite.api.spi.WorkflowServiceProvider;

/**
 * Builder for building an instance of {@link WorkflowRepository}.
 * 
 * @author i49
 */
public interface WorkflowRepositoryBuilder {
    
    /**
     * Creates new instance of this type.
     * 
     * @return newly created instance.
     */
    static WorkflowRepositoryBuilder newInstance() {
        return WorkflowServiceProvider.provide().creteRepositoryBuilder();
    }
    
    /**
     * Specifies the data source of the repository.
     * 
     * @param dataSource the data source of the repository.
     * @return this builder.
     * @throws NullPointerException if one or more parameters are {@code null}.
     */
    WorkflowRepositoryBuilder withDataSource(DataSource dataSource);
    
    WorkflowRepositoryBuilder withUrl(String url);
    
    WorkflowRepositoryBuilder withCredential(String username, String password);
  
    /**
     * Enables the option for repository.
     * 
     * @param option the option to enable.
     * @return this builder.
     * @throws NullPointerException if {@code option} is {@code null}.
     */
    WorkflowRepositoryBuilder withOption(RepositoryOption option);

    /**
     * Builds new repository.
     * 
     * @return newly created instance of {@link WorkflowRepository}.
     */
    WorkflowRepository build();
}
