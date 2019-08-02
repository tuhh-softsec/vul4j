/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.validation.rules.messwert;

import de.intevation.lada.model.land.Messwert;
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
        Messwert messwert = (Messwert)object;
        Float unsicherheit = messwert.getMessfehler();
        Double nachweisgrenze = messwert.getNwgZuMesswert();
        Double wert = messwert.getMesswert();
        if (messwert.getMesswertNwg() == null && ( unsicherheit == null || unsicherheit == 0f)) {
            Violation violation = new Violation();
            violation.addWarning("messfehler", 631);
            return violation;
        }
        else if (messwert.getMesswertNwg() != null && unsicherheit != null) {
            Violation violation = new Violation();
            violation.addWarning("messfehler", 635);
            return violation;
        }
        return null;
    }
}
