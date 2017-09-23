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

package io.github.i49.unite.core.storage.jdbc;

import static io.github.i49.unite.core.common.Message.REPOSITORY_ACCESS_ERROR_OCCURRED;

import java.sql.Connection;
import java.sql.SQLException;

import io.github.i49.unite.api.base.WorkflowException;

/**
 * Database session.
 * 
 * @author i49
 */
public class JdbcSession implements AutoCloseable {
    
    private final Connection connection;
    
    public JdbcSession(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void close() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            throw new WorkflowException(REPOSITORY_ACCESS_ERROR_OCCURRED.toString(), e);
        }
    }
    
    /**
     * Returns the connection of this session;
     * 
     * @return the connection to the database.
     */
    public Connection getConnection() {
        return connection;
    }
}
