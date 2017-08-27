/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
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
package com.github.i49.quantumleap.core.repository;

import static com.github.i49.quantumleap.core.common.Message.RESOURCE_IS_MISSING;
import static com.github.i49.quantumleap.core.common.Message.STATEMENT_IS_UNDEFINED;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.MissingResourceException;
import java.util.Properties;

/**
 * All SQL commands used by {@link JdbcWorkflowRepository}.
 */
enum SqlCommand {

    COUNT_JOBS,
    COUNT_JOBS_BY_STATUS,
    COUNT_WORKFLOWS,
    
    DELETE_JOB_LINKS,
    DELETE_JOBS,
    DELETE_TASKS,
    DELETE_WORKFLOWS,
    
    FIND_FIRST_JOB_BY_STATUS,
    FIND_JOB_BY_ID,
    FIND_JOB_STATUS_BY_ID,
    FIND_JOBS_BY_STATUS,
    FIND_LINKS_BY_TARGET,
    FIND_NEXT_JOBS,
    FIND_TASK,
    FIND_WORKFLOW_BY_ID,

    INSERT_JOB,
    INSERT_JOB_LINK,
    INSERT_TASK,
    INSERT_WORKFLOW,
    
    UPDATE_JOB,
    UPDATE_JOB_STATUS,
    UPDATE_JOB_STATUS_IF_READY
    ;

    private static final String RESOURCE_NAME = "sql.properties";
    private static final Properties props = loadStatements();

    /**
     * Creates a prepared statement for this command.
     * 
     * @param connection the database connection.
     * @return newly created prepared statement.
     * @throws SQLException if data access error has occurred. 
     */
    private PreparedStatement prepare(Connection connection) throws SQLException {
        if (this == INSERT_WORKFLOW || this == INSERT_JOB) {
            return connection.prepareStatement(getSql(), Statement.RETURN_GENERATED_KEYS);
        } else {
            return connection.prepareStatement(getSql());
        }
    }
    
    public Query getQuery(Connection connection) throws SQLException {
        PreparedStatement statement = prepare(connection);
        return new Query(statement);
    }
    
    public String getSql() {
        String sql = props.getProperty(name());
        if (sql == null) {
            throw new MissingResourceException(STATEMENT_IS_UNDEFINED.with(name()), getClass().getName(), name());
        }
        return sql;
    }
    
    private static Properties loadStatements() {
        Class<SqlCommand> theClass = SqlCommand.class;
        try (InputStream inStream = theClass.getResourceAsStream(RESOURCE_NAME)) {
            Properties props = new Properties();
            props.load(inStream);
            return props;
        } catch (IOException e) {
            throw new MissingResourceException(RESOURCE_IS_MISSING.with(RESOURCE_NAME), theClass.getName(), null);
        }
    }
}
