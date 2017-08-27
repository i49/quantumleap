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

/**
 * Interface responsible for marshalling of Java objects.
 * 
 * @param <R> the type of the content produced by marshalling.
 */
interface Marshaller<R> {

    /**
     * Marshals the given object.
     * 
     * @param object the object to be marshalled.
     * @return the content produced by marshalling of the object.
     */
    R marshal(Object object);
    
    /**
     * Unmarshals the given content into an object.
     * 
     * @param <T> the type of the object to be produced by unmarshalling.
     * @param content the content to be unmarshalled. 
     * @param type the class of object to be produced by unmarshalling. 
     * @return the newly created object.
     */
    <T> T unmarshal(R content, Class<T> type);
}
