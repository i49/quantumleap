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
package com.github.i49.quantumleap.api.workflow;

/**
 * The exception thrown by the worlflow engine.
 */
public class WorkflowException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs this exception with a detail message.
     * 
     * @param message the detail message of this exception. cannot be
     *            {@code null}.
     */
    public WorkflowException(String message) {
        super(message);
    }

    /**
     * Constructs this exception with a detail message and a real cause.
     * 
     * @param message the detail message of this exception. cannot be
     *            {@code null}.
     * @param cause the real cause of this exception. cannot be {@code null}.
     */
    public WorkflowException(String message, Throwable cause) {
        super(message, cause);
    }
}
