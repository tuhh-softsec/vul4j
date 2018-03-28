package org.esigate.test;

import java.util.Properties;

/**
 * This is a builder for esigate properties.
 * 
 * <p>
 * Used mainly for testing purposes.
 * 
 * @author Nicolas Richeton
 * 
 */
public class PropertiesBuilder {

    private Properties prop = new Properties();
    private Properties target = null;

    /**
     * Constructor
     */
    public PropertiesBuilder() {
    }

    /**
     * Set a property.
     * 
     * <p>
     * Perform automatic conversion for
     * <ul>
     * <li>Class object : uses the full name (package + classname)</li>
     * <li>Multiple objects : appends all values, comma separated.</li>
     * <li>Other objects : uses the toString() value.</li>
     * </ul>
     * 
     * @param key
     *            property name
     * @param value
     *            one or multiple objects
     * @return the builder (fluent style)
     */
    @SuppressWarnings("rawtypes")
    public PropertiesBuilder set(Object key, Object... value) {

        String objectValue = "";
        boolean first = true;

        for (int i = 0; i < value.length; i++) {

            // Handle list
            if (first)
                first = false;
            else
                objectValue = objectValue + ",";

            // Add object
            Object o = value[i];

            if (o instanceof Class) {
                objectValue = objectValue + ((Class) o).getName();
            }
            // Other conversions could be added here...
            else {
                // Default value
                objectValue = o.toString();
            }
        }

        return this.set(key.toString(), objectValue);
    }

    /**
     * Set a property.
     * 
     * @param key
     *            property name
     * 
     * @param value
     *            property value
     * @return the builder (fluent style)
     */
    public PropertiesBuilder set(String key, String value) {
        prop.setProperty(key, value);
        return this;
    }

    /**
     * The builder can update an existing Properties object.
     * 
     * @param p
     *            target properties object
     * @return the builder (fluent style)
     */
    public PropertiesBuilder on(Properties p) {
        target = p;
        return this;
    }

    /**
     * Build the Properties object, based on previous parameters.
     * 
     * @return the properties
     */
    public Properties build() {

        if (target != null) {
            // Merge properties
            target.putAll(prop);
        } else {
            target = prop;
        }

        return target;
    }
}
