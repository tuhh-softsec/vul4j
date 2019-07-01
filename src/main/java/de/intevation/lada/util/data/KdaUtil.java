/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.util.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vividsolutions.jts.geom.Coordinate;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Utilities for kda transformations
 *
 */
public class KdaUtil {
    ObjectMapper builder;
    public ObjectNode transform(int kdaFrom, int kdaTo, String x, String y) {
        builder = new ObjectMapper();
        Transform t;
        switch (kdaFrom) {
            case 1: t = this.new Transform_1(); break;
            case 2: t = this.new Transform_2(); break;
            case 4: t = this.new Transform_4(); break;
            case 5: t = this.new Transform_5(); break;
            case 8: t = this.new Transform_8(); break;
            default: return null;
        }
        return t.transform(kdaTo, x, y);
    }

    private interface Transform {
        public ObjectNode transform(int to, String x, String y);
        public ObjectNode transformTo1(String x, String y);
        public ObjectNode transformTo2(String x, String y);
        public ObjectNode transformTo4(String x, String y);
        public ObjectNode transformTo5(String x, String y);
        public ObjectNode transformTo8(String x, String y);
    }

    private abstract class AbstractTransform implements Transform {
        public ObjectNode transform(int to, String x, String y) {
            switch (to) {
                case 1: return transformTo1(x, y);
                case 2: return transformTo2(x, y);
                case 4: return transformTo4(x, y);
                case 5: return transformTo5(x, y);
                case 8: return transformTo8(x, y);
            }
            return null;
        }
    }

    private class Transform_1 extends AbstractTransform {
        @Override
        public ObjectNode transformTo1(String x, String y) {
            ObjectNode response = builder.createObjectNode();
            response.put("x", x);
            response.put("y", y);
            return response;
        }

        @Override
        public ObjectNode transformTo2(String x, String y) {
            String epsg = getEpsgForGK(x);
            ObjectNode degrees = jtsTransform(epsg, "EPSG:4326", y, x);
            if (degrees == null) {
                return null;
            }
            return degreeToArc(
                degrees.get("y").asText(),
                degrees.get("x").asText());
        }

        @Override
        public ObjectNode transformTo4(String x, String y) {
            String epsg = getEpsgForGK(x);
            ObjectNode coords = jtsTransform(epsg, "EPSG:4326", y, x);
            if (coords == null) {
                return null;
            }
            String coordX = coords.get("x").asText();
            String coordY = coords.get("y").asText();
            int maxLenX = coordX.length() - coordX.indexOf(".");
            int precX = maxLenX < 7 ? maxLenX : 7;
            int maxLenY = coordY.length() - coordY.indexOf(".");
            int precY = maxLenY < 7 ? maxLenY : 7;
            coordX = coordX.substring(0, coordX.indexOf(".") + precX);
            coordY = coordY.substring(0, coordY.indexOf(".") + precY);
            coords.put("x", coordY);
            coords.put("y", coordX);
            return coords;
        }

        @Override
        public ObjectNode transformTo5(String x, String y) {
            String epsgGK = getEpsgForGK(x);
            ObjectNode degrees = jtsTransform(epsgGK, "EPSG:4326", y, x);
            String epsgWGS = getWgsUtmEpsg(
                degrees.get("y").asDouble(),
                degrees.get("x").asDouble());
            ObjectNode coord = jtsTransform(epsgGK,
                epsgWGS,
                y,
                x);
            if (coord == null) {
                return null;
            }
            coord.put("x", epsgWGS.substring(epsgWGS.length()-2, epsgWGS.length())+coord.get("x").asText());
            String coordX = coord.get("x").asText();
            String coordY = coord.get("y").asText();
            int maxLenX = coordX.length() - coordX.indexOf(".");
            int precX = maxLenX < 3 ? maxLenX : 3;
            int maxLenY = coordY.length() - coordY.indexOf(".");
            int precY = maxLenY < 3 ? maxLenY : 3;
            coordX = coordX.substring(0, coordX.indexOf(".") + precX);
            coordY = coordY.substring(0, coordY.indexOf(".") + precY);
            coord.put("x", coordX);
            coord.put("y", coordY);
            return coord;
        }

        @Override
        public ObjectNode transformTo8(String x, String y) {
            String epsgGK = getEpsgForGK(x);
            ObjectNode degrees = jtsTransform(epsgGK, "EPSG:4326", y, x);
            String epsgEd50 = getEpsgForEd50UtmFromDegree(
                degrees.get("y").asText());
            ObjectNode coord = jtsTransform(epsgGK, epsgEd50, y, x);
            if (coord == null) {
                return null;
            }
            String coordX = coord.get("x").asText();
            String coordY = coord.get("y").asText();
            int maxLenX = coordX.length() - coordX.indexOf(".");
            int precX = maxLenX < 3 ? maxLenX : 3;
            int maxLenY = coordY.length() - coordY.indexOf(".");
            int precY = maxLenY < 3 ? maxLenY : 3;
            coordX = coordX.substring(0, coordX.indexOf(".") + precX);
            coordY = coordY.substring(0, coordY.indexOf(".") + precY);
            String zone = epsgEd50.substring(epsgEd50.length() -2, epsgEd50.length());
            coord.put("x", zone+coordX);
            coord.put("y", coordY);
            return coord;
        }
    }

    private class Transform_2 extends AbstractTransform {
        @Override
        public ObjectNode transformTo1(String x, String y) {
            ObjectNode degrees = arcToDegree(x, y);
            String epsgGk = getGkEpsg(
                degrees.get("x").asDouble(),
                degrees.get("y").asDouble());

            ObjectNode coord = jtsTransform(
                "EPSG:4326",
                epsgGk,
                degrees.get("y").asText(),
                degrees.get("x").asText());
            if (coord == null) {
                return null;
            }
            String coordX = coord.get("x").asText();
            String coordY = coord.get("y").asText();
            int maxLenX = coordX.length() - coordX.indexOf(".");
            int precX = maxLenX < 2 ? maxLenX : 2;
            int maxLenY = coordY.length() - coordY.indexOf(".");
            int precY = maxLenY < 2 ? maxLenY : 2;
            coordX = coordX.substring(0, coordX.indexOf(".") + precX);
            coordY = coordY.substring(0, coordY.indexOf(".") + precY);
            coord.put("x", coordY);
            coord.put("y", coordX);
            return coord;
        }

        @Override
        public ObjectNode transformTo2(String x, String y) {
            ObjectNode response = builder.createObjectNode();
            response.put("x", x);
            response.put("y", y);
            return response;
        }

        @Override
        public ObjectNode transformTo4(String x, String y) {
            return arcToDegree(x, y);
        }

        @Override
        public ObjectNode transformTo5(String x, String y) {
            ObjectNode degrees = arcToDegree(x, y);
            String epsgWgs = getWgsUtmEpsg(
                degrees.get("x").asDouble(),
                degrees.get("y").asDouble());
            ObjectNode coord = jtsTransform("EPSG:4326",
                epsgWgs,
                degrees.get("y").asText(),
                degrees.get("x").asText());
            if (coord == null) {
                return null;
            }
            coord.put("x", epsgWgs.substring(epsgWgs.length()-2, epsgWgs.length())+coord.get("x").asText());
            String coordX = coord.get("x").asText();
            String coordY = coord.get("y").asText();
            int maxLenX = coordX.length() - coordX.indexOf(".");
            int precX = maxLenX < 3 ? maxLenX : 3;
            int maxLenY = coordY.length() - coordY.indexOf(".");
            int precY = maxLenY < 3 ? maxLenY : 3;
            coordX = coordX.substring(0, coordX.indexOf(".") + precX);
            coordY = coordY.substring(0, coordY.indexOf(".") + precY);
            coord.put("x", coordX);
            coord.put("y", coordY);
            return coord;
        }

        @Override
        public ObjectNode transformTo8(String x, String y) {
            ObjectNode degrees = arcToDegree(x, y);
            String epsgEd50 = getEpsgForEd50UtmFromDegree(
                degrees.get("x").asText());
            ObjectNode coord = jtsTransform("EPSG:4326",
                epsgEd50,
                degrees.get("y").asText(),
                degrees.get("x").asText());
            if (coord == null) {
                return null;
            }
            String coordX = coord.get("x").asText();
            String coordY = coord.get("y").asText();
            int maxLenX = coordX.length() - coordX.indexOf(".");
            int precX = maxLenX < 3 ? maxLenX : 3;
            int maxLenY = coordY.length() - coordY.indexOf(".");
            int precY = maxLenY < 3 ? maxLenY : 3;
            coordX = coordX.substring(0, coordX.indexOf(".") + precX);
            coordY = coordY.substring(0, coordY.indexOf(".") + precY);
            String zone = epsgEd50.substring(epsgEd50.length() -2, epsgEd50.length());
            coord.put("x", zone+coordX);
            coord.put("y", coordY);
            return coord;
        }
    }

    private class Transform_4 extends AbstractTransform {

        @Override
        public ObjectNode transformTo1(String x, String y) {
            x = x.replaceAll(",", ".");
            y = y.replaceAll(",", ".");
            String epsgGk = getGkEpsg(Double.valueOf(x), Double.valueOf(y));
            ObjectNode coord = jtsTransform("EPSG:4326", epsgGk, y, x);
            if (coord == null) {
                return null;
            }
            String coordX = coord.get("x").asText();
            String coordY = coord.get("y").asText();
            int maxLenX = coordX.length() - coordX.indexOf(".");
            int precX = maxLenX < 3 ? maxLenX : 3;
            int maxLenY = coordY.length() - coordY.indexOf(".");
            int precY = maxLenY < 3 ? maxLenY : 3;
            coordX = coordX.substring(0, coordX.indexOf(".") + precX);
            coordY = coordY.substring(0, coordY.indexOf(".") + precY);
            coord.put("x", coordY);
            coord.put("y", coordX);
            return coord;
        }

        @Override
        public ObjectNode transformTo2(String x, String y) {
            return degreeToArc(x, y);
        }

        @Override
        public ObjectNode transformTo4(String x, String y) {
            ObjectNode response = builder.createObjectNode();
            response.put("x", x);
            response.put("y", y);
            return response;
        }

        @Override
        public ObjectNode transformTo5(String x, String y) {
            x = x.replaceAll(",", ".");
            y = y.replaceAll(",", ".");
            String epsgWgs = getWgsUtmEpsg(Double.valueOf(x), Double.valueOf(y));
            ObjectNode coord = jtsTransform("EPSG:4326", epsgWgs, y, x);
            if (coord == null) {
                return null;
            }
            coord.put("x", epsgWgs.substring(epsgWgs.length()-2, epsgWgs.length())+coord.get("x").asText());
            String coordX = coord.get("x").asText();
            String coordY = coord.get("y").asText();
            int maxLenX = coordX.length() - coordX.indexOf(".");
            int precX = maxLenX < 3 ? maxLenX : 3;
            int maxLenY = coordY.length() - coordY.indexOf(".");
            int precY = maxLenY < 3 ? maxLenY : 3;
            coordX = coordX.substring(0, coordX.indexOf(".") + precX);
            coordY = coordY.substring(0, coordY.indexOf(".") + precY);
            coord.put("x", coordX);
            coord.put("y", coordY);
            return coord;
        }

        @Override
        public ObjectNode transformTo8(String x, String y) {
            String epsgEd50 = getEpsgForEd50UtmFromDegree(x);
            ObjectNode coord = jtsTransform("EPSG:4326", epsgEd50, y, x);
            if (coord == null) {
                return null;
            }
            String coordX = coord.get("x").asText();
            String coordY = coord.get("y").asText();
            int maxLenX = coordX.length() - coordX.indexOf(".");
            int precX = maxLenX < 3 ? maxLenX : 3;
            int maxLenY = coordY.length() - coordY.indexOf(".");
            int precY = maxLenY < 3 ? maxLenY : 3;
            coordX = coordX.substring(0, coordX.indexOf(".") + precX);
            coordY = coordY.substring(0, coordY.indexOf(".") + precY);
            String zone = epsgEd50.substring(epsgEd50.length() -2, epsgEd50.length());
            coord.put("x", zone+coordX);
            coord.put("y", coordY);
            return coord;
        }
    }

    private class Transform_5 extends AbstractTransform {

        @Override
        public ObjectNode transformTo1(String x, String y) {
            String epsgWgs = getEpsgForWgsUtm(x);
            x = x.substring(2, x.length());
            ObjectNode degrees = jtsTransform(epsgWgs, "EPSG:4326", x, y);
            if (degrees == null) {
                return null;
            }
            String epsgGk = getGkEpsg(
                degrees.get("y").asDouble(),
                degrees.get("x").asDouble());
            ObjectNode coords = jtsTransform(epsgWgs, epsgGk, x, y);
            if (coords == null) {
                return null;
            }
            String coordX = coords.get("x").asText();
            String coordY = coords.get("y").asText();
            int maxLenX = coordX.length() - coordX.indexOf(".");
            int precX = maxLenX < 2 ? maxLenX : 2;
            int maxLenY = coordY.length() - coordY.indexOf(".");
            int precY = maxLenY < 2 ? maxLenY : 2;
            coordX = coordX.substring(0, coordX.indexOf(".") + precX);
            coordY = coordY.substring(0, coordY.indexOf(".") + precY);
            coords.put("x", coordY);
            coords.put("y", coordX);
            return coords;
        }

        @Override
        public ObjectNode transformTo2(String x, String y) {
            String epsgWgs = getEpsgForWgsUtm(x);
            x = x.substring(2, x.length());
            ObjectNode degrees = jtsTransform(epsgWgs, "EPSG:4326", x, y);
            ObjectNode coords = degreeToArc(
                degrees.get("y").asText(),
                degrees.get("x").asText());
            return coords;
        }

        @Override
        public ObjectNode transformTo4(String x, String y) {
            String epsgWgs = getEpsgForWgsUtm(x);
            x = x.substring(2, x.length());
            ObjectNode coords = jtsTransform(epsgWgs, "EPSG:4326", x, y);
            if (coords == null) {
                return null;
            }
            String coordX = coords.get("x").asText();
            String coordY = coords.get("y").asText();
            int maxLenX = coordX.length() - coordX.indexOf(".");
            int precX = maxLenX < 7 ? maxLenX : 7;
            int maxLenY = coordY.length() - coordY.indexOf(".");
            int precY = maxLenY < 7 ? maxLenY : 7;
            coordX = coordX.substring(0, coordX.indexOf(".") + precX);
            coordY = coordY.substring(0, coordY.indexOf(".") + precY);
            coords.put("x", coordY);
            coords.put("y", coordX);
            return coords;
        }

        @Override
        public ObjectNode transformTo5(String x, String y) {
            ObjectNode response = builder.createObjectNode();
            response.put("x", x);
            response.put("y", y);
            return response;
        }

        @Override
        public ObjectNode transformTo8(String x, String y) {
            String epsgWgs = getEpsgForWgsUtm(x);
            x = x.substring(2, x.length());
            ObjectNode coords4326 = jtsTransform(epsgWgs, "EPSG:4326", x, y);
            if (coords4326 == null) {
                return null;
            }
            String epsgEd50 = getEpsgForEd50UtmFromDegree(coords4326.get("y").asText());
            ObjectNode coords = jtsTransform(epsgWgs, epsgEd50, x, y);
            if (coords == null) {
                return null;
            }
            String coordX = coords.get("x").asText();
            String coordY = coords.get("y").asText();
            int maxLenX = coordX.length() - coordX.indexOf(".");
            int precX = maxLenX < 7 ? maxLenX : 7;
            int maxLenY = coordY.length() - coordY.indexOf(".");
            int precY = maxLenY < 7 ? maxLenY : 7;
            coordX = coordX.substring(0, coordX.indexOf(".") + precX);
            coordY = coordY.substring(0, coordY.indexOf(".") + precY);
            String zone = epsgEd50.substring(epsgEd50.length() -2, epsgEd50.length());
            coords.put("x", zone+coordX);
            coords.put("y", coordY);
            return coords;
        }
    }
    private class Transform_8 extends AbstractTransform {

        @Override
        public ObjectNode transformTo1(String x, String y) {
            String epsgEd50 = getEpsgForEd50Utm(x);
            x = x.substring(2, x.length());
            ObjectNode degrees = jtsTransform(epsgEd50, "EPSG:4326", x, y);
            if (degrees == null) {
                return null;
            }
            String epsgGk = getGkEpsg(
                degrees.get("y").asDouble(),
                degrees.get("x").asDouble());
            ObjectNode coords = jtsTransform(epsgEd50, epsgGk, x, y);
            if (coords == null) {
                return null;
            }
            String coordX = coords.get("x").asText();
            String coordY = coords.get("y").asText();
            int maxLenX = coordX.length() - coordX.indexOf(".");
            int precX = maxLenX < 2 ? maxLenX : 2;
            int maxLenY = coordY.length() - coordY.indexOf(".");
            int precY = maxLenY < 2 ? maxLenY : 2;
            coordX = coordX.substring(0, coordX.indexOf(".") + precX);
            coordY = coordY.substring(0, coordY.indexOf(".") + precY);
            coords.put("x", coordY);
            coords.put("y", coordX);
            return coords;
        }

        @Override
        public ObjectNode transformTo2(String x, String y) {
            String epsgWgs = getEpsgForEd50Utm(x);
            x = x.substring(2, x.length());
            ObjectNode degrees = jtsTransform(epsgWgs, "EPSG:4326", x, y);
            ObjectNode coords = degreeToArc(
                degrees.get("y").asText(),
                degrees.get("x").asText());
            return coords;
        }

        @Override
        public ObjectNode transformTo4(String x, String y) {
            String epsgEd50 = getEpsgForEd50Utm(x);
            x = x.substring(2, x.length());
            ObjectNode coords = jtsTransform(epsgEd50, "EPSG:4326", x, y);
            if (coords == null) {
                return null;
            }
            String coordX = coords.get("x").asText();
            String coordY = coords.get("y").asText();
            int maxLenX = coordX.length() - coordX.indexOf(".");
            int precX = maxLenX < 7 ? maxLenX : 7;
            int maxLenY = coordY.length() - coordY.indexOf(".");
            int precY = maxLenY < 7 ? maxLenY : 7;
            coordX = coordX.substring(0, coordX.indexOf(".") + precX);
            coordY = coordY.substring(0, coordY.indexOf(".") + precY);
            coords.put("x", coordY);
            coords.put("y", coordX);
            return coords;
        }

        @Override
        public ObjectNode transformTo5(String x, String y) {
            String epsgEd50 = getEpsgForEd50Utm(x);
            if (epsgEd50.equals("")) {
                return null;
            }
            String x1 = x.substring(2, x.length());
            ObjectNode coords4326 = jtsTransform(epsgEd50, "EPSG:4326", x1, y);
            String epsgWgs = getEpsgForWgsUtmFromDegree(coords4326.get("y").asText());
            ObjectNode coords = jtsTransform(epsgEd50, epsgWgs, y, x1);
            if (coords == null) {
                return null;
            }
            String coordX = coords.get("x").asText();
            String coordY = coords.get("y").asText();
            int maxLenX = coordX.length() - coordX.indexOf(".");
            int precX = maxLenX < 7 ? maxLenX : 7;
            int maxLenY = coordY.length() - coordY.indexOf(".");
            int precY = maxLenY < 7 ? maxLenY : 7;
            coordX = coordX.substring(0, coordX.indexOf(".") + precX);
            coordY = coordY.substring(0, coordY.indexOf(".") + precY);
            String zone = epsgWgs.substring(epsgWgs.length() -2, epsgWgs.length());
            coords.put("x", zone+coordY);
            coords.put("y", coordX);
            return coords;
        }

        @Override
        public ObjectNode transformTo8(String x, String y) {
            ObjectNode response = builder.createObjectNode();
            response.put("x", x);
            response.put("y", y);
            return response;
        }

    }

    private ObjectNode jtsTransform(String epsgFrom, String epsgTo, String x, String y) {
        try {
            CoordinateReferenceSystem src = CRS.decode(epsgFrom);
            CoordinateReferenceSystem target = CRS.decode(epsgTo);

            MathTransform transform = CRS.findMathTransform(src, target);
            Coordinate srcCoord = new Coordinate();
            srcCoord.y = Double.valueOf(y.replace(",", "."));
            srcCoord.x = Double.valueOf(x.replace(",", "."));
            Coordinate targetCoord = new Coordinate();
            JTS.transform(srcCoord, targetCoord, transform);
            ObjectNode response = builder.createObjectNode();
            response.put("x", String.valueOf(targetCoord.x));
            response.put("y", String.valueOf(targetCoord.y));
            return response;

        } catch (FactoryException | TransformException e) {
            return null;
        }
    }
    private ObjectNode degreeToArc(String x, String y) {
        x = x.replaceAll(",", ".");
        y = y.replaceAll(",", ".");
        String[] xParts = x.split("\\.");
        String[] yParts = y.split("\\.");
        double factorX = 3600;
        double factorY = 3600;
        double wsX = Double.parseDouble("0."+xParts[1])*factorX;
        double wsY = Double.parseDouble("0."+yParts[1])*factorY;
        String xRes = xParts[0] +
            String.format("%02d", (int)Math.floor(wsX/60)) +
            String.format("%02.5f", wsX%60);
        String yRes = yParts[0] +
            String.format("%02d", (int)Math.floor(wsY/60)) +
            String.format("%02.5f", wsY%60);
        xRes = xRes.replaceAll("\\.", ",");
        yRes = yRes.replaceAll("\\.", ",");
        if (xParts[0].startsWith("-")) {
            xRes = xRes.replace("-", "W");
        }
        else {
            xRes = "E" + xRes;
        }
        if (yParts[0].startsWith("-")) {
            yRes = yRes.replace("-", "S");
        }
        else {
            yRes = "N" + yRes;
        }
        ObjectNode response = builder.createObjectNode();
        response.put("x", xRes.toString());
        response.put("y", yRes.toString());
        return response;
    }

    private ObjectNode arcToDegree(String x, String y) {
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
            if (x.contains(",")) {
                // with decimal separator
                Pattern p = Pattern.compile("([+|-|W|E]?)(\\d{1,3})(\\d{2})(\\d{2}),(\\d{1,5})([W|E]?)");
                Matcher m = p.matcher(x);
                m.matches();
                xPrefix = m.group(1);
                xDegree = Integer.valueOf(m.group(2));
                xMin = Integer.valueOf(m.group(3));
                xSec = Double.valueOf(m.group(4) + "." + m.group(5));
                xSuffix = m.group(6);
            }
            else {
                Pattern p = Pattern.compile("([+|-|W|E]?)(\\d{3})(\\d{0,2})(\\d{0,2})([W|E]?)");
                Matcher m = p.matcher(x);
                m.matches();
                xPrefix = m.group(1);
                xDegree = Integer.valueOf(m.group(2));
                xMin = Integer.valueOf(m.group(3));
                xSec = Double.valueOf(m.group(4));
                xSuffix = m.group(5);
            }
            if(y.contains(",")) {
                Pattern p = Pattern.compile("([+|-|N|S]?)(\\d{1,2})(\\d{2})(\\d{2}),(\\d{1,5})([N|S]?)");
                Matcher m = p.matcher(y);
                m.matches();
                yPrefix = m.group(1);
                yDegree = Integer.valueOf(m.group(2));
                yMin = Integer.valueOf(m.group(3));
                ySec = Double.valueOf(m.group(4) + "." + m.group(5));
                ySuffix = m.group(6);
            }
            else {
                Pattern p = Pattern.compile("([+|-|N|S]?)(\\d{2})(\\d{0,2})(\\d{0,2})([N|S]?)");
                Matcher m = p.matcher(y);
                m.matches();
                yPrefix = m.group(1);
                yDegree = Integer.valueOf(m.group(2));
                yMin = Integer.valueOf(m.group(3));
                ySec = Double.valueOf(m.group(4));
                ySuffix = m.group(5);
            }
        }
        catch(IllegalStateException e) {
            return null;
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
        ObjectNode response = builder.createObjectNode();
        response.put("x", String.valueOf(ddX));
        response.put("y", String.valueOf(ddY));
        return response;
    }

    private String getWgsUtmEpsg(double x, double y) {
        int pref;
        if (y > 0) {
            pref = 32600;
        }
        else {
            pref = 32700;
        }
        int zone = (int)Math.floor((x+180)/6)+1;
        zone += pref;
        return "EPSG:" + zone;
    }

    private String getGkEpsg(double x, double y) {
        int code = 31460;
        int ref = (int)Math.round(x/3);
        switch(ref) {
            case 2: code+=6;break;
            case 3: code+=7;break;
            case 4: code+=8;break;
            case 5: code+=9;break;
        }
        return "EPSG:" + code;
    }

    private String getEpsgForWgsUtm(String x) {
        String epsg = "EPSG:326";
        x = x.replaceAll(",", ".");
        String part = x.split("\\.")[0];
        String zone = part.length() == 7 ? ("0" + part.substring(0, 1)) :
            part.substring(0, 2);
        return epsg + zone;
    }

    private String getEpsgForWgsUtmFromDegree(String x) {
        x = x.replaceAll(",", ".");
        Double xCoord;
        try {
            xCoord = Double.valueOf(x);
        }
        catch(NumberFormatException nfe) {
            return "";
        }
        String zone;
        if (xCoord < -12 && xCoord > -18) zone = "28";
        else if (xCoord < -6 && xCoord > -12) zone = "29";
        else if (xCoord < 0 && xCoord > -6) zone = "30";
        else if (xCoord < 6 && xCoord > 0) zone = "31";
        else if (xCoord < 12 && xCoord > 6) zone = "32";
        else if (xCoord < 18 && xCoord > 12) zone = "33";
        else if (xCoord < 24 && xCoord > 18) zone = "34";
        else return "";
        return "EPSG:326" + zone;
    }

    private String getEpsgForGK(String y) {
        y = y.replaceAll(",", ".");
        String part = y.split("\\.")[0];
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

    private String getEpsgForEd50Utm(String x) {
        String epsg = "EPSG:230";
        String part = x.split(",")[0];
        String zone = part.length() == 7 ? ("0" + part.substring(0, 1)) :
            part.substring(0, 2);
        return epsg + zone;
    }

    private String getEpsgForEd50UtmFromDegree(String x) {
        x = x.replaceAll(",", ".");
        Double xCoord;
        try {
            xCoord = Double.valueOf(x);
        }
        catch(NumberFormatException nfe) {
            return "";
        }
        String zone;
        if (xCoord <= -12 && xCoord > -18) zone = "28";
        else if (xCoord <= -6 && xCoord > -12) zone = "29";
        else if (xCoord <= 0 && xCoord > -6) zone = "30";
        else if (xCoord <= 6 && xCoord > 0) zone = "31";
        else if (xCoord <= 12 && xCoord > 6) zone = "32";
        else if (xCoord <= 18 && xCoord > 12) zone = "33";
        else if (xCoord <= 24 && xCoord > 18) zone = "34";
        else return "";

        return "EPSG:230" + zone;
    }
}