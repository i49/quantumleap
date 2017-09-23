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

import static io.github.i49.unite.core.common.Message.RESOURCE_CANNOT_BE_READ;
import static io.github.i49.unite.core.common.Message.SQL_SCRIPT_FAILED;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

import io.github.i49.unite.api.base.WorkflowException;

/**
 * A helper class for running given SQL script.
 */
public class SqlScriptRunner {

    private final Connection conncetion;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public SqlScriptRunner(Connection conncetion) {
        this.conncetion = conncetion;
    }

    /**
     * Runs a script read from the specified resource file.
     * 
     * @param resourceName the name of the resource file, which must be on the current classpath.
     */
    public void runScript(String resourceName) {
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
        try (Statement s = this.conncetion.createStatement()) {
            for (String command : commands) {
                s.execute(command);
            }
        }
        this.conncetion.commit();
    }

    private String[] parseScript(List<String> lines) {
        String whole = lines.stream().map(String::trim).map(line -> line.replaceAll("--.*", ""))
                .filter(line -> !line.isEmpty()).collect(Collectors.joining(" "));
        return whole.replaceAll("/\\*.*?\\*/", "").split(";");
    }
}
