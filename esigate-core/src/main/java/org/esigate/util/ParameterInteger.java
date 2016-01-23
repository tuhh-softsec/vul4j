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
 * Integer parameter.
 * 
 * @author Alexis Thaveau
 */
public class ParameterInteger extends Parameter<Integer> {

    public ParameterInteger(String name, Integer defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public Integer getValue(Properties properties) {
        Integer defaultValue = 0;
        if (getDefaultValue() != null) {
            defaultValue = getDefaultValue();
        }
        return PropertiesUtil.getPropertyValue(properties, getName(), defaultValue);

    }
}
