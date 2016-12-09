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

import com.vividsolutions.jts.geom.Point;

import de.intevation.lada.model.land.Ortszuordnung;
import de.intevation.lada.model.stammdaten.Ort;
import de.intevation.lada.model.stammdaten.Verwaltungseinheit;
import de.intevation.lada.model.stammdaten.Verwaltungsgrenze;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

/**
 * Validation rule for ort.
 * Validates if the coordinates are in the specified "Verwaltungseinheit".
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@ValidationRule("Ortszuordnung")
public class CoordinatesInVE implements Rule {

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    @SuppressWarnings("unchecked")
    @Override
    public Violation execute(Object object) {
        Ortszuordnung oz = (Ortszuordnung)object;
        if (!"E".equals(oz.getOrtszuordnungTyp())) {
            return null;
        }

        Ort ort = repository.getByIdPlain(Ort.class, oz.getOrtId(), "stamm");
        String gemId = ort.getGemId();
        if (gemId == null) {
            Violation violation = new Violation();
            violation.addWarning("gemId", 631);
            return violation;
        }

        QueryBuilder<Verwaltungsgrenze> vg =
            new QueryBuilder<Verwaltungsgrenze>(
                repository.entityManager("stamm"), Verwaltungsgrenze.class);
        vg.and("gemId", gemId);
        List<Verwaltungsgrenze> vgs = repository.filterPlain(
            vg.getQuery(), "stamm");
        if (vgs == null || vgs.isEmpty()) {
            Violation violation = new Violation();
            violation.addWarning("verwaltungsgrenze", 600);
            return violation;
        }

        Point p = ort.getGeom();
        boolean hit = false;
        for (Verwaltungsgrenze singlevg : vgs) {
            if(singlevg.getShape().contains(p)) {
                hit = true;
            }
        }
        if (!hit) {
            Violation violation = new Violation();
            violation.addWarning("verwaltungsgrenze", 651);
            return violation;
        }
        return null;
    }

}
