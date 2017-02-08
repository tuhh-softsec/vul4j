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
import de.intevation.lada.model.stammdaten.Staat;
import de.intevation.lada.model.stammdaten.Verwaltungseinheit;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
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
        Integer kda = ort.getKdaId();
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
            srcCoord.x = Double.valueOf(xCoord.replace(",", "."));
            srcCoord.y = Double.valueOf(yCoord.replace(",", "."));
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

    /**
     * Use given attribute to try to add other attributes.
     * To set futher attributes at least one of the following attribute set
     * need to be present:
     * - kda, x, y
     * - gemId
     * - staat
     *
     * @param kda   The koordinatenart
     * @param x     The x coordinate
     * @param y     The y coordinate
     * @param gemId The gemeinde id
     * @param staat The staat id
     */
    public Ort completeOrt(Ort ort) {
        QueryBuilder<Ort> builder =
            new QueryBuilder<Ort>(
                repository.entityManager("stamm"),
                Ort.class);
        logger.debug("try to make a complete ort");
        if (ort.getKdaId() != null &&
            ort.getKoordXExtern() != null &&
            ort.getKoordYExtern() != null
        ) {
            logger.debug("has koordinates");
            builder.and("kdaId", ort.getKdaId());
            builder.and("koordXExtern", ort.getKoordXExtern());
            builder.and("koordYExtern", ort.getKoordYExtern());
            builder.and("ozId", ort.getOzId());
            builder.and("netzbetreiberId", ort.getNetzbetreiberId());
            List<Ort> orte = repository.filterPlain(builder.getQuery(), "stamm");
            if (orte != null && orte.size() > 0) {
                return orte.get(0);
            }
        }
        else if (ort.getGemId() != null) {
            logger.debug("has gemid");
            builder.and("gemId", ort.getGemId());
            builder.and("ozId", ort.getOzId());
            builder.and("netzbetreiberId", ort.getNetzbetreiberId());
            List<Ort> orte = repository.filterPlain(builder.getQuery(), "stamm");
            if (orte != null && orte.size() > 0) {
                logger.debug("found ort: " + orte.get(0).getId());
                return orte.get(0);
            }
        }
        else  if (ort.getStaatId() != null &&
            ort.getStaatId() != 0
        ) {
            logger.debug("has staat");
            builder.and("staatId", ort.getGemId());
            builder.and("ozId", ort.getOzId());
            builder.and("netzbetreiberId", ort.getNetzbetreiberId());
            List<Ort> orte = repository.filterPlain(builder.getQuery(), "stamm");
            if (orte != null && orte.size() > 0) {
                return orte.get(0);
            }
        }

        logger.debug("no ort found");
        return createOrt(ort);
    }

    private Ort createOrt(Ort ort) {
        boolean hasKoord = false;
        boolean hasGem = false;
        boolean hasStaat = false;
        if (ort.getKdaId() != null &&
            ort.getKoordXExtern() != null &&
            ort.getKoordYExtern() != null
        ) {
            logger.debug("transformCoordinates");
            transformCoordinates(ort);
            hasKoord = true;
        }
        if (ort.getGemId() == null && hasKoord) {
            logger.debug("findVerwaltungseinheit");
            findVerwaltungseinheit(ort);
        }
        if (ort.getGemId() != null){
            if (ort.getStaatId() == null) {
                ort.setStaatId(0);
            }
            Verwaltungseinheit v = repository.getByIdPlain(
                Verwaltungseinheit.class,
                ort.getGemId(),
                "stamm");
            if (!hasKoord) {
                ort.setKdaId(4);
                ort.setKoordYExtern(String.valueOf(v.getMittelpunkt().getY()));
                ort.setKoordXExtern(String.valueOf(v.getMittelpunkt().getX()));
            }
            if (ort.getKurztext() == null) {
                ort.setKurztext(v.getBezeichnung());
            }
            if (ort.getLangtext() == null) {
                ort.setLangtext(v.getBezeichnung());
            }
            if (ort.getBerichtstext() == null) {
                ort.setBerichtstext(v.getBezeichnung());
            }
            transformCoordinates(ort);
            hasGem = true;
        }
        if (ort.getStaatId() != null &&
            ort.getStaatId() != 0 &&
            !hasKoord &&
            !hasGem
        ) {
            Staat staat =
                repository.getByIdPlain(Staat.class, ort.getStaatId(), "stamm");
            ort.setKdaId(staat.getKdaId());
            ort.setKoordXExtern(staat.getKoordXExtern());
            ort.setKoordYExtern(staat.getKoordYExtern());
            ort.setKurztext(staat.getStaat());
            ort.setLangtext(staat.getStaat());
            if (staat.getStaatIso() != null) {
                ort.setOrtId("Staat_" + staat.getStaatIso());
            }
            ort.setBerichtstext(staat.getStaat());
            transformCoordinates(ort);
            hasStaat = true;
        }
        return ort;
    }

    /**
     * Use the geom of an ort object to determine the verwaltungseinheit.
     * If verwaltungseinheit was found the gemId is used as reference in the ort
     * object.
     *
     * @param ort   The ort object
     */
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
        return !(errors == null) && !errors.isEmpty();
    }
}
