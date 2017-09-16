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
package io.github.i49.unite.server.runner;

import java.util.Map;

import io.github.i49.unite.api.tasks.Task;
import io.github.i49.unite.api.tasks.TaskContext;

public class ScalingTask implements Task {

    @Override
    public void run(TaskContext context) {
        Map<String, Object> input = context.getInputParameters();
        int multiplicand = (int)input.get("multiplicand");
        int multiplier = (int)input.get("multiplier");
        int answer = multiplicand * multiplier;
        context.getOutputParameters().put("answer", answer);
    }
}
