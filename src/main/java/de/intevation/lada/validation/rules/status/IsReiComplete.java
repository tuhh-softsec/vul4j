/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.validation.rules.status;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.Probe;
import de.intevation.lada.model.land.StatusProtokoll;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

/**
 * Validation rule for status.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@ValidationRule("Status")
public class IsReiComplete implements Rule {

    @Inject Logger logger;

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    @Override
    public Violation execute(Object object) {
        StatusProtokoll status = (StatusProtokoll)object;

        Messung messung = repository.getByIdPlain(Messung.class, status.getMessungsId(), "land");
        Probe probe = repository.getByIdPlain(Probe.class, messung.getProbeId(), "land");
        if (!Integer.valueOf(3).equals(probe.getDatenbasisId()) &&
            !Integer.valueOf(4).equals(probe.getDatenbasisId())) {
            return null;
        }
        Violation violation = new Violation();
        if (probe.getReiProgpunktGrpId() == null) {
            violation.addError("rei_progpunkt_grp_id", 631);
        }
        if (probe.getKtaGruppeId() == null) {
            violation.addError("kta_gruppe_id", 631);
        }
        if (violation.hasErrors()) {
            return violation;
        }
        return null;
    }
}
