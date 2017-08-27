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
package io.github.i49.unite.core.repository;

import static io.github.i49.unite.core.common.Message.INTERNAL_ERROR;
import static io.github.i49.unite.core.common.Message.REPOSITORY_ACCESS_ERROR_OCCURRED;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.github.i49.unite.api.base.WorkflowException;

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
            throwAccessError(e);
        }
    }
    
    public long queryForLong() {
        try (ResultSet rs = statement.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throwAccessError(e);
            return 0;
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
            throwAccessError(e);
            return null;
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
            throwAccessError(e);
            return Optional.empty();
        }
    }
   
    public int update() {
        try {
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new WorkflowException(REPOSITORY_ACCESS_ERROR_OCCURRED.toString(), e);
        }
    }
    
    public long updateAndGenerateLong() {
        update();
        try (ResultSet rs = statement.getGeneratedKeys()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throwAccessError(e);
            return 0;
        }
    }
    
    private static void throwInternalError(SQLException e) {
        throw new WorkflowException(INTERNAL_ERROR.toString(), e);
    }
    
    private static void throwAccessError(SQLException e) {
        throw new WorkflowException(REPOSITORY_ACCESS_ERROR_OCCURRED.toString(), e);
    }
}
