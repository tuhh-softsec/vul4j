package de.intevation.lada.validation;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

@Named("lprobevalidator")
public class LProbeValidator
implements Validator
{

    private Map<String, Integer> warnings;

    @Override
    public void validate(Object probe)
    throws ValidationException {
        warnings = new HashMap<String, Integer>();
        warnings.put("entnahmeort", 612);
        validateId(probe);
    }

    private void validateId(Object probe)
    throws ValidationException{
        Map<String, Integer> errors = new HashMap<String, Integer>();
        // TODO Implement me!
        errors.put("probeid", 611);
        ValidationException ve = new ValidationException(errors);
        throw ve;
    }

    @Override
    public Map<String, Integer> getWarnings() {
        return warnings;
    }
}
