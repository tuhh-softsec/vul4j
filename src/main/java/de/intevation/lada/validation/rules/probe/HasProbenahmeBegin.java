package de.intevation.lada.validation.rules.probe;

import java.sql.Timestamp;

import de.intevation.lada.model.land.LProbe;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

@ValidationRule("Probe")
public class HasProbenahmeBegin implements Rule {

    @Override
    public Violation execute(Object object) {
        LProbe probe = (LProbe)object;
        Timestamp begin = probe.getProbeentnahmeBeginn();
        if (begin == null) {
            Violation violation = new Violation();
            violation.addWarning("probeentnahmeBegin", 631);
            return violation;
        }
        return null;
    }

}
