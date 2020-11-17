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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import de.intevation.lada.importer.ReportItem;
import de.intevation.lada.model.stammdaten.Ort;
import de.intevation.lada.model.stammdaten.Staat;
import de.intevation.lada.model.stammdaten.Verwaltungseinheit;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.KdaUtil;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;

/**
 * Class to create, transform and complete ort objects.
 */
public class OrtFactory {

    private static final int LEN7 = 7;

    private static final int EPSG4326 = 4326;

    private static final int ERR611 = 611;

    private static final int ORTTYP5 = 5;

    private static final int ERR652 = 652;

    private static final int KDA4 = 4;

    private static final int ERR675 = 675;
    private static final int ZONE2 = 2;
    private static final int ZONE3 = 3;
    private static final int ZONE4 = 4;
    private static final int ZONE5 = 5;

    @Inject
    @RepositoryConfig(type = RepositoryType.RO)
    private Repository repository;

    private List<ReportItem> errors;

    /**
     * Transform the external coordinates to the geom representation.
     * @param ort the ort
     */
    public void transformCoordinates(Ort ort) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        if (ort.getKdaId() == null
            || ort.getKoordXExtern() == null
            || ort.getKoordXExtern().equals("")
            || ort.getKoordYExtern() == null
            || ort.getKoordYExtern().equals("")) {
            ReportItem err = new ReportItem();
            err.setCode(ERR675);
            err.setKey("coordinates");
            err.setValue(ort.getKdaId()
                + " " + ort.getKoordXExtern() + " " + ort.getKoordYExtern());
            errors.add(err);
            return;
        }
        Integer kda = ort.getKdaId();
        String xCoord = ort.getKoordXExtern();
        String yCoord = ort.getKoordYExtern();

        KdaUtil util = new KdaUtil();
        ObjectNode coords = util.transform(kda, KDA4, xCoord, yCoord);
        if (coords == null) {
            ReportItem err = new ReportItem();
            err.setCode(ERR652);
            err.setKey("coordinates");
            err.setValue(ort.getKdaId()
                + " " + ort.getKoordXExtern() + " " + ort.getKoordYExtern());
            errors.add(err);
            return;
        }
        ort.setGeom(
            generateGeom(coords.get("x").asDouble(),
            coords.get("y").asDouble()));
        return;
    }


    /**
     * Use given attribute to try to add other attributes.
     * To set futher attributes at least one of the following attribute set
     * need to be present:
     * - kda, x, y
     * - gemId
     * - staat
     *
     * @param ort The incomplete ort
     * @return The resulting ort.
     */
    public Ort completeOrt(Ort ort) {
        if (errors == null) {
            errors = new ArrayList<ReportItem>();
        } else {
            errors.clear();
        }
        QueryBuilder<Ort> builder =
            new QueryBuilder<>(
                repository.entityManager(Strings.STAMM),
                Ort.class);
        if (ort.getKdaId() != null
            && ort.getKoordXExtern() != null
            && ort.getKoordYExtern() != null
        ) {
            builder.and("kdaId", ort.getKdaId());
            builder.and("koordXExtern", ort.getKoordXExtern());
            builder.and("koordYExtern", ort.getKoordYExtern());
            builder.and("ozId", ort.getOzId());
            builder.and("netzbetreiberId", ort.getNetzbetreiberId());
            List<Ort> orte =
                repository.filterPlain(builder.getQuery(), Strings.STAMM);
            if (orte != null && !orte.isEmpty()) {
                return orte.get(0);
            }
        } else if (ort.getGemId() != null) {
            builder.and("gemId", ort.getGemId());
            builder.and("ozId", ort.getOzId());
            builder.and("netzbetreiberId", ort.getNetzbetreiberId());
            List<Ort> orte =
                repository.filterPlain(builder.getQuery(), Strings.STAMM);
            if (orte != null && !orte.isEmpty()) {
                return orte.get(0);
            }
        } else  if (ort.getStaatId() != null) {
            builder.and("staatId", ort.getStaatId());
            builder.and("ortTyp", ORTTYP5);
            builder.and("ozId", ort.getOzId());
            builder.and("netzbetreiberId", ort.getNetzbetreiberId());
            List<Ort> orte =
                repository.filterPlain(builder.getQuery(), Strings.STAMM);
            if (orte != null && !orte.isEmpty()) {
                return orte.get(0);
            }
        }

        return createOrt(ort);
    }

    private Ort createOrt(Ort ort) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        boolean hasKoord = false;
        boolean hasGem = false;
        boolean hasStaat = false;
        if (ort.getKdaId() != null
            && ort.getKoordXExtern() != null
            && ort.getKoordYExtern() != null
        ) {
            transformCoordinates(ort);
            hasKoord = true;
        }
        if (ort.getGemId() == null && hasKoord) {
            findVerwaltungseinheit(ort);
        }
        if (ort.getGemId() != null) {
            if (ort.getStaatId() == null) {
                ort.setStaatId(0);
            }
            Verwaltungseinheit v = repository.getByIdPlain(
                Verwaltungseinheit.class,
                ort.getGemId(),
                Strings.STAMM);
            if (v == null) {
                ReportItem err = new ReportItem();
                err.setCode(ERR675);
                err.setKey("gem_id");
                err.setValue(ort.getGemId());
                errors.add(err);
                return null;
            } else {
                if (!hasKoord) {
                    ort.setKdaId(KDA4);
                    ort.setKoordYExtern(
                        String.valueOf(v.getMittelpunkt().getY()));
                    ort.setKoordXExtern(
                        String.valueOf(v.getMittelpunkt().getX()));
                }
                if (ort.getLangtext() == null || ort.getLangtext().equals("")) {
                    ort.setLangtext(v.getBezeichnung());
                }
                if (ort.getBerichtstext() == null
                    || ort.getBerichtstext().equals("")
                ) {
                    ort.setBerichtstext(v.getBezeichnung());
                }
                transformCoordinates(ort);
                hasGem = true;
            }
        }
        if (ort.getStaatId() != null
            && !hasKoord
            && !hasGem
        ) {
            Staat staat =
                repository.getByIdPlain(
                    Staat.class, ort.getStaatId(), Strings.STAMM);
            ort.setKdaId(staat.getKdaId());
            ort.setKoordXExtern(staat.getKoordXExtern());
            ort.setKoordYExtern(staat.getKoordYExtern());
            ort.setLangtext(staat.getStaat());
            ort.setOrtTyp(ORTTYP5);
            ort.setOrtId("STAAT_" + staat.getId());
            if (staat.getStaatIso() != null) {
                ort.setKurztext("STAAT_" + staat.getStaatIso());
            } else {
                ort.setKurztext("STAAT_" + staat.getId());
            }
            ort.setBerichtstext(staat.getStaat());
            transformCoordinates(ort);
            hasStaat = true;
        }
        if (!hasKoord && !hasGem && !hasStaat) {
            ReportItem err = new ReportItem();
            err.setCode(ERR611);
            err.setKey("ort");
            err.setValue("");
            errors.add(err);
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
        Query q = repository.entityManager(Strings.STAMM)
            .createQuery("SELECT vg.gemId "
                + "FROM Verwaltungsgrenze vg "
                + "WHERE is_gemeinde = TRUE "
                + "AND contains(vg.shape, :geom) = TRUE");
        q.setParameter("geom", ort.getGeom());
        List<?> ret = q.getResultList();
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
        geom.setSRID(EPSG4326);
        return geom;
    }

    private String getEpsgForWgsUtm(String x) {
        String epsg = "EPSG:326";
        String part = x.split(",")[0];
        String zone = part.length() == LEN7 ? ("0" + part.substring(0, 1))
            : part.substring(0, 2);
        return epsg + zone;
    }

    private String getEpsgForEd50Utm(String x) {
        String epsg = "EPSG:230";
        String part = x.split(",")[0];
        String zone = part.length() == LEN7 ? ("0" + part.substring(0, 1)) :
            part.substring(0, 2);
        return epsg + zone;
    }

    private String getEpsgForGK(String koordXExtern) {
        String part = koordXExtern.split(",")[0];
        String zone = part.length() == LEN7 ? (part.substring(0, 1)) : null;
        if (zone == null) {
            return "";
        }
        try {
            Integer iZone = Integer.valueOf(zone);
            String epsg = "EPSG:3146";
            switch (iZone) {
                case ZONE2: return epsg + "6";
                case ZONE3: return epsg + "7";
                case ZONE4: return epsg + "8";
                case ZONE5: return epsg + "9";
                default: return "";
            }
        } catch (NumberFormatException e) {
            return "";
        }
    }

    public List<ReportItem> getErrors() {
        return errors;
    }

    /**
     * Check if the factory has any errors.
     * @return True if there are errors
     */
    public boolean hasErrors() {
        return !(errors == null) && !errors.isEmpty();
    }
}
