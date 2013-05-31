package de.intevation.lada.validation;

import java.util.Map;


public interface Validator
{
    public void validate(Object object) throws ValidationException;
    public Map<String, Integer> getWarnings();
}
