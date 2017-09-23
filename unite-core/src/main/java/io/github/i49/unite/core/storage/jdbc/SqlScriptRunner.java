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

import static io.github.i49.unite.core.message.Message.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import io.github.i49.unite.api.base.WorkflowException;

/**
 * A helper class for running given SQL script.
 */
public class SqlScriptRunner {

    private final Connection connection;
    private final Dialect dialect;
    
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /**
     * Constructs this runner.
     * 
     * @param connection the established connection to the database. 
     */
    public SqlScriptRunner(Connection connection) {
        this.connection = connection;
        this.dialect = guessDialect(connection);
    }

    /**
     * Runs a script read from the specified resource file.
     * 
     * @param baseName the base name of the resource file.
     */
    public void runScript(String baseName) {
        String resourceName = getSqlResourceName(baseName, this.dialect);
        try (InputStream in = getClass().getResourceAsStream(resourceName)) {
            try (Reader reader = new InputStreamReader(in, DEFAULT_CHARSET)) {
                runScript(reader);
            }
        } catch (IOException e) {
            throw new WorkflowException(RESOURCE_CANNOT_BE_READ.with(resourceName), e);
        } catch (SQLException e) {
            throw new WorkflowException(SQL_SCRIPT_FAILED.with(resourceName), e);
        }
    }

    public void runScript(Reader reader) throws IOException, SQLException {
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            List<String> lines = bufferedReader.lines().collect(Collectors.toList());
            runScript(lines);
        }
    }

    public void runScript(List<String> lines) throws SQLException {
        String[] commands = parseScript(lines);
        execute(commands);
    }

    private void execute(String[] commands) throws SQLException {
        try (Statement s = this.connection.createStatement()) {
            for (String command : commands) {
                s.execute(command);
            }
        }
        this.connection.commit();
    }

    private String[] parseScript(List<String> lines) {
        String whole = lines.stream().map(String::trim).map(line -> line.replaceAll("--.*", ""))
                .filter(line -> !line.isEmpty()).collect(Collectors.joining(" "));
        return whole.replaceAll("/\\*.*?\\*/", "").split(";");
    }
    
    private static Dialect guessDialect(Connection connection) {
        String productName = null;
        try {
            DatabaseMetaData metadata = connection.getMetaData();
            productName = metadata.getDatabaseProductName();
            return Dialect.ofProduct(productName);
        } catch (SQLException e) {
            throw new WorkflowException(REPOSITORY_ACCESS_ERROR_OCCURRED.toString(), e);
        } catch (NoSuchElementException e) {
            throw new WorkflowException(REPOSITORY_PRODUCT_UNSUPPORTED.with(productName));
        }
    }
    
    private String getSqlResourceName(String baseName, Dialect dialect) {
        StringBuilder b = new StringBuilder(baseName);
        b.append("-").append(dialect.getSpecifier()).append(".sql");
        return b.toString();
    }
}
