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
package io.github.i49.unite.server.runner;

import io.github.i49.unite.api.base.Platform;

/**
 * The interface for running workflows and jobs in the repository.
 */
public interface WorkflowRunner {
 
    /**
     * Returns the current platform on which this runner is running.
     * 
     * @return the current platform, never be {@code null}.
     */
    Platform getPlatform();

    /**
     * Returns the total number of jobs completed by this runner.
     * 
     * @return the total number of jobs completed.
     */
    long getTotalNumberOfJobsDone();

    /**
     * Checks if this runner is running or not.
     * 
     * @return {@code true} if this runner is running, {@code false} otherwise.
     */
    boolean isRunning();

    /**
     * Runs a single job and exits.
     * 
     * @return the number of jobs done by this method, which is zero or one.
     */
    long runSingle();

    /**
     * Runs all jobs in the repository and exits.
     * 
     * @return the number of jobs done by this method.
     */
    long runAll();

    long runInfinite();

    /**
     * Stops running.
     */
    void stop();
}
