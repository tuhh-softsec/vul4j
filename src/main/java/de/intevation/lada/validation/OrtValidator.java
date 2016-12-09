/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.validation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import de.intevation.lada.model.stammdaten.Ort;
import de.intevation.lada.validation.annotation.ValidationConfig;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

/**
 * Validation for ort objects.
 *
 * Instantiates the set of rules for ort objects and uses these rules to
 * validate the object.
 *
 * @author <a href="mailto:raimund.renkert@intevation.de">Raimund Renkert</a>
 */
@ValidationConfig(type="Ort")
@ApplicationScoped
public class OrtValidator implements Validator {

    @Inject
    @ValidationRule("Ort")
    private Instance<Rule> rules;

    @Override
    public Violation validate(Object object) {
        Violation violations = new Violation();
        if (!(object instanceof Ort)) {
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
