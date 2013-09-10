package de.intevation.lada.validation;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception used for validation errors.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class ValidationException
extends Exception
{
    /**
     * Errors saved in this exception.
     */
    private Map<String, Integer> errors;

    /**
     * Warnings saved in this exception.
     */
    private Map<String, Integer> warnings;

    /**
     * Do not allow an empty ValidationException object. 
     */
    private ValidationException() {
    }

    /**
     * Create a new instance with errors.
     *
     * @param errors
     */
    public ValidationException(Map<String, Integer> errors) {
        this();
        this.errors = errors;
        this.warnings = new HashMap<String, Integer>();
    }

    /**
     * Create a new instance with errors and warnings.
     *
     * @param errors
     */
    public ValidationException(
        Map<String, Integer> errors,
        Map<String, Integer> warnings
    ) {
        this();
        this.errors = errors;
        this.warnings = warnings;
    }

    /**
     * Getter for the errors.
     *
     * @return the errors
     */
    public Map<String, Integer> getErrors() {
        return errors;
    }

    /**
     * Getter for the warnings.
     *
     * @return the warnings
     */
    public Map<String, Integer> getWarnings() {
        return warnings;
    }
}
