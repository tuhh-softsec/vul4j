/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.validation;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <a href="mailto:raimund.renkert@intevation.de">Raimund Renkert</a>
 */
public class Violation {

    private Map<String, Integer> warnings;

    private Map<String, Integer> errors;

    public Violation() {
        this.warnings = new HashMap<String, Integer>();
        this.errors = new HashMap<String, Integer>();
    }

    public Map<String, Integer> getWarnings() {
        return this.warnings;
    }

    public Map<String, Integer> getErrors() {
        return this.errors;
    }

    public void addWarning(String key, Integer value) {
        this.warnings.put(key, value);
    }

    public void addError(String key, Integer value) {
        this.errors.put(key, value);
    }

    public void addWarnings(Map<String, Integer> warnings) {
        this.warnings.putAll(warnings);
    }

    public void addErrors(Map<String, Integer> errors) {
        this.errors.putAll(errors);
    }

    public boolean hasWarnings() {
        return this.warnings.size() > 0;
    }

    public boolean hasErrors() {
        return this.errors.size() > 0;
    }
}
