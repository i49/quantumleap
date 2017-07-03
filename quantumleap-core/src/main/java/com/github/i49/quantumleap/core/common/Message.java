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

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Localized messages defined for this API implementation.
 */
public enum Message {
    PARAMETER_IS_NULL,
    PARAMETER_ITEM_IS_NULL,
    PARAMETER_IS_OF_UNEXPECTED_TYPE,
    PARAMETER_ITEM_IS_OF_UNEXPECTED_TYPE,

    RESOURCE_IS_MISSING,

    STATEMENT_IS_UNDEFINED,
    REPOSITORY_FAILED_TO_CLEAR,
    
    INTERNAL_ERROR
    ;

    private static final String BASE_NAME = "com.github.i49.quantumleap.core.messages";
    private static final ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME);

    @Override
    public String toString() {
        return getPattern();
    }

    public String with(Object... arguments) {
        return MessageFormat.format(getPattern(), arguments);
    }

    private String getPattern() {
        return bundle.getString(name());
    }
}
