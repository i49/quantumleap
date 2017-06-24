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

import com.github.i49.quantumleap.api.workflow.Platform;

/**
 * A helper class to detect current platform.
 */
public class Platforms {

    private static final Platform current;
    
    static {
        current = detect();
    }
    
    public static Platform getCurrent() {
        return current;
    }

    private static Platform detect() {
        String name = System.getProperty("os.name");
        name = name.toLowerCase();
        if (name.contains("windows")) {
            return Platform.WINDOWS;
        } else if (name.contains("linux") || name.contains("unix")) {
            return Platform.UNIX;
        }
        // TODO: test other platforms.
        return Platform.OTHER;
    }
}
