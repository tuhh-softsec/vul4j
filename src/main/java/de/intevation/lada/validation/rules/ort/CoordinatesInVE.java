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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

import de.intevation.lada.model.land.Ortszuordnung;
import de.intevation.lada.model.stammdaten.Ort;
import de.intevation.lada.model.stammdaten.Verwaltungseinheit;
import de.intevation.lada.model.stammdaten.Verwaltungsgrenze;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.Response;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

/**
 * Validation rule for ort.
 * Validates if the coordinates are in the specified "Verwaltungseinheit".
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@ValidationRule("Ort")
public class CoordinatesInVE implements Rule {

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    @SuppressWarnings("unchecked")
    @Override
    public Violation execute(Object object) {
        Ortszuordnung ort = (Ortszuordnung)object;
        if (!"E".equals(ort.getOrtszuordnungTyp())) {
            return null;
        }
        QueryBuilder<Ort> ortBuilder =
            new QueryBuilder<Ort>(repository.entityManager("stamm"), Ort.class);
        ortBuilder.and("id", ort.getOrtId());
        Response response = repository.filter(ortBuilder.getQuery(), "stamm");
        List<Ort> orte = (List<Ort>)response.getData();
        QueryBuilder<Verwaltungseinheit> veBuilder =
            new QueryBuilder<Verwaltungseinheit>(
                repository.entityManager("stamm"), Verwaltungseinheit.class);
        veBuilder.and("id", orte.get(0).getGemId());
        Response ver = repository.filter(veBuilder.getQuery(), "stamm");
        if (((List<Verwaltungseinheit>)ver.getData()).isEmpty()) {
            Violation violation = new Violation();
            violation.addWarning("verwaltungseinheit", 653);
            return violation;
        }
        Verwaltungseinheit ve = ((List<Verwaltungseinheit>)ver.getData()).get(0);
        QueryBuilder<Verwaltungsgrenze> vg =
            new QueryBuilder<Verwaltungsgrenze>(repository.entityManager("stamm"), Verwaltungsgrenze.class);
        vg.and("ags", ve.getId());
        Response rvg = repository.filter(vg.getQuery(), "stamm");
        List<Verwaltungsgrenze> vgs = (List<Verwaltungsgrenze>)rvg.getData();
        if (vgs == null || vgs.isEmpty()) {
            Violation violation = new Violation();
            violation.addWarning("verwaltungseinheit", 653);
            return violation;
        }
        double y = orte.get(0).getLatitude();
        double x = orte.get(0).getLongitude();
        Coordinate c = new Coordinate(x, y);
        GeometryFactory gf = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);
        Point p = gf.createPoint(c);
        boolean hit = false;
        for (Verwaltungsgrenze singlevg : vgs) {
            if(singlevg.getShape().contains(p)) {
                hit = true;
            }
        }
        if (!hit) {
            Violation violation = new Violation();
            violation.addWarning("verwaltungseinheit", 651);
            return violation;
        }
        return null;
    }

}
