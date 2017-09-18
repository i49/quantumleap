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
package io.github.i49.unite.core.workflow.mappers;

import java.util.Map;

import io.github.i49.unite.api.base.ParameterSet;
import io.github.i49.unite.api.workflow.ParameterSetMapper;

/**
 */
public class KeyMapParameterSetMapper implements ParameterSetMapper {
    
    private final Map<String, String> keyMap;
    
    public KeyMapParameterSetMapper(Map<String, String> keyMap) {
        this.keyMap = keyMap;
    }

    @Override
    public void mapParameterSet(ParameterSet source, ParameterSet target) {
        for (String sourceKey: keyMap.keySet()) {
            String targetKey = keyMap.get(sourceKey);
            Object value = source.get(sourceKey);
            target.put(targetKey, value);
        }
    }
}
