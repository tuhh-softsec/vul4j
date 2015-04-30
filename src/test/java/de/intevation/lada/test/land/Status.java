/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.test.land;

import java.io.StringReader;
import java.net.URL;
import java.util.List;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Assert;

import de.intevation.lada.Protocol;

/**
 * Class containing test cases for status objects.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class Status {

    private static final String COMPARE =
        "{\"id\":1,\"erzeuger\":\"06010\",\"messungsId\":440,\"status\":3," +
        "\"owner\":false,\"readonly\":false,\"treeModified\":null," +
        "\"parentModified\":null," +
        "\"sdatum\":1373846400000,\"skommentar\":\"test\"}";

    private static final String CREATE =
        "{\"erzeuger\":\"06010\",\"messungsId\":MID,\"status\":3," +
        "\"sdatum\":1373846400000,\"skommentar\":\"status3\"}";

    private List<Protocol> protocol;

    private static Integer createdId;

    public Integer getCreatedId() {
        return createdId;
    }

    /**
     * @return The test protocol
     */
    public List<Protocol> getProtocol() {
        return protocol;
    }

    /**
     * Test the GET Service by requesting all objects.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    public final void getAllService(URL baseUrl, List<Protocol> protocol)
    throws Exception {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName("StatusService");
        prot.setType("get all");
        prot.setPassed(false);
        protocol.add(prot);
        /* Create a client*/
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(baseUrl + "status");
        /* Request all objects*/
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        try{
            /* Try to parse the response*/
            JsonReader reader = Json.createReader(new StringReader(entity));
            JsonObject content = reader.readObject();
            /* Verify the response*/
            Assert.assertTrue(content.getBoolean("success"));
            prot.addInfo("success", content.getBoolean("success"));
            Assert.assertEquals("200", content.getString("message"));
            prot.addInfo("message", content.getString("message"));
            Assert.assertNotNull(content.getJsonArray("data"));
            prot.addInfo("objects", content.getJsonArray("data").size());
        }
        catch(JsonException je) {
            prot.addInfo("exception", je.getMessage());
            Assert.fail(je.getMessage());
        }
        prot.setPassed(true);
    }

    /**
     * Test the GET Service by requesting a single object by id.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    public final void getByIdService(URL baseUrl, List<Protocol> protocol)
    throws Exception {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName("StatusService");
        prot.setType("get by Id");
        prot.setPassed(false);
        protocol.add(prot);
        try {
            /* Create a json object from static messwert string*/
            JsonReader fromStringRreader =
                Json.createReader(new StringReader(COMPARE));
            JsonObject staticMessung = fromStringRreader.readObject();
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(baseUrl + "status/1");
            prot.addInfo("statusId", 1);
            /* Request a object by id*/
            Response response = target.request().get();
            String entity = response.readEntity(String.class);
            /* Try to parse the response*/
            JsonReader fromServiceReader =
                Json.createReader(new StringReader(entity));
            JsonObject content = fromServiceReader.readObject();
            /* Verify the response*/
            Assert.assertTrue(content.getBoolean("success"));
            prot.addInfo("success", content.getBoolean("success"));
            Assert.assertEquals("200", content.getString("message"));
            prot.addInfo("message", content.getString("message"));
            Assert.assertEquals(staticMessung,
                content.getJsonObject("data"));
            prot.addInfo("object", "equals");
        }
        catch(JsonException je) {
            prot.addInfo("exception", je.getMessage());
            Assert.fail(je.getMessage());
        }
        prot.setPassed(true);
    }

    /**
     * Test the GET service using filters.
     *
     * @param baseUrl The url poining to the test deployment.
     */
    public final void filterService(URL baseUrl, List<Protocol> protocol) {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName("StatusService");
        prot.setType("get by filter");
        prot.setPassed(false);
        protocol.add(prot);
        try {
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target =
                client.target(baseUrl + "status?messungId=1");
            prot.addInfo("filter", "messungId=1");
            /* Request the objects using the filter*/
            Response response = target.request().get();
            String entity = response.readEntity(String.class);
            /* Try to parse the response*/
            JsonReader reader = Json.createReader(new StringReader(entity));
            JsonObject respObj = reader.readObject();
            /* Verify the response*/
            Assert.assertTrue(respObj.getBoolean("success"));
            prot.addInfo("success", respObj.getBoolean("success"));
            Assert.assertEquals("200", respObj.getString("message"));
            prot.addInfo("message", respObj.getString("message"));
            Assert.assertNotNull(respObj.getJsonArray("data"));
            prot.addInfo("objects", respObj.getJsonArray("data").size());
        }
        catch(JsonException je) {
            prot.addInfo("exception", je.getMessage());
            Assert.fail(je.getMessage());
        }
        prot.setPassed(true);
    }

    /**
     * Test the CREATE Service.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    public final void createService(
        URL baseUrl,
        List<Protocol> protocol,
        Integer messungId)
    throws Exception {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName("StatusService");
        prot.setType("create");
        prot.setPassed(false);
        protocol.add(prot);
        try {
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(baseUrl + "status");
            /* Send a post request containing a new object*/
            String stat = CREATE.replace("MID", messungId.toString());
            Response response = target.request().post(
                    Entity.entity(stat, MediaType.APPLICATION_JSON));
            String entity = response.readEntity(String.class);
            /* Try to parse the response*/
            JsonReader fromServiceReader =
                Json.createReader(new StringReader(entity));
            JsonObject content = fromServiceReader.readObject();
            /* Save the id*/
            createdId =
                content.getJsonObject("data").getJsonNumber("id").intValue();
            prot.addInfo("statusId", createdId);
            /* Verify the response*/
            Assert.assertTrue(content.getBoolean("success"));
            prot.addInfo("success", content.getBoolean("success"));
            Assert.assertEquals("200", content.getString("message"));
            prot.addInfo("message", content.getString("message"));
        }
        catch(JsonException je) {
            prot.addInfo("exception", je.getMessage());
            Assert.fail(je.getMessage());
        }
        prot.setPassed(true);
    }

    /**
     * Test the UPDATE Service.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    public final void updateService(URL baseUrl, List<Protocol> protocol)
    throws Exception {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName("StatusService");
        prot.setType("update");
        prot.setPassed(false);
        protocol.add(prot);
        try {
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target =
                client.target(baseUrl + "status/" + createdId);
            prot.addInfo("statusId", createdId);
            /* Request an object with the saved id*/
            Response response = target.request().get();
            String entity = response.readEntity(String.class);
            /* Try to parse the response*/
            JsonReader reader = Json.createReader(new StringReader(entity));
            JsonObject old = reader.readObject().getJsonObject("data");
            /* Change the mmtId*/
            String updatedEntity =
                old.toString().replace("status3", "status3updated");
            prot.addInfo("updated field", "skommentar");
            prot.addInfo("updated value", "status3");
            prot.addInfo("updated to", "status3updated");
            /* Send the updated messwert via put request*/
            WebTarget putTarget = client.target(baseUrl + "status/" + createdId);
            Response updated = putTarget.request().put(
                Entity.entity(updatedEntity, MediaType.APPLICATION_JSON));
            /* Try to parse the response*/
            JsonReader updatedReader = Json.createReader(
                new StringReader(updated.readEntity(String.class)));
            JsonObject updatedObj = updatedReader.readObject();
            /* Verify the response*/
            Assert.assertTrue(updatedObj.getBoolean("success"));
            prot.addInfo("success", updatedObj.getBoolean("success"));
            Assert.assertEquals("200", updatedObj.getString("message"));
            prot.addInfo("message", updatedObj.getString("message"));
            Assert.assertEquals("status3updated",
                updatedObj.getJsonObject("data").getString("skommentar"));
        }
        catch(JsonException je) {
            prot.addInfo("exception", je.getMessage());
            Assert.fail(je.getMessage());
        }
        prot.setPassed(true);
    }

    /**
     * Test the DELETE Service.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    public final void deleteService(URL baseUrl, List<Protocol> protocol) {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName("StatusService");
        prot.setType("delete");
        prot.setPassed(false);
        protocol.add(prot);
        try {
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target =
                client.target(baseUrl + "status/" + createdId);
            prot.addInfo("statusId", createdId);
            /* Delete the object with the saved id*/
            Response response = target.request().delete();
            String entity = response.readEntity(String.class);
            /* Try to parse the response*/
            JsonReader reader = Json.createReader(new StringReader(entity));
            JsonObject respObj = reader.readObject();
            /* Verify the response*/
            Assert.assertTrue(respObj.getBoolean("success"));
            prot.addInfo("success", respObj.getBoolean("success"));
            Assert.assertEquals("200", respObj.getString("message"));
            prot.addInfo("message", respObj.getString("message"));
        }
        catch(JsonException je) {
            prot.addInfo("exception", je.getMessage());
            Assert.fail(je.getMessage());
        }
        prot.setPassed(true);
    }
}
