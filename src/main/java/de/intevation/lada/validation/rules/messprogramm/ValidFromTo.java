/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.validation.rules.messprogramm;

import de.intevation.lada.model.land.Messprogramm;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

/**
 * Validation rule for Messprogramm.
 * Validates if the validity period is meaningful.
 */
@ValidationRule("Messprogramm")
public class ValidFromTo implements Rule {

    @Override
    public Violation execute(Object object) {
        Messprogramm messprogramm = (Messprogramm)object;
        Violation violation = new Violation();

        if (messprogramm.getGueltigVon() != null
            && messprogramm.getGueltigBis() != null
            && messprogramm.getGueltigVon() > messprogramm.getGueltigBis()) {
            violation.addError("gueltigVon", 662);
            violation.addError("gueltigBis", 662);
        }

        return violation.hasErrors()
            ? violation
            :null;
    }
}
