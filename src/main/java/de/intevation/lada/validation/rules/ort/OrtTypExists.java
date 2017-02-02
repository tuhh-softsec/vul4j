/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.validation.rules.ort;

import java.util.List;

import javax.inject.Inject;

import de.intevation.lada.model.stammdaten.Ort;
import de.intevation.lada.model.stammdaten.OrtTyp;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

/**
 * Validation rule for ort.
 * Validates if the a given OrtTyp exists.
 *
 */
@ValidationRule("Ort")
public class OrtTypExists implements Rule {

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    @Override
    public Violation execute(Object object) {
        Ort ort = (Ort)object;

        QueryBuilder<OrtTyp> builder =
            new QueryBuilder<OrtTyp>(
                repository.entityManager("stamm"),
                OrtTyp.class);
        builder.and("id", ort.getOrtTyp());
        List<OrtTyp> ots = repository.filterPlain(
            builder.getQuery(), "stamm");
        if (ots == null || ots.isEmpty()) {
            Violation violation = new Violation();
            violation.addError("ortTyp", 612);
            return violation;
        }

        return null;
    }

}
