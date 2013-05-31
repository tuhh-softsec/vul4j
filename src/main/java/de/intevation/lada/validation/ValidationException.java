package de.intevation.lada.validation;

import java.util.Map;


public class ValidationException
extends Exception
{
    private Map<String, Integer> errors;

    @SuppressWarnings("unused")
    private ValidationException() {
    }

    public ValidationException(Map<String, Integer> errors) {
        this.errors = errors;
    }

    public Map<String, Integer> getErrors() {
        return errors;
    }
}
