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

import java.util.NoSuchElementException;

/**
 * SQL dialect.
 * 
 * @author i49
 */
public enum Dialect {
    HSQLDB,
    MYSQL
    ;
    
    public String getSpecifier() {
        return name().toLowerCase();
    }
    
    public static Dialect ofProduct(String product) {
        if (product.equals("HSQL Database Engine")) {
            return HSQLDB;
        }
        throw new NoSuchElementException(product);
    }
}
