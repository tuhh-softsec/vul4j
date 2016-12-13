/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.factory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import de.intevation.lada.importer.ReportItem;
import de.intevation.lada.model.stammdaten.Ort;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;

public class OrtFactory {

    @Inject
    private Logger logger;

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    private List<ReportItem> errors;

    public void transformCoordinates(Ort ort) {
        errors = new ArrayList<ReportItem>();
        int kda = ort.getKdaId();
        String epsg = null;
        String xCoord = null;
        String yCoord = null;
        switch(kda) {
            case 4: epsg = "EPSG:4326";
                    /* EPSG:4326 defines the order of latitude and longitude
                     * the other way round than IMIS coordinates specification.
                     */
                    xCoord = ort.getKoordYExtern();
                    yCoord = ort.getKoordXExtern();
                    break;
            case 5: epsg = getEpsgForWgsUtm(ort.getKoordXExtern());
                    xCoord = ort.getKoordXExtern().length() == 7 ?
                        ort.getKoordXExtern().substring(1, 7) :
                        ort.getKoordXExtern().substring(2, 8);
                    yCoord = ort.getKoordYExtern();
                    break;
            default: ReportItem err = new ReportItem();
                err.setCode(612);
                err.setKey("kdaId");
                err.setValue(ort.getKdaId().toString());
                errors.add(err);
                return;
        }
        try {
            CoordinateReferenceSystem src = CRS.decode(epsg);
            CoordinateReferenceSystem target = CRS.decode("EPSG:4326");

            MathTransform transform = CRS.findMathTransform(src, target);
            Coordinate srcCoord = new Coordinate();
            srcCoord.x = Double.valueOf(xCoord);
            srcCoord.y = Double.valueOf(yCoord);
            Coordinate targetCoord = new Coordinate();
            JTS.transform(srcCoord, targetCoord, transform);

            ort.setGeom(generateGeom(targetCoord.y, targetCoord.x));
        } catch (FactoryException |
                TransformException e) {
            logger.error(e.getMessage());
            ReportItem err = new ReportItem();
            err.setCode(672);
            err.setKey("coordinates");
            err.setValue(ort.getKdaId() + " " +
                ort.getKoordXExtern() + " " + ort.getKoordYExtern());
            errors.add(err);
            return;
        }
    }

    public void findVerwaltungseinheit(Ort ort) {
        if (ort.getGeom() == null) {
            return;
        }
        Query q = repository.entityManager("stamm")
            .createQuery("SELECT vg.gemId " +
                         "FROM Verwaltungsgrenze vg " +
                         "WHERE contains(vg.shape, :geom) = TRUE");
        q.setParameter("geom", ort.getGeom());
        List<Object> ret = q.getResultList();
        if (!ret.isEmpty()) {
            ort.setGemId(ret.get(0).toString());
            ort.setStaatId(0);
        }
        return;
    }

    private Point generateGeom(Double x, Double y) {
        GeometryFactory geomFactory = new GeometryFactory();
        Coordinate coord = new Coordinate(x, y);
        Point geom = geomFactory.createPoint(coord);
        geom.setSRID(4326);
        return geom;
    }

    private String getEpsgForWgsUtm(String x) {
        String epsg = "EPSG:326";
        String part = x.split(",")[0];
        String zone = part.length() == 7 ? ("0" + part.substring(0, 1)) :
            part.substring(0, 2);
        return epsg + zone;
    }

    public List<ReportItem> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
