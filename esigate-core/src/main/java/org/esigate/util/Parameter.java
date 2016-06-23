/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.esigate.util;

import java.util.Properties;

/**
 * A parameter with a T value.
 * 
 * @param <T>
 *            type
 */
public abstract class Parameter<T> {
    private final String name;
    private final T defaultValue;

    @Override
    public boolean equals(Object obj) {
        boolean equals = false;
        if (obj != null) {
            if (obj instanceof Parameter) {
                equals = this.name.equals(((Parameter<?>) obj).getName());
            } else if (obj instanceof String) {
                equals = this.name.equals(obj);
            }
        }
        return equals;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    public Parameter(String name) {
        this(name, null);
    }

    Parameter(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;

    }

    public String getName() {
        return name;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public T getValue(Properties properties) {
        T value = (T) properties.getProperty(this.name);

        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

}
