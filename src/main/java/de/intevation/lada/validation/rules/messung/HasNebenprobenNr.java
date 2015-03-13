package de.intevation.lada.validation.rules.messung;

import de.intevation.lada.model.land.LMessung;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

@ValidationRule("Messung")
public class HasNebenprobenNr implements Rule {

    @Override
    public Violation execute(Object object) {
        LMessung messung = (LMessung)object;
        if (messung.getNebenprobenNr() == null ||
            messung.getNebenprobenNr().equals("")) {
            Violation violation = new Violation();
            violation.addWarning("nebenprobenNr", 631);
            return violation;
        }
        return null;
    }

}
