/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.validation.rules.status;

import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.intevation.lada.model.land.StatusProtokoll;
import de.intevation.lada.model.stammdaten.StatusReihenfolge;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.StatusCodes;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

/**
 * Validation rule for status.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@ValidationRule("Status")
public class StatusFolge implements Rule {

    @Inject Logger logger;

    @Inject
    @RepositoryConfig(type = RepositoryType.RO)
    private Repository repository;

    @Override
    public Violation execute(Object object) {
        StatusProtokoll status = (StatusProtokoll) object;

        // Get the previous status
        QueryBuilder<StatusProtokoll> lastFilter =
            new QueryBuilder<StatusProtokoll>(
                    repository.entityManager(Strings.LAND),
                    StatusProtokoll.class);

        lastFilter.and("messungsId", status.getMessungsId());
        lastFilter.orderBy("datum", true);
        List<StatusProtokoll> protos =
            repository.filterPlain(lastFilter.getQuery(), Strings.LAND);
        if (protos.isEmpty()) {
            return null;
        }
        StatusProtokoll last = protos.get(protos.size() - 1);
        QueryBuilder<StatusReihenfolge> folgeFilter =
            new QueryBuilder<StatusReihenfolge>(
                repository.entityManager(Strings.STAMM),
                StatusReihenfolge.class);
        folgeFilter.and("vonId", last.getStatusKombi());
        folgeFilter.and("zuId", status.getStatusKombi());
        List<StatusReihenfolge> reihenfolge =
            repository.filterPlain(folgeFilter.getQuery(), Strings.STAMM);
        if (reihenfolge.isEmpty()) {
            Violation violation = new Violation();
            violation.addError("status", StatusCodes.VALUE_NOT_MATCHING);
            return violation;
        }

        return null;
    }
}
