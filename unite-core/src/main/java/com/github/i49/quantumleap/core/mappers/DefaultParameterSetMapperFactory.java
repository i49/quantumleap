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
package com.github.i49.quantumleap.core.mappers;

import static com.github.i49.quantumleap.core.common.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import com.github.i49.quantumleap.api.workflow.ParameterSetMapper;
import com.github.i49.quantumleap.api.workflow.ParameterSetMapperFactory;

/**
 * Default implementation of {@link ParameterSetMapperFactory}.
 */
public class DefaultParameterSetMapperFactory implements ParameterSetMapperFactory {
    
    @Override
    public ParameterSetMapper createKeyMapper(Map<String, String> keyMap) {
        checkNotNull(keyMap, "keyMap");
        return new KeyMapParameterSetMapper(keyMap);
    }

    @Override
    public ParameterSetMapper createKeyMapper(String... keys) {
        checkNotNull(keys, "keys");
        Map<String, String> keyMap = new HashMap<>();
        for (int i = 0; i < keys.length; i += 2) {
            keyMap.put(keys[i], keys[i + 1]);
        }
        return createKeyMapper(keyMap);
    }
}
