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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * All SQL commands used by {@link JdbcWorkflowRepository}.
 */
enum SqlCommand {

    COUNT_JOBS("SELECT COUNT(1) FROM job"),
    COUNT_JOBS_BY_STATUS("SELECT COUNT(1) FROM job WHERE job_status = ?"),
    COUNT_WORKFLOWS("SELECT COUNT(1) FROM workflow"),
    
    DELETE_WORKFLOWS("DELETE FROM workflow"),
    DELETE_JOBS("DELETE FROM job"),
    DELETE_JOB_DEPENDENCY("DELETE FROM job_dependency"),
    DELETE_TASKS("DELETE FROM task"),
    
    FIND_JOBS_BY_STATUS("SELECT * FROM job WHERE job_status = ? ORDER BY job_id"),
    FIND_FIRST_JOB_BY_STATUS("SELECT * FROM job WHERE job_status = ? ORDER BY job_id LIMIT 1"),
    FIND_JOB_STATUS_BY_ID("SELECT job_status FROM job WHERE job_id = ?"),
    FIND_DEPENDANT_JOBS("SELECT job_id FROM job_dependency WHERE dependency_id = ?"),
    FIND_TASK("SELECT * FROM task WHERE job_id = ? ORDER BY sequence_number"),

    INSERT_JOB("INSERT INTO job (job_name, job_status, workflow_id) VALUES(?, ?, ?)"),
    INSERT_JOB_DEPENDENCY("INSERT INTO job_dependency (job_id, dependency_id) VALUES(?, ?)"),
    INSERT_TASK("INSERT INTO task (job_id, sequence_number, class_name, parameters) VALUES(?, ?, ?, ?)"),
    INSERT_WORKFLOW("INSERT INTO workflow (workflow_name) VALUES(?)"),
    
    UPDATE_JOB_STATUS("UPDATE job SET job_status = ? WHERE job_id = ?"),
    UPDATE_JOB_STATUS_IF_READY(
            "UPDATE job j SET job_status = 'READY' "
            + "WHERE job_id = ? AND NOT EXISTS ("
            + "SELECT 1 FROM job_dependency d "
            + "INNER JOIN job dj ON dj.job_id = d.dependency_id AND dj.job_status <> 'COMPLETED' "
            + "WHERE d.job_id = j.job_id) "
    )
    ;

    private final String sql;

    /**
     * Constructs this command.
     * 
     * @param sql the SQL statement assigned to this command.
     */
    private SqlCommand(String sql) {
        this.sql = sql;
    }

    /**
     * Creates a prepared statement for this command.
     * 
     * @param connection the database connection.
     * @return newly created prepared statement.
     * @throws SQLException if data access error has occurred. 
     */
    public PreparedStatement prepare(Connection connection) throws SQLException {
        if (this == INSERT_WORKFLOW || this == INSERT_JOB) {
            return connection.prepareStatement(this.sql, Statement.RETURN_GENERATED_KEYS);
        } else {
            return connection.prepareStatement(this.sql);
        }
    }
}
