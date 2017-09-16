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

import java.util.List;

import io.github.i49.unite.api.tasks.Task;
import io.github.i49.unite.api.tasks.TaskContext;

public class SummingTask implements Task {

    @Override
    public void run(TaskContext context) {
        @SuppressWarnings("unchecked")
        List<Integer> numbers = (List<Integer>)context.getInputParameters().get("numbers");
        context.getOutputParameters().put("sum", sum(numbers));
    }
    
    private static int sum(List<Integer> numbers) {
        Integer sum = numbers.stream().reduce(0, (x, y)->x + y);
        return sum;
    }
}
