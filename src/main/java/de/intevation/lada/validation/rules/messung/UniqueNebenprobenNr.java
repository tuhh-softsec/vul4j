/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.validation.rules.messung;

import java.util.List;

import javax.inject.Inject;

import de.intevation.lada.model.land.Messung;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
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
 * Validates if the nebenprobennr is unique for a probe.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@ValidationRule("Messung")
public class UniqueNebenprobenNr implements Rule {

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repo;

    @SuppressWarnings("unchecked")
    @Override
    public Violation execute(Object object) {
        Messung messung= (Messung)object;
        if (messung.getNebenprobenNr() != null) {
            QueryBuilder<Messung> builder = new QueryBuilder<Messung>(
                repo.entityManager(Strings.LAND),
                Messung.class);
            builder.and("nebenprobenNr", messung.getNebenprobenNr());
            builder.and("probeId", messung.getProbeId());
            Response response = repo.filter(builder.getQuery(), Strings.LAND);
            if (!((List<Messung>)response.getData()).isEmpty()) {
                Messung found = ((List<Messung>)response.getData()).get(0);
                // The messung found in the db equals the new messung. (Update)
                if (messung.getId() != null &&
                    messung.getId().equals(found.getId())) {
                    return null;
                }
                Violation violation = new Violation();
                violation.addError("nebenprobenNr", StatusCodes.VALUE_AMBIGOUS);
                return violation;
            }
        }
        return null;
    }

}
