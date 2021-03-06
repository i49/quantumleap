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
package io.github.i49.unite.core.workflow;

import java.util.OptionalLong;

/**
 * Workflow components such as jobs and workflows.
 */
class WorkflowComponent {

    private OptionalLong id;

    protected WorkflowComponent() {
        this.id = OptionalLong.empty();
    }
    
    protected WorkflowComponent(OptionalLong id) {
        this.id = id;
    }

    public boolean hasId() {
        return id.isPresent();
    }

    public long getId() {
        return id.getAsLong();
    }

    public void setId(long id) {
        this.id = OptionalLong.of(id);
    }
}
