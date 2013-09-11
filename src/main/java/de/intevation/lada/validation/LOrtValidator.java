package de.intevation.lada.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

import de.intevation.lada.data.QueryBuilder;
import de.intevation.lada.data.Repository;
import de.intevation.lada.model.DeVg;
import de.intevation.lada.model.LOrt;
import de.intevation.lada.model.Ort;
import de.intevation.lada.model.SVerwaltungseinheit;
import de.intevation.lada.rest.Response;

/**
 * Validator for LOrt objects.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@ApplicationScoped
@Named("lortvalidator")
public class LOrtValidator
implements Validator
{
    @Inject
    @Named("readonlyrepository")
    private Repository readonlyRepo;

    @Inject
    @Named("ortrepository")
    private Repository ortRepo;

    /**
     * Validate a LOrt object.
     *
     * @param object    The LOrt object.
     * @param update    The database operation.
     *                  TRUE indicates that the object should be updated, FALSE
     *                  if the object is a new Object.
     * @return Map containing warnings.
     */
    @Override
    public Map<String, Integer> validate(Object object, boolean update)
    throws ValidationException {
        Map<String, Integer> warnings = new HashMap<String, Integer>();
        if (!(object instanceof LOrt)) {
            Map<String, Integer> errors = new HashMap<String, Integer>();
            errors.put("lort", 610);
            throw new ValidationException(errors);
        }
        LOrt ort = (LOrt)object;
        validateVerwaltungseinheit(ort, warnings);
        //TODO: more validation, see LSB: VI - Konsistenzregeln
        return warnings;
    }

    /**
     * Check if the given lat/lon coordinates are within the area of the
     * verwaltungseinheit.
     *
     * @param ort
     * @param warnings
     */
    private void validateVerwaltungseinheit(LOrt ort, Map<String, Integer> warnings) {
        if (!ort.getOrtsTyp().equals("E")) {
            return;
        }
        QueryBuilder<Ort> ortBuilder =
            new QueryBuilder<Ort>(ortRepo.getEntityManager(), Ort.class);
        ortBuilder.and("ortId", ort.getOrtId());
        Response response = ortRepo.filter(ortBuilder.getQuery());
        List<Ort> orte = (List<Ort>)response.getData();
        QueryBuilder<SVerwaltungseinheit> veBuilder =
            new QueryBuilder<SVerwaltungseinheit>(
                readonlyRepo.getEntityManager(), SVerwaltungseinheit.class);
        veBuilder.and("gemId", orte.get(0).getGemId());
        Response ver = readonlyRepo.filter(veBuilder.getQuery());
        SVerwaltungseinheit ve = ((List<SVerwaltungseinheit>)ver.getData()).get(0);
        QueryBuilder<DeVg> vg =
            new QueryBuilder<DeVg>(readonlyRepo.getEntityManager(), DeVg.class);
        vg.and("ags", ve.getGemId());
        Response rvg = readonlyRepo.filter(vg.getQuery());
        List<DeVg> vgs = (List<DeVg>)rvg.getData();
        if (vgs == null || vgs.isEmpty()) {
            warnings.put("verwaltungseinheit", 653);
            return;
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
            warnings.put("verwaltungseinheit", 651);
        }
    }
}
