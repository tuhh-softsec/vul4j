package de.intevation.lada.importer.laf;

import java.util.regex.Pattern;

/**
 * An EntryFormat describes the internal structure of LAF-based key-value pairs.
 * The pattern is a regular expression used to match the value in the LAF
 * importer.
 * The entry formats are defined in a config file
 * (see wiki-doc: https://bfs-intern.intevation.de/Server/Importer).
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class EntryFormat
{
    private String key;
    private Pattern pattern;
    private Object defaultValue;

    /**
     * Default constructor to create a new EntryFormat object.
     */
    public EntryFormat() {
    }

    /**
     * @return the key.
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key The key to set.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the pattern
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * @param pattern The pattern to set.
     */
    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * @return the default value.
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * @param defaultValue the default value to set.
     */
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
}
