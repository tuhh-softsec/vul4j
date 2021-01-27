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

import de.intevation.lada.model.land.Ortszuordnung;
import de.intevation.lada.model.land.Probe;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.util.rest.Response;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

/**
 * Validation rule for probe.
 * Validates if the probe has a "entnahmeort".
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@ValidationRule("Probe")
public class HasEntnahmeOrt implements Rule {

    @Inject
    @RepositoryConfig(type = RepositoryType.RO)
    private Repository repo;

    @Override
    public Violation execute(Object object) {
        Probe probe = (Probe) object;
        Integer id = probe.getId();
        if (id == null) {
            Violation violation = new Violation();
            violation.addWarning("entnahmeOrt", 631);
            return violation;
        }
        if (probe.getReiProgpunktGrpId() != null
            || Integer.valueOf(3).equals(probe.getDatenbasisId())
            || Integer.valueOf(4).equals(probe.getDatenbasisId())) {
                return null;
        }
        QueryBuilder<Ortszuordnung> builder =
            new QueryBuilder<Ortszuordnung>(
                repo.entityManager(Strings.LAND), Ortszuordnung.class);
        builder.and("probeId", id);
        Response response = repo.filter(builder.getQuery(), Strings.LAND);
        @SuppressWarnings("unchecked")
        List<Ortszuordnung> orte = (List<Ortszuordnung>) response.getData();
        for (Ortszuordnung ort: orte) {
            if ("E".equals(ort.getOrtszuordnungTyp())) {
                return null;
            }
        }
        Violation violation = new Violation();
        violation.addWarning("entnahmeOrt", 631);
        return violation;
    }

}
