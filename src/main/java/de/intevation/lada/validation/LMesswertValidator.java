package de.intevation.lada.validation;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import de.intevation.lada.model.LMesswert;

@Named("lmesswertvalidator")
@ApplicationScoped
public class LMesswertValidator
implements Validator
{
    /**
     * Validate a LMesswert object.
     *
     * @param object    The LMesswert object.
     * @param update    The database operation.
     *                  TRUE indicates that the object should be updated, FALSE
     *                  if the object is a new Object.
     * @return Map containing warnings.
     */
    @Override
    public Map<String, Integer> validate(Object object, boolean update)
    throws ValidationException {
        Map<String, Integer> warnings = new HashMap<String, Integer>();
        if (!(object instanceof LMesswert)) {
            Map<String, Integer> errors = new HashMap<String, Integer>();
            errors.put("lmesswert", 610);
            throw new ValidationException(errors);
        }
        LMesswert messwert = (LMesswert)object;

        validateMessunsicherheit(messwert, warnings);
        return null;
    }

    /**
     * Check if the LMesswert has a 'Messunsicherheit' or if the the value
     * is lower than the 'Nachweisgrenze'.
     *
     * @param messwert  The LMesswert object.
     * @param warnings  The map containing warnings.
     * @throws ValidationException
     */
    private void validateMessunsicherheit(
        LMesswert messwert,
        Map<String, Integer> warnings)
    throws ValidationException {
        Float unsicherheit = messwert.getMessfehler();
        Float nachweisgrenze = messwert.getNwgZuMesswert();
        Float wert = messwert.getMesswert();
        if (unsicherheit != null && unsicherheit > 0f) {
            return;
        }
        else if (nachweisgrenze != null && wert < nachweisgrenze) {
            return;
        }
        else {
            warnings.put("messwert", 631);
        }
    }
}
