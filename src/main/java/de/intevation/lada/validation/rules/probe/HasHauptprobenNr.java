package de.intevation.lada.validation.rules.probe;

import de.intevation.lada.model.land.LProbe;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

@ValidationRule("Probe")
public class HasHauptprobenNr implements Rule {

    @Override
    public Violation execute(Object object) {
        LProbe probe = (LProbe)object;
        if (probe.getHauptprobenNr() == null ||
            probe.getHauptprobenNr().equals("")) {
            Violation violation = new Violation();
            violation.addError("hauptprobenNr", 631);
            return violation;
        }
        return null;
    }
}
