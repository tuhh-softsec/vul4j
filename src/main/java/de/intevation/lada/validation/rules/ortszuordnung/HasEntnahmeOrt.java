/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.validation.rules.ortszuordnung;

import java.util.List;

import javax.inject.Inject;

import de.intevation.lada.model.land.Ortszuordnung;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

@ValidationRule("Ortszuordnung")
public class HasEntnahmeOrt implements Rule {

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    @Override
    public Violation execute(Object object) {
        Ortszuordnung ort = (Ortszuordnung)object;
        if (!"E".equals(ort.getOrtszuordnungTyp())) {
            return null;
        }

        QueryBuilder<Ortszuordnung> builder = new QueryBuilder<Ortszuordnung>(
            repository.entityManager("land"),
            Ortszuordnung.class);
        builder.and("probeId", ort.getProbeId());
        List<Ortszuordnung> orte = repository.filterPlain(
            builder.getQuery(),
            "land");
        for (Ortszuordnung o : orte) {
            if ("E".equals(o.getOrtszuordnungTyp()) &&
                !o.getId().equals(ort.getId())
            ) {
                Violation violation = new Violation();
                violation.addError("ortszuordnungsTyp", 611);
                return violation;
            }
        }

        return null;
    }
}
