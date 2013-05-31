package de.intevation.lada.validation;

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
    }

    /**
     * Getter for the errors.
     *
     * @return
     */
    public Map<String, Integer> getErrors() {
        return errors;
    }
}
