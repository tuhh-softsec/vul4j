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
import de.intevation.lada.model.stammdaten.StatusKombi;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
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
public class StatusKombination implements Rule {

    @Inject Logger logger;

    @Inject
    @RepositoryConfig(type = RepositoryType.RO)
    private Repository repository;

    @Override
    public Violation execute(Object object) {
        StatusProtokoll status = (StatusProtokoll) object;
        QueryBuilder<StatusKombi> kombi = new QueryBuilder<StatusKombi>(
            repository.entityManager(Strings.STAMM),
            StatusKombi.class);
        kombi.and("id", status.getStatusKombi());
        List<StatusKombi> result =
            repository.filterPlain(kombi.getQuery(), Strings.STAMM);
        if (result.isEmpty()) {
            Violation violation = new Violation();
            violation.addError("kombi", 632);
            return violation;
        }
        return null;
    }
}
