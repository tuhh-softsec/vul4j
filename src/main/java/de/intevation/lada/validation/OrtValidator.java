package de.intevation.lada.validation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import de.intevation.lada.model.land.LOrt;
import de.intevation.lada.validation.annotation.ValidationConfig;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

@ValidationConfig(type="Ort")
@ApplicationScoped
public class OrtValidator implements Validator {

    @Inject
    @ValidationRule("Ort")
    private Instance<Rule> rules;

    @Override
    public Violation validate(Object object) {
        Violation violations = new Violation();
        if (!(object instanceof LOrt)) {
            violations.addError("ort", 602);
            return violations;
        }
        for(Rule rule: rules) {
            Violation result = rule.execute(object);
            if (result != null) {
                if (result.hasWarnings()) {
                    violations.addWarnings(result.getWarnings());
                }
                if (result.hasErrors()) {
                    violations.addErrors(result.getErrors());
                }
            }
        }
        return violations;
    }

}
