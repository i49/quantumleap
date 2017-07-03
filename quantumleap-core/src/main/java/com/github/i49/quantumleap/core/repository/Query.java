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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.i49.quantumleap.api.workflow.WorkflowException;
import com.github.i49.quantumleap.core.common.Message;

/**
 * SQL statement.
 */
public class Query {
    
    private final PreparedStatement statement;
    
    public Query(PreparedStatement statement) {
        this.statement = statement;
    }
    
    public Query setInt(int index, int value) {
        try {
            statement.setInt(index, value);
        } catch (SQLException e) {
            throwInternalError(e);
        }
        return this;
    }

    public Query setLong(int index, long value) {
        try {
            statement.setLong(index, value);
        } catch (SQLException e) {
            throwInternalError(e);
        }
        return this;
    }

    public Query setString(int index, String value) {
        try {
            statement.setString(index, value);
        } catch (SQLException e) {
            throwInternalError(e);
        }
        return this;
    }
    
    public Query setBytes(int index, byte[] bytes) {
        try {
            statement.setBytes(index, bytes);
        } catch (SQLException e) {
            throwInternalError(e);
        }
        return this;
    }
    
    public Query setEnum(int index, Enum<?> value) {
        return setString(index, value.name());
    }

    public void execute() {
        try {
            statement.execute();
        } catch (SQLException e) {
            // TODO:
            throw new WorkflowException("", e);
        }
    }
    
    public long queryForLong() {
        try (ResultSet rs = statement.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            // TODO:
            throw new WorkflowException("", e);
        }
    }
    
    public <T> List<T> queryForList(RowMapper<T> mapper) {
        try (ResultSet resultSet = statement.executeQuery()) {
            List<T> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(mapper.mapRow(resultSet));
            }
            return list;
        } catch (SQLException e) {
            // TODO:
            throw new WorkflowException("", e);
        }
    }
    
    public <T> Optional<T> queryForObject(RowMapper<T> mapper) {
        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return Optional.of(mapper.mapRow(resultSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            // TODO:
            throw new WorkflowException("", e);
        }
    }
   
    public int update() {
        try {
            return statement.executeUpdate();
        } catch (SQLException e) {
            // TODO:
            throw new WorkflowException("", e);
        }
    }
    
    public long updateAndGenerateLong() {
        update();
        try (ResultSet rs = statement.getGeneratedKeys()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            // TODO:
            throw new WorkflowException("", e);
        }
    }
    
    private static void throwInternalError(SQLException e) {
        throw new WorkflowException(Message.INTERNAL_ERROR.toString(), e);
    }
}
