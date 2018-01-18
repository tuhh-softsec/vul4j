/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.validation.rules.probe;

import java.util.List;

import javax.inject.Inject;

import de.intevation.lada.model.land.Probe;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

/**
 * Validation rule for probe.
 * Validates if the probe has a unique "id_alt".
 *
 */
@ValidationRule("Probe")
public class UniqueIdAlt implements Rule {

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repo;

    @Override
    public Violation execute(Object object) {
        Probe probe = (Probe)object;
        QueryBuilder<Probe> builder = new QueryBuilder<Probe>(
            repo.entityManager(Strings.LAND),
            Probe.class);
        builder.and("idAlt", probe.getIdAlt());
        List<Probe> existing = repo.filterPlain(builder.getQuery(), Strings.LAND);
        if (!existing.isEmpty()) {
            Probe found = existing.get(0);
            // The probe found in the db equals the new probe. (Update)
            if (probe.getId() != null && probe.getId().equals(found.getId())) {
                return null;
            }
            Violation violation = new Violation();
            violation.addError("idAlt", 611);
            return violation;
        }
        return null;
    }

}
