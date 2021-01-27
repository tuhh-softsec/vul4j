/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada;

import java.util.HashMap;
import java.util.Map;

/**
 * Protocol is a storage class that takes info about test runs.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class Protocol {

    private String name;

    private String type;

    private boolean passed;

    private Map<String, Object> info;

    public Protocol() {
        info = new HashMap<String, Object>();
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name of the test
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The test type.
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return Test passed
     */
    public boolean isPassed() {
        return passed;
    }

    /**
     * @param passed Wether the passed or not.
     */
    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    /**
     * @return Test infos.
     */
    public Map<String, Object> getInfo() {
        return info;
    }

    /**
     * Add new test info.
     * @param key Test key.
     * @param value Test value
     */
    public void addInfo(String key, Object value) {
        this.info.put(key, value);
    }

    /**
     * Returns a formated string of the protocol.
     * @param verbose For more detailed output
     * @return formated String
     */
    public String toString(boolean verbose) {
        String ret = "Test: ";

        ret += this.name + " (" + this.type + "): ";
        if (this.passed) {
            ret += "success";
        } else {
            ret += "FAILED";
        }
        if (verbose) {
            ret += "\nInfo: ";
            for (String key: info.keySet()) {
                ret += "\n    " + key + ": " + info.get(key);
            }
        }
        return ret;
    }
}
