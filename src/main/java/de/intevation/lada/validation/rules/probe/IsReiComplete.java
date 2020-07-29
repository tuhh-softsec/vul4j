/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.validation.rules.probe;

import javax.inject.Inject;

import de.intevation.lada.model.land.Probe;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

/**
 * Validation rule for probe.
 * Validates if the probe has valid REI attributes.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@ValidationRule("Probe")
public class IsReiComplete implements Rule {

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    @Override
    public Violation execute(Object object) {
        Probe probe = (Probe)object;
        Violation violation = new Violation();
        if (probe.getDatenbasisId() == null) {
            return null;
        }
        if (probe.getDatenbasisId() != 3 &&
            probe.getDatenbasisId() != 4) {
            if (probe.getReiProgpunktGrpId() != null) {
                violation.addError("reiProgpunktGruppeId", 632);
            }
            if (probe.getKtaGruppeId() != null) {
                violation.addError("ktaGruppeId", 632);
            }
            if (violation.hasErrors()) {
                return violation;
            }
            return null;
        }
        if (probe.getReiProgpunktGrpId() == null) {
            violation.addWarning("reiProgpunktGruppeId", 631);
        }
        if (probe.getKtaGruppeId() == null) {
            violation.addWarning("ktaGruppeId", 631);
        }
        if (violation.hasWarnings()) {
            return violation;
        }
        return null;
    }
}
