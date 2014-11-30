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

import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

/**
 * Array parameter.
 * 
 * @author Alexis Thaveau
 */
public class ParameterArray extends Parameter<String[]> {

    public ParameterArray(String name, String[] defaultValue) {
        super(name, defaultValue);

    }

    public ParameterArray(String name) {
        super(name);
    }

    @Override
    public String[] getValue(Properties properties) {
        String[] value;
        Collection<String> list =
                PropertiesUtil.getPropertyValue(properties, getName(), Collections.<String>emptyList());
        if (list == null || list.isEmpty()) {
            value = getDefaultValue();
        } else {
            value = list.toArray(new String[list.size()]);
        }
        return value;
    }
}
