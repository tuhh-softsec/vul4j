package de.intevation.lada.validation;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import de.intevation.lada.model.LProbe;

/**
 * Validator for LProbe objects.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Named("lprobevalidator")
public class LProbeValidator
implements Validator
{

    /**
     * Warnings found while validating the LProbe
     */
    private Map<String, Integer> warnings;

    /**
     * Validate a LProbe object.
     *
     * @param probe The LProbe object.
     */
    @Override
    public void validate(Object probe)
    throws ValidationException {
        warnings = new HashMap<String, Integer>();
        if (!(probe instanceof LProbe)) {
            Map<String, Integer> errors = new HashMap<String, Integer>();
            errors.put("lprobe", 610);
            throw new ValidationException(errors);
        }
        // Dummy warning.
        warnings.put("entnahmeort", 612);
        validateId(probe);
    }

    /**
     * Validate the LProbe id.
     *
     * @param probe The LProbe object.
     * @throws ValidationException
     */
    private void validateId(Object probe)
    throws ValidationException{
        Map<String, Integer> errors = new HashMap<String, Integer>();
        // TODO Implement me!

        // Dummy error.
        errors.put("probeId", 611);
        ValidationException ve = new ValidationException(errors);
        throw ve;
    }

    /**
     * Getter for warnings occurred while validating the LProbe.
     *
     * @return Map of field - warning code pairs.
     */
    @Override
    public Map<String, Integer> getWarnings() {
        return warnings;
    }
}
