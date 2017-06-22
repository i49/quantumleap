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
package com.github.i49.quantumleap.core.common;

import static com.github.i49.quantumleap.core.common.Message.*;

/**
 * Preconditions for methods.
 */
public final class Preconditions {

    /**
     * Checks if given parameter value is {@code null}.
     * 
     * @param object the value of the parameter.
     * @param name the name of the parameter.
     * @throws NullPointerException if given object is {@code null}.
     */
    public static void checkNotNull(Object object, String name) {
        if (object == null) {
            throw new NullPointerException(PARAMETER_IS_NULL.with(name));
        }
    }
    
    /**
     * Checks if any element value of given array parameter is {@code null}. 
     * 
     * @param objects the array passed in as a parameter.
     * @param name the name of the array-type parameter.
     */
    public static void checkNotNull(Object[] objects, String name) {
        if (objects == null) {
            throw new NullPointerException(PARAMETER_IS_NULL.with(name));
        }
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] == null) {
                throw new NullPointerException(PARAMETER_ITEM_IS_NULL.with(name, i));
            }
        }
    }

    /**
     * Checks if given parameter is of the type specified.
     * 
     * @param T the expected type.
     * @param object the value of the parameter.
     * @param expectedType the class of the expected type.
     * @param name the name of the parameter.
     * @return the value casted to the {@code expectedType}.
     * @throws IllegalArgumentException if given object is not of the expected type.
     */
    public static <T> T checkRealType(Object object, Class<T> expectedType, String name) {
        if (!expectedType.isInstance(object)) {
            throw new IllegalArgumentException(PARAMETER_NOT_INSTANCE_FOR_ENGINE.with(name));
        }
        return expectedType.cast(object);
    }
    
    private Preconditions() {
    }
}
