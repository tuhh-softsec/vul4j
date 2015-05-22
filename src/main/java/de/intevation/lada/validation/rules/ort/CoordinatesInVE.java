package de.intevation.lada.validation.rules.ort;

import java.util.List;

import javax.inject.Inject;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

import de.intevation.lada.model.land.LOrt;
import de.intevation.lada.model.stamm.DeVg;
import de.intevation.lada.model.stamm.SOrt;
import de.intevation.lada.model.stamm.Verwaltungseinheit;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.Response;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

@ValidationRule("Ort")
public class CoordinatesInVE implements Rule {

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    @Override
    public Violation execute(Object object) {
        LOrt ort = (LOrt)object;
        if (!"E".equals(ort.getOrtsTyp())) {
            return null;
        }
        QueryBuilder<SOrt> ortBuilder =
            new QueryBuilder<SOrt>(repository.entityManager("stamm"), SOrt.class);
        ortBuilder.and("id", ort.getOrt());
        Response response = repository.filter(ortBuilder.getQuery(), "stamm");
        List<SOrt> orte = (List<SOrt>)response.getData();
        QueryBuilder<Verwaltungseinheit> veBuilder =
            new QueryBuilder<Verwaltungseinheit>(
                repository.entityManager("stamm"), Verwaltungseinheit.class);
        veBuilder.and("id", orte.get(0).getVerwaltungseinheitId());
        Response ver = repository.filter(veBuilder.getQuery(), "stamm");
        if (((List<Verwaltungseinheit>)ver.getData()).isEmpty()) {
            Violation violation = new Violation();
            violation.addWarning("verwaltungseinheit", 653);
            return violation;
        }
        Verwaltungseinheit ve = ((List<Verwaltungseinheit>)ver.getData()).get(0);
        QueryBuilder<DeVg> vg =
            new QueryBuilder<DeVg>(repository.entityManager("stamm"), DeVg.class);
        vg.and("ags", ve.getId());
        Response rvg = repository.filter(vg.getQuery(), "stamm");
        List<DeVg> vgs = (List<DeVg>)rvg.getData();
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
        for (DeVg singlevg : vgs) {
            if(singlevg.getGeom().contains(p)) {
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
