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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.esigate.ConfigurationException;

/**
 * Utility methods for loading configuration parameters.
 * 
 * @author Francois-Xavier Bonnet
 * @author Nicolas Richeton
 * 
 */
public final class PropertiesUtil {

    private PropertiesUtil() {

    }

    /**
     * Retrieves a property containing a comma separated list of values, trim them and return them as a Collection of
     * String.
     * 
     * @param properties
     * @param propertyName
     * @return the values
     */
    public static Collection<String> getPropertyValueAsList(Properties properties, String propertyName) {
        return getPropertyValueAsList(properties, propertyName, null);
    }

    /**
     * Retrieves a property containing a comma separated list of values, trim them and return them as a Collection of
     * String.
     * 
     * @param properties
     * @param propertyName
     * @param defaultValue
     * @return the values
     */
    public static Collection<String> getPropertyValueAsList(Properties properties, String propertyName,
            String defaultValue) {
        String propertyValue = properties.getProperty(propertyName);
        if (propertyValue == null) {
            propertyValue = defaultValue;
        }
        Collection<String> result = toCollection(propertyValue);
        if (result.contains("*") && result.size() > 1) {
            throw new ConfigurationException(propertyName + " must be a comma-separated list or *");
        }
        return result;
    }

    /**
     * Return the provided comma-separated String as a collection. Order is maintained.
     * 
     * @param list
     * @return Ordered collection
     */
    private static Collection<String> toCollection(String list) {
        Collection<String> result = new ArrayList<String>();
        if (list != null) {
            String[] values = list.split(",");
            for (String value : values) {
                result.add(value.trim());
            }
        }
        return result;
    }

    /**
     * Populates a list based on 2 properties defining tokens to include and tokens to exclude.
     * 
     * @param list
     * @param properties
     * @param toAddPropertyName
     * @param toRemovePropertyName
     */
    public static void populate(FilterList list, Properties properties, String toAddPropertyName,
            String toRemovePropertyName) {
        Collection<String> toAdd = getPropertyValueAsList(properties, toAddPropertyName);
        Collection<String> toRemove = getPropertyValueAsList(properties, toRemovePropertyName);
        if (toAdd.contains("*") && toRemove.contains("*")) {
            throw new ConfigurationException("cannot use * for " + toAddPropertyName + " and " + toRemovePropertyName
                    + " at the same time");
        }
        if (toRemove.contains("*")) {
            list.remove(toRemove);
            list.add(toAdd);
        } else {
            list.add(toAdd);
            list.remove(toRemove);
        }
    }

    /**
     * Populates a list based on 2 properties defining tokens to include and tokens to exclude.
     * 
     * @param list
     * @param properties
     * @param toAddPropertyName
     * @param toRemovePropertyName
     * @param defaultToAddString
     * @param defaultToRemoveString
     */
    public static void populate(FilterList list, Properties properties, String toAddPropertyName,
            String toRemovePropertyName, String defaultToAddString, String defaultToRemoveString) {
        Collection<String> defaultToAdd = toCollection(defaultToAddString);
        Collection<String> defaultToRemove = toCollection(defaultToRemoveString);
        list.add(defaultToAdd);
        list.remove(defaultToRemove);
        Collection<String> toAdd = getPropertyValueAsList(properties, toAddPropertyName);
        Collection<String> toRemove = getPropertyValueAsList(properties, toRemovePropertyName);
        if (toAdd.contains("*") && toRemove.contains("*")) {
            throw new ConfigurationException("cannot use * for " + toAddPropertyName + " and " + toRemovePropertyName
                    + " at the same time");
        }
        if (toRemove.contains("*")) {
            list.remove(toRemove);
            list.add(toAdd);
        } else {
            list.add(toAdd);
            list.remove(toRemove);
        }
    }

    public static int getPropertyValue(Properties props, String name, int defaultValue) {
        String value = props.getProperty(name);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    public static boolean getPropertyValue(Properties props, String name, boolean defaultValue) {
        String value = props.getProperty(name);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public static String getPropertyValue(Properties props, String name, String defaultValue) {
        return props.getProperty(name, defaultValue);
    }

    public static float getPropertyValue(Properties properties, String name, float defaultValue) {
        String value = properties.getProperty(name);
        return value != null ? Float.parseFloat(value) : defaultValue;
    }

    public static long getPropertyValue(Properties properties, String name, long defaultValue) {
        String value = properties.getProperty(name);
        return value != null ? Long.parseLong(value) : defaultValue;
    }

}
