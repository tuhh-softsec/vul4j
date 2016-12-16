/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.validation.rules.ort;

import javax.inject.Inject;

import de.intevation.lada.model.stammdaten.Ort;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

@ValidationRule("Ort")
public class IsUnique implements Rule {

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    @Override
    public Violation execute(Object object) {
        Ort ort = (Ort)object;

        QueryBuilder<Ort> builder = new QueryBuilder<Ort>(
            repository.entityManager("stamm"), Ort.class);
        if (ort.getId() != null) {
            // Consider UPDATE
            builder.and("id", ort.getId()).not();
        }
        builder.and("netzbetreiberId", ort.getNetzbetreiberId());
        builder.and("ortId", ort.getOrtId());
        if (!repository.filterPlain(builder.getQuery(), "stamm").isEmpty()) {
            Violation violation = new Violation();
            violation.addError("ortId", 672);
            return violation;
        }

        return null;
    }
}
