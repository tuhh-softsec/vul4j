/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.validation.rules.messwert;

import de.intevation.lada.model.land.Messwert;
import de.intevation.lada.model.stammdaten.Umwelt;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

/**
 * Validation rule for messwert.
 * Validates if the "messeinheit" is the secondary "messeinheit" of to umweltbereich
 * connected to this messwert
 */
@ValidationRule("Messwert")
public class SecondaryMehSelected implements Rule {

    @Override
    public Violation execute(Object object) {
        Messwert messwert = (Messwert)object;
        Umwelt umwelt = null;
        Violation violation = null;

        if (messwert.getMessung() != null
                && messwert.getMessung().getProbe() != null) {
            umwelt = messwert.getMessung().getProbe().getUmwelt();
        }

        if (umwelt != null) {
            Integer secMehId = umwelt.getSecMehId();
            if (secMehId.equals(messwert.getMehId())) {
                violation = new Violation();
                violation.addWarning("mehId", 636);
            }
        }
        return violation;

    }
}
