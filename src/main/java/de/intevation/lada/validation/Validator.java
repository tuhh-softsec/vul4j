package de.intevation.lada.validation;

import java.util.Map;

/**
 * Validator interface. Implement this interface for lada object validations.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public interface Validator
{
    /**
     * Validate a database object.
     * 
     * @param object    The object to validate.
     * @param update    The database operation.
     *                  TRUE indicates that the object should be updated, FALSE
     *                  if the object is a new Object.
     * @return Map containing warnings.
     * @throws ValidationException
     */
    public Map<String, Integer> validate(Object object, boolean update)
    throws ValidationException;
}
