/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.test;

import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.WordUtils;
import org.junit.Assert;

import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import de.intevation.lada.BaseTest;
import de.intevation.lada.Protocol;
import de.intevation.lada.test.land.ProbeTest;

/**
 * Class for Lada service tests.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class ServiceTest {

    private static final String LAT_KEY = "latitude";
    private static final String LONG_KEY = "longitude";

    /**
     * Test protocol for output of results.
     */
    protected List<Protocol> protocol;

    /**
     * Timestamp attributes.
     */
    protected List<String> timestampAttributes = new ArrayList<String>();

    /**
     * Geometry attributes.
     */
    protected List<String> geomPointAttributes = new ArrayList<String>();

    /**
     * Base url of the server.
     */
    protected URL baseUrl;

    /**
     * Initialize the tests.
     * @param bUrl The server url used for the request.
     * @param p The resulting test protocol
     */
    public void init(URL bUrl, List<Protocol> p) {
        this.baseUrl = bUrl;
        this.protocol = p;
    }

    /**
     * @return The test protocol
     */
    public List<Protocol> getProtocol() {
        return protocol;
    }

    /**
     * Load JSON resource file.
     * @param resource the resource location
     * @return Object containing the resource.
     */
    protected JsonObject readJsonResource(String resource) {
        InputStream stream =
            ProbeTest.class.getResourceAsStream(resource);
        Scanner scanner = new Scanner(stream, "UTF-8");
        scanner.useDelimiter("\\A");
        String raw = scanner.next();
        scanner.close();
        JsonReader reader = Json.createReader(new StringReader(raw));
        JsonObject content = reader.readObject();
        reader.close();
        return content;
    }

    /**
     * Convert geometries and timestamps.
     * @param object The current version.
     * @return Builder with the new version.
     */
    protected JsonObjectBuilder convertObject(JsonObject object) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        for (Entry<String, JsonValue> entry : object.entrySet()) {
            String key = WordUtils.capitalize(
                entry.getKey(), new char[]{'_'}).replaceAll("_", "");
            key = key.replaceFirst(
                key.substring(0, 1), key.substring(0, 1).toLowerCase());
            if (timestampAttributes.contains(key)) {
                Timestamp timestamp = Timestamp.valueOf(
                    entry.getValue().toString().replaceAll("\"", ""));
                builder.add(key, timestamp.getTime());
            } else if (geomPointAttributes.contains(key)) {
                // Convert EWKT to latitude and longitude
                String wkt = entry.getValue().toString().split(";")[1];
                try {
                    Geometry geom = new WKTReader().read(wkt);
                    if (!(geom instanceof Point)) {
                        throw new IllegalArgumentException(
                            "WKT does not represent a point");
                    }
                    Point point = (Point) geom;
                    builder.add(LONG_KEY, point.getX());
                    builder.add(LAT_KEY, point.getY());
                } catch (ParseException | IllegalArgumentException e) {
                    Protocol prot = new Protocol();
                    prot.addInfo("exception", e.getMessage());
                    protocol.add(prot);
                    Assert.fail("Exception while parsing WKT '"
                        + wkt + "':\n"
                        + e.getMessage());
                }
            } else {
                builder.add(key, entry.getValue());
            }
        }
        return builder;
    }

    /**
     * Base for all the get all requests.
     * @param name of the entity to request
     * @param parameter the url parameter used in the request.
     * @return the json object returned by the serive.
     */
    public JsonObject getAll(String name, String parameter) {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName(name + " service");
        prot.setType("get all");
        prot.setPassed(false);
        protocol.add(prot);
        /* Create a client*/
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(baseUrl + parameter);
        /* Request all objects*/
        Response response = target.request()
            .header("X-SHIB-user", BaseTest.testUser)
            .header("X-SHIB-roles", BaseTest.testRoles)
            .get();
        String entity = response.readEntity(String.class);
        try {
            /* Try to parse the response*/
            JsonReader reader = Json.createReader(new StringReader(entity));
            JsonObject content = reader.readObject();
            /* Verify the response*/
            Assert.assertTrue("Unsuccessful response object:\n" + content,
                content.getBoolean("success"));
            prot.addInfo("success", content.getBoolean("success"));
            Assert.assertEquals("200", content.getString("message"));
            prot.addInfo("message", content.getString("message"));
            Assert.assertNotNull(content.getJsonArray("data"));
            prot.addInfo("objects", content.getJsonArray("data").size());
            prot.setPassed(true);
            return content;
        } catch (JsonException je) {
            prot.addInfo("exception", je.getMessage());
            Assert.fail("Exception while parsing '" + entity + "':\n"
                + je.getMessage());
        }
        return null;
    }
    /**
     * Test the GET Service by requesting a single object by id.
     * @param name the name of the entity to request.
     * @param parameter the parameters used in the request.
     * @param expected the expected json result.
     * @return The resulting json object.
     */
    public JsonObject getById(
        String name,
        String parameter,
        JsonObject expected
    ) {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName(name + " service");
        prot.setType("get by Id");
        prot.setPassed(false);
        protocol.add(prot);
        /* Create a client*/
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(baseUrl + parameter);
        prot.addInfo("parameter", parameter);
        /* Request a object by id*/
        Response response = target.request()
            .header("X-SHIB-user", BaseTest.testUser)
            .header("X-SHIB-roles", BaseTest.testRoles)
            .get();
        String entity = response.readEntity(String.class);
        try {
            /* Try to parse the response*/
            JsonReader fromServiceReader =
                Json.createReader(new StringReader(entity));
            JsonObject content = fromServiceReader.readObject();
            /* Verify the response*/
            Assert.assertTrue("Unsuccessful response object:\n" + content,
                content.getBoolean("success"));
            prot.addInfo("success", content.getBoolean("success"));
            Assert.assertEquals("200", content.getString("message"));
            prot.addInfo("message", content.getString("message"));
            Assert.assertFalse(content.getJsonObject("data").isEmpty());
            JsonObject object = content.getJsonObject("data");
            for (Entry<String, JsonValue> entry : expected.entrySet()) {
                if (entry.getKey().equals("parentModified")
                    || entry.getKey().equals("treeModified")
                    || entry.getKey().equals("letzteAenderung")) {
                    continue;
                }
                Assert.assertEquals(
                    entry.getValue(),
                    object.get(entry.getKey()));
            }
            prot.addInfo("object", "equals");
            prot.setPassed(true);
            return content;
        } catch (JsonException je) {
            prot.addInfo("exception", je.getMessage());
            Assert.fail("Exception while parsing '" + entity + "':\n"
                + je.getMessage());
        }
        return null;
    }

    /**
     * Test the GET service using filters.
     * @param name the name of the requested entity.
     * @param parameter the parameters used in the request.
     * @return the resulting json object.
     */
    public JsonObject filter(String name, String parameter) {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName(name + " service");
        prot.setType("filter");
        prot.setPassed(false);
        protocol.add(prot);
        /* Create a client*/
        Client client = ClientBuilder.newClient();
        WebTarget target =
            client.target(baseUrl + parameter);
        prot.addInfo("filter", parameter);
        /* Request the objects using the filter*/
        Response response = target.request()
            .header("X-SHIB-user", BaseTest.testUser)
            .header("X-SHIB-roles", BaseTest.testRoles)
            .get();
        String entity = response.readEntity(String.class);
        try {
            /* Try to parse the response*/
            JsonReader reader = Json.createReader(new StringReader(entity));
            JsonObject content = reader.readObject();
            /* Verify the response*/
            Assert.assertTrue("Unsuccessful response object:\n" + content,
                content.getBoolean("success"));
            prot.addInfo("success", content.getBoolean("success"));
            Assert.assertEquals("200", content.getString("message"));
            prot.addInfo("message", content.getString("message"));
            Assert.assertNotNull(content.getJsonArray("data"));
            prot.addInfo("objects", content.getJsonArray("data").size());
            prot.setPassed(true);
            return content;
        } catch (JsonException je) {
            prot.addInfo("exception", je.getMessage());
            Assert.fail("Exception while parsing '" + entity + "':\n"
                + je.getMessage());
        }
        return null;
    }

    /**
     * Test the CREATE Service.
     * @param name the name of the entity to request.
     * @param parameter the parameters used in the request.
     * @param create the object to create, embedded in POST body.
     * @return The resulting json object.
     *
     */
    public JsonObject create(String name, String parameter, JsonObject create) {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName(name + " service");
        prot.setType("create");
        prot.setPassed(false);
        protocol.add(prot);
        /* Create a client*/
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(baseUrl + parameter);
        /* Send a post request containing a new object*/
        Response response = target.request()
            .header("X-SHIB-user", BaseTest.testUser)
            .header("X-SHIB-roles", BaseTest.testRoles)
            .post(Entity.entity(create.toString(), MediaType.APPLICATION_JSON));
        String entity = response.readEntity(String.class);
        try {
            /* Try to parse the response*/
            JsonReader fromServiceReader =
                Json.createReader(new StringReader(entity));
            JsonObject content = fromServiceReader.readObject();
            /* Verify the response*/
            Assert.assertTrue("Unsuccessful response object:\n" + content,
                content.getBoolean("success"));
            prot.addInfo("success", content.getBoolean("success"));
            Assert.assertEquals("200", content.getString("message"));
            prot.addInfo("message", content.getString("message"));
            prot.setPassed(true);
            return content;
        } catch (JsonException je) {
            prot.addInfo("exception", je.getMessage());
            Assert.fail("Exception while parsing '" + entity + "':\n"
                + je.getMessage());
        }
        return null;
    }

    /**
     * Test an update service.
     * @param name the name of the entity to request.
     * @param parameter the parameters used in the request.
     * @param updateAttribute the name of the attribute to update.
     * @param oldValue the value to replace.
     * @param newValue the new value to set.
     * @return The resulting json object.
     */
    public JsonObject update(
        String name,
        String parameter,
        String updateAttribute,
        String oldValue,
        String newValue
    ) {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName(name + " service");
        prot.setType("update");
        prot.setPassed(false);
        protocol.add(prot);
        try {
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(baseUrl + parameter);
            /* Request object corresponding to id in URL */
            Response response = target.request()
                .header("X-SHIB-user", BaseTest.testUser)
                .header("X-SHIB-roles", BaseTest.testRoles)
                .get();
            String entity = response.readEntity(String.class);
            /* Try to parse the response*/
            JsonReader reader = Json.createReader(new StringReader(entity));
            JsonObject oldObject = reader.readObject().getJsonObject("data");

            /* Value replacement */
            String updatedEntity =
                oldObject.toString().replace(oldValue, newValue);
            prot.addInfo("updated datafield", updateAttribute);
            prot.addInfo("updated value", oldValue);
            prot.addInfo("updated to", newValue);

            /* Send modified object via put request*/
            WebTarget putTarget = client.target(baseUrl + parameter);
            Response updated = putTarget.request()
                .header("X-SHIB-user", BaseTest.testUser)
                .header("X-SHIB-roles", BaseTest.testRoles)
                .put(Entity.entity(updatedEntity, MediaType.APPLICATION_JSON));

            /* Try to parse the response*/
            JsonReader updatedReader = Json.createReader(
                new StringReader(updated.readEntity(String.class)));
            JsonObject updatedObject = updatedReader.readObject();
            /* Verify the response*/
            Assert.assertTrue("Unsuccessful response object:\n"
                + updatedObject,
                updatedObject.getBoolean("success"));
            prot.addInfo("success", updatedObject.getBoolean("success"));
            Assert.assertEquals("200", updatedObject.getString("message"));
            prot.addInfo("message", updatedObject.getString("message"));
            Assert.assertEquals(newValue,
                updatedObject.getJsonObject("data").getString(updateAttribute));
            prot.setPassed(true);
            return updatedObject;
        } catch (JsonException je) {
            prot.addInfo("exception", je.getMessage());
            Assert.fail(je.getMessage());
        }
        return null;
    }

    /**
     * Test the DELETE Service.
     * @param name the name of the entity to delete.
     * @param parameter the parameters used in the request.
     * @return The resulting json object.
     */
    public JsonObject delete(String name, String parameter) {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName(name + " service");
        prot.setType("delete");
        prot.setPassed(false);
        protocol.add(prot);
        /* Create a client*/
        Client client = ClientBuilder.newClient();
        WebTarget target =
            client.target(baseUrl + parameter);
        prot.addInfo("parameter", parameter);
        /* Delete object with ID given in URL */
        Response response = target.request()
            .header("X-SHIB-user", BaseTest.testUser)
            .header("X-SHIB-roles", BaseTest.testRoles)
            .delete();
        String entity = response.readEntity(String.class);
        try {
            /* Try to parse the response*/
            JsonReader reader = Json.createReader(new StringReader(entity));
            JsonObject content = reader.readObject();
            /* Verify the response*/
            Assert.assertTrue("Unsuccessful response object:\n" + content,
                content.getBoolean("success"));
            prot.addInfo("success", content.getBoolean("success"));
            Assert.assertEquals("200", content.getString("message"));
            prot.addInfo("message", content.getString("message"));
            prot.setPassed(true);
            return content;
        } catch (JsonException je) {
            prot.addInfo("exception", je.getMessage());
            Assert.fail("Exception while parsing '" + entity + "':\n"
                + je.getMessage());
        }
        return null;
    }
}
