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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.persistence.Query;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import de.intevation.lada.importer.ReportItem;
import de.intevation.lada.model.stammdaten.Ort;
import de.intevation.lada.model.stammdaten.Staat;
import de.intevation.lada.model.stammdaten.Verwaltungseinheit;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;

public class OrtFactory {

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    private List<ReportItem> errors;

    public void transformCoordinates(Ort ort) {
        if (errors == null) {
            errors = new ArrayList<ReportItem>();
        }
        Integer kda = ort.getKdaId();
        String epsg = null;
        String xCoord = null;
        String yCoord = null;
        switch(kda) {
        case 1:
            epsg = getEpsgForGK(ort.getKoordXExtern());
            if ("".equals(epsg)) {
                ReportItem err = new ReportItem();
                err.setCode(670);
                err.setKey("coordinates");
                err.setValue(ort.getKdaId() + " " + ort.getKoordXExtern() + " " + ort.getKoordYExtern());
                errors.add(err);
                return;
            }
            xCoord = ort.getKoordYExtern();
            yCoord = ort.getKoordXExtern();
            jtsTransform(epsg, xCoord, yCoord, ort);
            break;
        case 2:
            degreeTransform(ort);
            break;
        case 4:
            epsg = "EPSG:4326";
            /* EPSG:4326 defines the order of latitude and longitude
             * the other way round than IMIS coordinates specification.
             */
            xCoord = ort.getKoordYExtern();
            yCoord = ort.getKoordXExtern();
            jtsTransform(epsg, xCoord, yCoord, ort);
            break;
        case 5:
            epsg = getEpsgForWgsUtm(ort.getKoordXExtern());
            xCoord = ort.getKoordXExtern().length() == 7 ? ort.getKoordXExtern().substring(1, 7)
                    : ort.getKoordXExtern().substring(2, 8);
            yCoord = ort.getKoordYExtern();
            jtsTransform(epsg, xCoord, yCoord, ort);
            break;
        default:
            ReportItem err = new ReportItem();
            err.setCode(612);
            err.setKey("kdaId");
            err.setValue(ort.getKdaId().toString());
            errors.add(err);
            return;
        }
    }

    private void jtsTransform(String epsg, String xCoord, String yCoord, Ort ort) {
        if (errors == null) {
            errors = new ArrayList<ReportItem>();
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
        } catch (NumberFormatException | FactoryException | TransformException e) {
            ReportItem err = new ReportItem();
            err.setCode(670);
            err.setKey("coordinates");
            err.setValue(ort.getKdaId() + " " + ort.getKoordXExtern() + " " + ort.getKoordYExtern());
            errors.add(err);
            return;
        }
    }

    private void degreeTransform(Ort ort) {
        if (errors == null) {
            errors = new ArrayList<ReportItem>();
        }
        String xCoord = ort.getKoordXExtern();
        String yCoord = ort.getKoordYExtern();
        int xDegree = 0;
        int xMin = 0;
        int yDegree = 0;
        int yMin = 0;
        double xSec = 0;
        double ySec = 0;
        String xPrefix = "";
        String xSuffix = "";
        String yPrefix = "";
        String ySuffix = "";
        try {
            if (xCoord.contains(",")) {
                // with decimal separator
                Pattern p = Pattern.compile("([+|-|W|E]?)(\\d{1,3})(\\d{2})(\\d{2}),(\\d{1,5})([W|E]?)");
                Matcher m = p.matcher(xCoord);
                m.matches();
                xPrefix = m.group(1);
                xDegree = Integer.valueOf(m.group(2));
                xMin = Integer.valueOf(m.group(3));
                xSec = Double.valueOf(m.group(4) + "." + m.group(5));
                xSuffix = m.group(6);
            }
            else {
                Pattern p = Pattern.compile("([+|-|W|E]?)(\\d{3})(\\d{0,2})(\\d{0,2})([W|E]?)");
                Matcher m = p.matcher(xCoord);
                m.matches();
                xPrefix = m.group(1);
                xDegree = Integer.valueOf(m.group(2));
                xMin = Integer.valueOf(m.group(3));
                xSec = Double.valueOf(m.group(4));
                xSuffix = m.group(5);
            }
            if(yCoord.contains(",")) {
                Pattern p = Pattern.compile("([+|-|N|S]?)(\\d{1,2})(\\d{2})(\\d{2}),(\\d{1,5})([N|S]?)");
                Matcher m = p.matcher(yCoord);
                m.matches();
                yPrefix = m.group(1);
                yDegree = Integer.valueOf(m.group(2));
                yMin = Integer.valueOf(m.group(3));
                ySec = Double.valueOf(m.group(4) + "." + m.group(5));
                ySuffix = m.group(6);
            }
            else {
                Pattern p = Pattern.compile("([+|-|N|S]?)(\\d{2})(\\d{0,2})(\\d{0,2})([N|S]?)");
                Matcher m = p.matcher(yCoord);
                m.matches();
                yPrefix = m.group(1);
                yDegree = Integer.valueOf(m.group(2));
                yMin = Integer.valueOf(m.group(3));
                ySec = Double.valueOf(m.group(4));
                ySuffix = m.group(5);
            }
        }
        catch(IllegalStateException e) {
            ReportItem err = new ReportItem();
            err.setCode(652);
            err.setKey("koord_extern");
            err.setValue(ort.getKdaId().toString() +
                " \"" + ort.getKoordXExtern() + "\" \"" +
                ort.getKoordYExtern() + "\"");
            errors.add(err);
            return;
        }

        double ddX = xDegree + ((xMin/60d) + (xSec/3600d));
        double ddY = yDegree + ((yMin/60d) + (ySec/3600d));

        if ((xPrefix != null && (xPrefix.equals("-") || xPrefix.equals("W"))) ||
            (xSuffix != null && xSuffix.equals("W"))) {
            ddX = ddX * -1;
        }
        if ((yPrefix != null && (yPrefix.equals("-") || yPrefix.equals("S"))) ||
            (ySuffix != null && (ySuffix.equals("S")))) {
            ddY = ddY * -1;
        }
        ort.setGeom(generateGeom(ddX, ddY));
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
        if (errors == null) {
            errors = new ArrayList<ReportItem>();
        }
        QueryBuilder<Ort> builder =
            new QueryBuilder<Ort>(
                repository.entityManager(Strings.STAMM),
                Ort.class);
        if (ort.getKdaId() != null &&
            ort.getKoordXExtern() != null &&
            ort.getKoordYExtern() != null
        ) {
            builder.and("kdaId", ort.getKdaId());
            builder.and("koordXExtern", ort.getKoordXExtern());
            builder.and("koordYExtern", ort.getKoordYExtern());
            builder.and("ozId", ort.getOzId());
            builder.and("netzbetreiberId", ort.getNetzbetreiberId());
            List<Ort> orte = repository.filterPlain(builder.getQuery(), Strings.STAMM);
            if (orte != null && orte.size() > 0) {
                return orte.get(0);
            }
        }
        else if (ort.getGemId() != null) {
            builder.and("gemId", ort.getGemId());
            builder.and("ozId", ort.getOzId());
            builder.and("netzbetreiberId", ort.getNetzbetreiberId());
            List<Ort> orte = repository.filterPlain(builder.getQuery(), Strings.STAMM);
            if (orte != null && orte.size() > 0) {
                return orte.get(0);
            }
        }
        else  if (ort.getStaatId() != null) {
            builder.and("staatId", ort.getStaatId());
            builder.and("ortTyp", 5);
            builder.and("ozId", ort.getOzId());
            builder.and("netzbetreiberId", ort.getNetzbetreiberId());
            List<Ort> orte = repository.filterPlain(builder.getQuery(), Strings.STAMM);
            if (orte != null && orte.size() > 0) {
                return orte.get(0);
            }
        }

        return createOrt(ort);
    }

    private Ort createOrt(Ort ort) {
        if (errors == null) {
            errors = new ArrayList<ReportItem>();
        }
        boolean hasKoord = false;
        boolean hasGem = false;
        boolean hasStaat = false;
        if (ort.getKdaId() != null &&
            ort.getKoordXExtern() != null &&
            ort.getKoordYExtern() != null
        ) {
            transformCoordinates(ort);
            hasKoord = true;
        }
        if (ort.getGemId() == null && hasKoord) {
            findVerwaltungseinheit(ort);
        }
        if (ort.getGemId() != null){
            if (ort.getStaatId() == null) {
                ort.setStaatId(0);
            }
            Verwaltungseinheit v = repository.getByIdPlain(
                Verwaltungseinheit.class,
                ort.getGemId(),
                Strings.STAMM);
            if (v == null) {
                ReportItem err = new ReportItem();
                err.setCode(675);
                err.setKey("gem_id");
                err.setValue(ort.getGemId());
                errors.add(err);
                return null;
            }
            else {
                if (!hasKoord) {
                    ort.setKdaId(4);
                    ort.setKoordYExtern(String.valueOf(v.getMittelpunkt().getY()));
                    ort.setKoordXExtern(String.valueOf(v.getMittelpunkt().getX()));
                }
                if (ort.getLangtext() == null || ort.getLangtext().equals("")) {
                    ort.setLangtext(v.getBezeichnung());
                }
                if (ort.getBerichtstext() == null || ort.getBerichtstext().equals("")) {
                    ort.setBerichtstext(v.getBezeichnung());
                }
                transformCoordinates(ort);
                hasGem = true;
            }
        }
        if (ort.getStaatId() != null &&
            !hasKoord &&
            !hasGem
        ) {
            Staat staat =
                repository.getByIdPlain(Staat.class, ort.getStaatId(), Strings.STAMM);
            ort.setKdaId(staat.getKdaId());
            ort.setKoordXExtern(staat.getKoordXExtern());
            ort.setKoordYExtern(staat.getKoordYExtern());
            ort.setKurztext(staat.getStaat());
            ort.setLangtext(staat.getStaat());
            ort.setOrtTyp(5);
            if (staat.getStaatIso() != null) {
                ort.setOrtId("Staat_" + staat.getStaatIso());
            }
            ort.setBerichtstext(staat.getStaat());
            transformCoordinates(ort);
            hasStaat = true;
        }
        if (!hasKoord && !hasGem && !hasStaat) {
            ReportItem err = new ReportItem();
            err.setCode(611);
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
            .createQuery("SELECT vg.gemId " +
                         "FROM Verwaltungsgrenze vg " +
                         "WHERE is_gemeinde = TRUE AND contains(vg.shape, :geom) = TRUE");
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

    private String getEpsgForGK(String koordXExtern) {
        String part = koordXExtern.split(",")[0];
        String zone = part.length() == 7 ? (part.substring(0, 1)) : null;
        if (zone == null) {
            return "";
        }
        try {
            Integer iZone = Integer.valueOf(zone);
            String epsg = "EPSG:3146";
            switch(iZone) {
                case 2: return epsg + "6";
                case 3: return epsg + "7";
                case 4: return epsg + "8";
                case 5: return epsg + "9";
                default: return "";
            }
        }
        catch (NumberFormatException e) {
            return "";
        }
    }

    public List<ReportItem> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !(errors == null) && !errors.isEmpty();
    }
}
