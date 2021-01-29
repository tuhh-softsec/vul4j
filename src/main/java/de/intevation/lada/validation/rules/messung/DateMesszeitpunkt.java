/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.validation.rules.messung;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;

import javax.inject.Inject;

import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.Probe;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.StatusCodes;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.util.rest.Response;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

/**
 * Validation rule for messungen.
 * Validates if the "messzeitpunkt" is before or after the
 * "probeentnahmebeginn"
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@ValidationRule("Messung")
public class DateMesszeitpunkt implements Rule {

    @Inject
    @RepositoryConfig(type = RepositoryType.RO)
    private Repository repository;

    @Override
    public Violation execute(Object object) {
        Messung messung = (Messung) object;
        Integer probeId = messung.getProbeId();
        Response response =
            repository.getById(Probe.class, probeId, Strings.LAND);
        Probe probe = (Probe) response.getData();

        if (probe == null) {
            Map<String, Integer> errors = new HashMap<String, Integer>();
            errors.put("lprobe", StatusCodes.ERROR_VALIDATION);
            return null;
        }

        if (messung.getMesszeitpunkt() == null) {
            return null;
        }

        if (messung.getMesszeitpunkt().after(new Date())) {
            Violation violation = new Violation();
            violation.addWarning("messzeitpunkt", StatusCodes.DATE_IN_FUTURE);
            return violation;
        }

        if (probe.getProbeentnahmeBeginn() == null
            && probe.getProbeentnahmeEnde() == null) {
            return null;
        }

        if ((probe.getProbeentnahmeBeginn() != null
            && probe.getProbeentnahmeBeginn().after(messung.getMesszeitpunkt())
            || probe.getProbeentnahmeEnde() != null
            && probe.getProbeentnahmeEnde().after(messung.getMesszeitpunkt()))
            && (probe.getProbenartId() != null
                && (probe.getProbenartId() == 3
                    || probe.getProbenartId() == 9))
        ) {
            Violation violation = new Violation();
            violation.addWarning(
                "messzeitpunkt#" + messung.getNebenprobenNr(),
                StatusCodes.VALUE_NOT_MATCHING);
            return violation;
        }
        return null;
    }
}
