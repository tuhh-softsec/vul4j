package de.intevation.lada.validation;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import de.intevation.lada.model.LOrt;

@ApplicationScoped
@Named("lortvalidator")
public class LOrtValidator
implements Validator
{
    /**
     * Validate a LOrt object.
     *
     * @param object    The LOrt object.
     * @param update    The database operation.
     *                  TRUE indicates that the object should be updated, FALSE
     *                  if the object is a new Object.
     * @return Map containing warnings.
     */
    @Override
    public Map<String, Integer> validate(Object object, boolean update)
    throws ValidationException {
        Map<String, Integer> warnings = new HashMap<String, Integer>();
        if (!(object instanceof LOrt)) {
            Map<String, Integer> errors = new HashMap<String, Integer>();
            errors.put("lort", 610);
            throw new ValidationException(errors);
        }

        //TODO: more validation, see LSB: VI - Konsistenzregeln
        return warnings;
    }

}
