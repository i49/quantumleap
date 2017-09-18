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
package io.github.i49.unite.core.workflow.factory;

import static io.github.i49.unite.core.common.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import io.github.i49.unite.api.workflow.ParameterSetMapper;
import io.github.i49.unite.api.workflow.WorkflowFactory;
import io.github.i49.unite.core.workflow.ManagedJobBuilder;
import io.github.i49.unite.core.workflow.ManagedWorkflowBuilder;
import io.github.i49.unite.core.workflow.mappers.KeyMapParameterSetMapper;

/**
 * The factory for producing workflow components.
 */
public class DefaultWorkflowFactory implements WorkflowFactory {
    
    private static final DefaultWorkflowFactory singleton = new DefaultWorkflowFactory();
    
    /**
     * Returns the singleton of this class.
     * 
     * @return the singleton of this class.
     */
    public static DefaultWorkflowFactory getInstance() {
        return singleton;
    }
    
    public DefaultWorkflowFactory() {
    }

    @Override
    public ManagedWorkflowBuilder createWorkflowBuilder(String name) {
        return new ManagedWorkflowBuilder(name);
    }

    @Override
    public ManagedJobBuilder createJobBuilder(String name) {
        return new ManagedJobBuilder(name);
    }

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
