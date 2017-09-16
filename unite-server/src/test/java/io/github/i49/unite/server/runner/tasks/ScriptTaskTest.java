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
package io.github.i49.unite.server.runner.tasks;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import io.github.i49.unite.api.tasks.ScriptTask;
import io.github.i49.unite.api.tasks.Task;

/**
 * Unit test of {@link ScriptTask}.
 */
public class ScriptTaskTest {

    @ClassRule
    public static final TaskRunner runner = new TaskRunner();

    @Before
    public void setUp() {
        runner.reset();
    }
    
    @Test
    public void run_shouldRunScript() {
        // given
        Path dir = Paths.get("target/test-classes");
        Path path = dir.resolve("hello" + getScriptExtension());
        Task task = runner.getFactory().createShellTaskBuilder(path)
                .arguments("John Smith")
                .build();
        
        // when
        runner.runTask(task);
    }
    
    private static String getScriptExtension() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            return ".bat";
        } else {
            return ".sh";
        }
    }
}

