package org.esigate.util;

/**
 * String parameter.
 * 
 * @author Alexis Thaveau
 */
public class ParameterString extends Parameter<String> {
    /***
     * Constructor.
     * 
     * @param name
     *            name
     * @param defaultValue
     *            defaultValue
     */
    public ParameterString(String name, String defaultValue) {
        super(name, defaultValue);
    }

    /***
     * Constructor.
     * 
     * @param name
     *            name
     */
    public ParameterString(String name) {
        super(name);
    }
}
