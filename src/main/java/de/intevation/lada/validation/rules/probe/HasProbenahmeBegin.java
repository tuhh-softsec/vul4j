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
 * Validates if the probe has a "probeentnahmeBeginn".
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@ValidationRule("Probe")
public class HasProbenahmeBegin implements Rule {

    @Override
    public Violation execute(Object object) {
        Probe probe = (Probe) object;
        Timestamp begin = probe.getProbeentnahmeBeginn();
        if (begin == null) {
            Violation violation = new Violation();
            violation.addWarning("probeentnahmeBeginn", 631);
            return violation;
        }
        return null;
    }

}
