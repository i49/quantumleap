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
package com.github.i49.quantumleap.core.repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.github.i49.quantumleap.api.workflow.WorkflowException;

/**
 * A binary marshaller implemented with built-in serialization provided by Java language.
 */
public class BinaryMarshaller implements Marshaller<byte[]> {

    @Override
    public byte[] marshal(Object object) {
        if (object == null) {
            return null;
        }
        byte[] bytes = null;
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                ObjectOutputStream objectStream = new ObjectOutputStream(byteStream)) {
            objectStream.writeObject(object);
            bytes = byteStream.toByteArray();
        } catch (IOException e) {
            // never reach here
        }
        return bytes;
    }

    @Override
    public <T> T unmarshal(byte[] marshalled, Class<T> type) {
        if (marshalled == null) {
            return null;
        }
        Object object = null;
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(marshalled);
             ObjectInputStream objectStream = new ObjectInputStream(byteStream)) {
             object = objectStream.readObject();    
        } catch (ClassNotFoundException e) {
            // TODO:
            throw new WorkflowException("", e);
        } catch (IOException e) {
            // never reach here
        }
        return type.cast(object);
    }
}
