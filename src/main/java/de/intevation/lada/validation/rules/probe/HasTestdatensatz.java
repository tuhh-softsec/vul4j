package de.intevation.lada.validation.rules.probe;

import de.intevation.lada.model.land.LProbe;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

@ValidationRule("Probe")
public class HasTestdatensatz implements Rule {

    @Override
    public Violation execute(Object object) {
        LProbe probe = (LProbe)object;
        if (probe.getTest() == null ||
            probe.getTest().equals("")) {
            Violation violation = new Violation();
            violation.addError("test", 631);
            return violation;
        }
        return null;
    }
}
