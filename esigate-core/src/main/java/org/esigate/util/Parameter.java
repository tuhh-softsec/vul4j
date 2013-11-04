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
import java.util.Properties;

public final class Parameter {
    private final String name;
    private final String defaultValue;

    @Override
    public boolean equals(Object obj) {
        return this.name.equals(obj);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    public Parameter(String name, String defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public int getValueInt(Properties properties) {
        int defaultValueInt = 0;
        if (defaultValue != null) {
            defaultValueInt = Integer.parseInt(defaultValue);
        }
        return PropertiesUtil.getPropertyValue(properties, name, defaultValueInt);
    }

    public boolean getValueBoolean(Properties properties) {
        boolean defaultValueBoolean = false;
        if (defaultValue != null) {
            defaultValueBoolean = Boolean.parseBoolean(defaultValue);
        }
        return PropertiesUtil.getPropertyValue(properties, name, defaultValueBoolean);
    }

    public float getValueFloat(Properties properties) {
        float defaultValueFloat = 0;
        if (defaultValue != null) {
            defaultValueFloat = Float.parseFloat(defaultValue);
        }
        return PropertiesUtil.getPropertyValue(properties, name, defaultValueFloat);
    }

    public long getValueLong(Properties properties) {
        long defaultValueLong = 0;
        if (defaultValue != null) {
            defaultValueLong = Long.parseLong(defaultValue);
        }
        return PropertiesUtil.getPropertyValue(properties, name, defaultValueLong);
    }

    public String getValueString(Properties properties) {
        return PropertiesUtil.getPropertyValue(properties, name, defaultValue);
    }

    public Collection<String> getValueList(Properties properties) {
        return PropertiesUtil.getPropertyValueAsList(properties, name, defaultValue);
    }

    public String getName() {
        return name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

}
