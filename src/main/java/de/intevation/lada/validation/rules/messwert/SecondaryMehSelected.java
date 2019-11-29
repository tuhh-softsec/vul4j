/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.validation.rules.messwert;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import de.intevation.lada.model.land.Messwert;
import de.intevation.lada.model.stammdaten.MassEinheitUmrechnung;
import de.intevation.lada.model.stammdaten.MessEinheit;
import de.intevation.lada.model.stammdaten.Umwelt;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

/**
 * Validation rule for messwert.
 * Validates if the "messeinheit" is the secondary "messeinheit" of to umweltbereich
 * connected to this messwert
 */
@ValidationRule("Messwert")
public class SecondaryMehSelected implements Rule {

    /**
     * The data repository granting read access.
     */
    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    @Override
    public Violation execute(Object object) {
        Messwert messwert = (Messwert)object;
        Umwelt umwelt = null;
        Violation violation = new Violation();

        if (messwert.getMessung() != null
                && messwert.getMessung().getProbe() != null) {
            umwelt = messwert.getMessung().getProbe().getUmwelt();
        }

        // If umwelt record is present
        if (umwelt != null) {
            Integer mehId = umwelt.getMehId();
            Integer secMehId = umwelt.getSecMehId();
            //If secondary meh is set
            if (secMehId == null) {
                return null;
            }
            //Check if the messwert is the secondary mehId
            if (secMehId != null && secMehId.equals(messwert.getMehId())) {
                violation.addWarning("mehId", 636);
                return violation;
            }
            /*Check if the messwert is convertable into the secondary unit but
            not into the primary */
            MessEinheit meh = repository.getByIdPlain(MessEinheit.class, mehId, Strings.STAMM);
            MessEinheit secMeh = repository.getByIdPlain(MessEinheit.class, secMehId, Strings.STAMM);
            AtomicBoolean primary = new AtomicBoolean(false);
            meh.getMassEinheitUmrechnungZus().forEach(umrechnung -> {
                if (umrechnung.getMehVon().getId().equals(messwert.getMehId())) {
                    primary.set(true);
                }
            });
            if (primary.get() == true) {
                return null;
            }
            secMeh.getMassEinheitUmrechnungZus().forEach(secUmrechnung -> {
                if (secUmrechnung.getMehVon().getId().equals(messwert.getMehId())) {
                    violation.addWarning("mehId", 636);
                }
            });
        }
        return violation;
    }
}
