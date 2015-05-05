package de.intevation.lada.validation.rules.Messwert;

import de.intevation.lada.model.land.LMesswert;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

@ValidationRule("Messwert")
public class HasMessunsicherheit implements Rule {

    @Override
    public Violation execute(Object object) {
        LMesswert messwert = (LMesswert)object;
        Float unsicherheit = messwert.getMessfehler();
        Float nachweisgrenze = messwert.getNwgZuMesswert();
        Float wert = messwert.getMesswert();
        if (unsicherheit != null && unsicherheit > 0f) {
            return null;
        }
        else if (nachweisgrenze != null && wert < nachweisgrenze) {
            return null;
        }
        Violation violation = new Violation();
        violation.addWarning("messwewrt", 631);
        return violation;
    }
}
