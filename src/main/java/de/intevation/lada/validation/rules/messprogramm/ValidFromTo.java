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

    private static final int DOY_MIN = 1;

    // Leap years should be handled in generation of Probe objects
    private static final int DOY_MAX = 365;

    @Override
    public Violation execute(Object object) {
        Messprogramm messprogramm = (Messprogramm) object;
        Violation violation = new Violation();

        if (messprogramm.getGueltigVon() != null
            && (messprogramm.getGueltigVon() < DOY_MIN
                || messprogramm.getGueltigVon() > DOY_MAX)) {
                violation.addError("gueltigVon", 612);
            }

        if (messprogramm.getGueltigBis() != null
            && (messprogramm.getGueltigBis() < DOY_MIN
                || messprogramm.getGueltigBis() > DOY_MAX)) {
                violation.addError("gueltigBis", 612);
            }

        return violation.hasErrors()
            ? violation
            : null;
    }
}
