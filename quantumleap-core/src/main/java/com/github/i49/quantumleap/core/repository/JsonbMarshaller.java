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

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.json.Json;
import javax.json.JsonString;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.adapter.JsonbAdapter;

/**
 * {@link JsonMarshaller} implemented with Java API for JSON Binding (JSR-367).
 */
public class JsonbMarshaller implements JsonMarshaller {
    
    private final Jsonb jsonb;
    
    public JsonbMarshaller() {
        JsonbConfig config = new JsonbConfig()
                .withAdapters(new PathAdapter());
        this.jsonb = JsonbBuilder.create(config);
    }

    @Override
    public String marshal(Object object) {
        return jsonb.toJson(object);
    }

    @Override
    public <T> T unmarshal(String str, Class<T> type) {
        return jsonb.fromJson(str, type);
    }

    /**
     * Adapter class for adapting {@link Path} objects.
     */
    private static class PathAdapter implements JsonbAdapter<Path, JsonString> {

        @Override
        public JsonString adaptToJson(Path path) throws Exception {
            return Json.createValue(path.toString());
        }

        @Override
        public Path adaptFromJson(JsonString adapted) throws Exception {
            return Paths.get(adapted.getString());
        }
    }
}
