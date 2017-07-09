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
package com.github.i49.quantumleap.core.workflow;

import com.github.i49.quantumleap.api.workflow.ParameterSetMapper;

/**
 * A link between jobs.
 */
public class JobLink {

    private final ManagedJob source;
    private final ManagedJob target;
    private final ParameterSetMapper mapper;
    
    public static JobLink of(ManagedJob source, ManagedJob target, ParameterSetMapper mapper) {
        return new JobLink(source, target, mapper);
    }
    
    private JobLink(ManagedJob source, ManagedJob target, ParameterSetMapper mapper) {
        this.source = source;
        this.target = target;
        this.mapper = mapper;
    }
    
    public ManagedJob getSource() {
        return source;
    }
    
    public ManagedJob getTarget() {
        return target;
    }
    
    public ParameterSetMapper getMapper() {
        return mapper;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + System.identityHashCode(source);
        result = prime * result + System.identityHashCode(target);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        JobLink other = (JobLink)obj;
        return (source == other.source && target == other.target);
    }
}
