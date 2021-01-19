/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.validation.rules.probe;

import java.sql.Timestamp;
import de.intevation.lada.model.land.Probe;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

/**
 * Validation rule for probe.
 * Validates if the "probeart" matches date values.
 *
 */
@ValidationRule("Probe")
public class CheckProbeart implements Rule {

    @Override
    public Violation execute(Object object) {
        Probe probe = (Probe) object;
        Timestamp end = probe.getProbeentnahmeEnde();
        Timestamp begin = probe.getProbeentnahmeBeginn();
        if (probe.getProbenartId() != null
             && probe.getDatenbasisId() != 1) {
          if (begin != null && end != null
              && !begin.equals(end)
              && probe.getProbenartId() == 1) {
            Violation violation = new Violation();
            violation.addWarning("probenartId", 639);
            return violation;
          }
        } else {
            return null;
          }
        return null;
    }
}
