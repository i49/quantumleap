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

package io.github.i49.unite.core.common;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

/**
 * Utility class for loading configuration file.
 * 
 * @author i49
 */
public final class Configurations {
    
    private static final String DEFAULT_NAME = "unite.yaml";

    /**
     * Loads configuration of the specified type.
     * 
     * @param clazz the class of the configuration.
     * @return loaded configuration.
     */
    public static <T> T load(Class<T> clazz) {
        return findAndLoadConfiguration(clazz);
    }

    private static <T> T findAndLoadConfiguration(Class<T> clazz) {
        Path path = Paths.get(DEFAULT_NAME);
        try {
            if (Files.exists(path)) {
                return loadConfiguration(Files.newInputStream(path), clazz);
            } else {
                ClassLoader loader = Configurations.class.getClassLoader();
                InputStream stream = loader.getResourceAsStream(DEFAULT_NAME);
                if (stream != null) {
                    return loadConfiguration(stream, clazz);
                }
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }
    
    private static <T> T loadConfiguration(InputStream stream, Class<T> clazz) throws IOException {
        Yaml yaml = createYamLoader();
        try (InputStream input = stream) {
          return yaml.loadAs(input, clazz);  
        }
    }
    
    private static Yaml createYamLoader() {
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        return new Yaml(representer);
    }
    
    private Configurations() {
    }
}
