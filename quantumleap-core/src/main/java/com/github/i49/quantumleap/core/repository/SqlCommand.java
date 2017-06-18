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
    DELETE_JOBS("DELETE FROM JOB"),
    FIND_JOBS_BY_STATUS("SELECT * FROM job WHERE job_status = ? ORDER BY job_id"),
    FIND_FIRST_JOB_BY_STATUS("SELECT * FROM job WHERE job_status = ? ORDER BY job_id LIMIT 1"),
    FIND_TASK("SELECT * FROM task WHERE job_id = ? ORDER BY sequence_number"),
    INSERT_JOB("INSERT INTO job (job_name, job_status, workflow_id) VALUES(?, ?, ?)"),
    INSERT_TASK("INSERT INTO task (job_id, sequence_number, class_name, task_params) VALUES(?, ?, ?, ?)"),
    INSERT_WORKFLOW("INSERT INTO workflow (workflow_name) VALUES(?)"),
    UPDATE_JOB_STATUS("UPDATE job SET job_status = ? WHERE job_id = ?"),
    ;

    private final String sql;

    private SqlCommand(String sql) {
        this.sql = sql;
    }

    public PreparedStatement prepare(Connection connection) throws SQLException {
        if (this == INSERT_WORKFLOW || this == INSERT_JOB) {
            return connection.prepareStatement(this.sql, Statement.RETURN_GENERATED_KEYS);
        } else {
            return connection.prepareStatement(this.sql);
        }
    }
}
