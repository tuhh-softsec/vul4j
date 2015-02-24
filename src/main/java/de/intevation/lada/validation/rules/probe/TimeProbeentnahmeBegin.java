package de.intevation.lada.validation.rules.probe;

import java.sql.Timestamp;
import java.util.Date;

import de.intevation.lada.model.land.LProbe;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

@ValidationRule("Probe")
public class TimeProbeentnahmeBegin implements Rule {

    @Override
    public Violation execute(Object object) {
        LProbe probe = (LProbe)object;
        Timestamp begin = probe.getProbeentnahmeBeginn();
        Timestamp end = probe.getProbeentnahmeEnde();
        if (begin == null && end == null) {
            return null;
        }
        if (begin == null && end != null) {
            Violation violation = new Violation();
            violation.addWarning("probeentnahmeBegin", 662);
            return violation;
        }
        if (begin.after(new Date())) {
            Violation violation = new Violation();
            violation.addWarning("probeentnahmeBegin", 661);
            return violation;
        }
        if (end == null) {
            return null;
        }
        if (begin.after(end)) {
            Violation violation = new Violation();
            violation.addWarning("probeentnahmeBegin", 662);
            return violation;
        }
        return null;
    }
}
