package de.intevation.lada.validation;

import java.util.Map;

/**
 * Validator interface. Implement this interface for lada object validations.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public interface Validator
{
    public void validate(Object object) throws ValidationException;
    public Map<String, Integer> getWarnings();
}
