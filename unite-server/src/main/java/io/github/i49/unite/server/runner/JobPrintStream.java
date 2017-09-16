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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 */
public class JobPrintStream extends PrintStream {
    
    public JobPrintStream() {
        super(new CompositeOutputStream());
    }
    
    public String[] getLines() {
        String captured = getCaptureStream().toString();
        return captured.split("\r?\n");
    }
    
    private ByteArrayOutputStream getCaptureStream() {
        CompositeOutputStream composite = (CompositeOutputStream)this.out;
        return composite.captureStream;
    }
    
    private static class CompositeOutputStream extends OutputStream {
        
        private ByteArrayOutputStream captureStream;
        
        private CompositeOutputStream() {
            this.captureStream = new ByteArrayOutputStream();
        }
        
        
        @Override
        public void close() throws IOException {
            this.captureStream.close();
        }

        @Override
        public void flush() throws IOException {
            this.captureStream.flush();
            System.out.flush();
        }

        @Override
        public void write(int b) throws IOException {
            this.captureStream.write(b);
            System.out.write(b);
        }
    }
}
