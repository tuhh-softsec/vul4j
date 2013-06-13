package de.intevation.lada.validation;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import de.intevation.lada.model.LMessung;

/**
 * Validator for LMessung objects.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Named("lmessungvalidator")
@ApplicationScoped
public class LMessungValidator
implements Validator
{

    @Override
    public Map<String, Integer> validate(Object object)
    throws ValidationException {
        Map<String, Integer> warnings = new HashMap<String, Integer>();
        if (!(object instanceof LMessung)) {
            Map<String, Integer> errors = new HashMap<String, Integer>();
            errors.put("lmessung", 610);
            throw new ValidationException(errors);
        }
        LMessung messung = (LMessung)object;

        //TODO: mode validation, see LSB: VI - Konsistenzregeln.
        return warnings;
    }

}
