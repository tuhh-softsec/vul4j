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

import de.intevation.lada.model.land.LStatusProtokoll;
import de.intevation.lada.model.stamm.StatusKombi;
import de.intevation.lada.model.stamm.StatusReihenfolge;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
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
public class StatusFolge implements Rule {

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    @Override
    public Violation execute(Object object) {
        LStatusProtokoll status = (LStatusProtokoll)object;
        QueryBuilder<StatusKombi> kombi = new QueryBuilder<StatusKombi>(
            repository.entityManager("stamm"),
            StatusKombi.class);
        kombi.and("stufeId", status.getStatusStufe());
        kombi.and("wertId", status.getStatusWert());
        List<StatusKombi> result =
            repository.filterPlain(kombi.getQuery(), "stamm");
        if (result.isEmpty()) {
            Violation violation = new Violation();
            violation.addError("status", 632);
            return violation;
        }

        // Get the previous status
        QueryBuilder<LStatusProtokoll> lastFilter =
            new QueryBuilder<LStatusProtokoll>(
                    repository.entityManager("land"),
                    LStatusProtokoll.class);

        lastFilter.and("messungsId", status.getMessungsId());
        lastFilter.orderBy("datum", true);
        List<LStatusProtokoll> protos =
            repository.filterPlain(lastFilter.getQuery(), "land");
        if (protos.isEmpty()) {
            return null;
        }
        LStatusProtokoll last = protos.get(protos.size() - 1);
        QueryBuilder<StatusKombi> kombi2 = kombi.getEmptyBuilder();
        kombi2.and("stufeId", last.getStatusStufe());
        kombi2.and("wertId", last.getStatusWert());
        List<StatusKombi> result2 =
            repository.filterPlain(kombi2.getQuery(), "stamm");
        if (result2.isEmpty()) {
            Violation violation = new Violation();
            violation.addError("status", 632);
            return violation;
        }
        QueryBuilder<StatusReihenfolge> folgeFilter =
            new QueryBuilder<StatusReihenfolge>(
                repository.entityManager("stamm"),
                StatusReihenfolge.class);
        folgeFilter.and("von", result2.get(0).getId());
        folgeFilter.and("zu", result.get(0).getId());
        List<StatusReihenfolge> reihenfolge =
            repository.filterPlain(folgeFilter.getQuery(), "stamm");
        if (reihenfolge.isEmpty()) {
            Violation violation = new Violation();
            violation.addError("status", 632);
            return violation;
        }

        return null;
    }
}
