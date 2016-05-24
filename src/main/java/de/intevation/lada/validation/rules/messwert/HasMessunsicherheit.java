/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.validation.rules.messwert;

import de.intevation.lada.model.land.LMesswert;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

/**
 * Validation rule for messwert.
 * Validates if the "messfehler" was set correctly.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@ValidationRule("Messwert")
public class HasMessunsicherheit implements Rule {

    @Override
    public Violation execute(Object object) {
        LMesswert messwert = (LMesswert)object;
        Float unsicherheit = messwert.getMessfehler();
        Double nachweisgrenze = messwert.getNwgZuMesswert();
        Double wert = messwert.getMesswert();
        if (unsicherheit != null && unsicherheit > 0f) {
            return null;
        }
        else if (nachweisgrenze != null && wert < nachweisgrenze) {
            return null;
        }
        Violation violation = new Violation();
        violation.addWarning("messwert", 631);
        return violation;
    }
}
