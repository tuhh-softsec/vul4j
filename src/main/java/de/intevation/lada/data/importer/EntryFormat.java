package de.intevation.lada.data.importer;

import java.util.regex.Pattern;


public class EntryFormat
{
    private String key;
    private Pattern pattern;
    private Object defaultValue;

    public EntryFormat() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
}
