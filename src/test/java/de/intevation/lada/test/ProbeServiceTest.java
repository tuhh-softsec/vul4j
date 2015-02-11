/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3) 
 * and comes with ABSOLUTELY NO WARRANTY! Check out 
 * the documentation coming with IMIS-Labordaten-Application for details. 
 */
package de.intevation.lada.test;

import java.io.StringReader;
import java.net.URL;

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

/**
 * Class to test the Lada probe REST service.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class ProbeServiceTest {

    private static final String COMPARE_PROBE =
        "{\"id\":1,\"baId\":\"1\",\"datenbasisId\":2," +
        "\"letzteAenderung\":1339570306000,\"media\":\"Trinkwasser " +
        "Zentralversorgung Oberflächenwasser aufbereitet\",\"mediaDesk\":" +
        "\"D: 59 04 01 00 05 05 01 02 00 00 00 00\",\"mittelungsdauer\":" +
        "null,\"mstId\":\"06010\",\"netzbetreiberId\":\"06\"," +
        "\"probeentnahmeBeginn\":1336467600000,\"probeentnahmeEnde\":" +
        "null,\"probenartId\":1,\"test\":false,\"umwId\":\"N72\"," +
        "\"erzeugerId\":null,\"mpKat\":\"1\",\"mplId\":null,\"mprId\":3749," +
        "\"probeNehmerId\":726,\"solldatumBeginn\":1336341600000," +
        "\"solldatumEnde\":1336939199000,\"probeIdAlt\":\"000007581034X\"," +
        "\"hauptprobenNr\":\"120510002\"}";

    private static final String CREATE_PROBE =
        "{\"baId\":\"1\",\"datenbasisId\":2,\"erzeugerId\":\"\"," +
        "\"hauptprobenNr\":\"1234567890\",\"media\":\"\",\"mediaDesk\":" +
        "\"\",\"mittelungsdauer\":\"\",\"mpKat\":\"\",\"mplId\":\"\"," +
        "\"mprId\":\"\",\"mstId\":\"11010\",\"netzbetreiberId\":\"11\"," +
        "\"probeNehmerId\":3,\"probenartId\":1,\"test\":true,\"umwId\":" +
        "\"A1\",\"letzteAenderung\":\"2015-02-09T10:58:36\"" +
        ",\"probeentnahmeBeginn\":\"2015-02-08T10:58:36\"," +
        "\"probeentnahmeEnde\":\"2015-02-09T10:58:36\",\"solldatumBeginn\":" +
        "\"2015-02-09T10:58:36\",\"solldatumEnde\":\"2015-02-09T10:58:36\"}";


    private Integer createdProbeId;

    /**
     * Main entry point in this class to start the tests.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    public final void test(URL baseUrl) throws Exception {
        System.out.println("\nStarting test (2) on Probe-Service:");
        probeGetAllService(baseUrl);
        probeGetByIdService(baseUrl);
        probeCreate(baseUrl);
        probeUpdate(baseUrl);
        probeDelete(baseUrl);
        probeFilter(baseUrl);
    }

    /**
     * Test the GET Service by requesting all probe objects.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    private final void probeGetAllService(URL baseUrl)
    throws Exception {
        System.out.println("Testing get: ");
        /* Create a client*/
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(baseUrl + "probe");
        /* Request all probe objects*/
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        try{
            /* Try to parse the response*/
            JsonReader reader = Json.createReader(new StringReader(entity));
            JsonObject content = reader.readObject();
            /* Verify the response*/
            Assert.assertTrue(content.getBoolean("success"));
            Assert.assertEquals("200", content.getString("message"));
        }
        catch(JsonException je) {
            Assert.fail(je.getMessage());
        }
        System.out.println("passed");
    }

    /**
     * Test the GET Service by requesting a single probe object by id.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    private final void probeGetByIdService(URL baseUrl)
    throws Exception {
        System.out.println("Testing getById: ");
        try {
            /* Create a json object from static probe string*/
            JsonReader fromStringRreader =
                Json.createReader(new StringReader(COMPARE_PROBE));
            JsonObject staticProbe = fromStringRreader.readObject();
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(baseUrl + "probe/1");
            /* Request a probe object by id*/
            Response response = target.request().get();
            String entity = response.readEntity(String.class);
            /* Try to parse the response*/
            JsonReader fromServiceReader =
                Json.createReader(new StringReader(entity));
            JsonObject content = fromServiceReader.readObject();
            /* Verify the response*/
            Assert.assertTrue(content.getBoolean("success"));
            Assert.assertEquals("200", content.getString("message"));
            Assert.assertEquals(staticProbe,
                content.getJsonObject("data"));
        }
        catch(JsonException je) {
            Assert.fail(je.getMessage());
        }
        System.out.println("passed");
    }

    /**
     * Test the CREATE Service.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    private final void probeCreate(URL baseUrl)
    throws Exception {
        System.out.println("Testing create: ");
        try {
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(baseUrl + "probe");
            /* Send a post request containing a new probe*/
            Response response = target.request().post(
                    Entity.entity(CREATE_PROBE, MediaType.APPLICATION_JSON));
            String entity = response.readEntity(String.class);
            /* Try to parse the response*/
            JsonReader fromServiceReader =
                Json.createReader(new StringReader(entity));
            JsonObject content = fromServiceReader.readObject();
            /* Save the probeid*/
            this.createdProbeId =
                content.getJsonObject("data").getJsonNumber("id").intValue();
            /* Verify the response*/
            Assert.assertTrue(content.getBoolean("success"));
            Assert.assertEquals("200", content.getString("message"));
        }
        catch(JsonException je) {
            Assert.fail(je.getMessage());
        }
        System.out.println("passed");
    }

    /**
     * Test the UPDATE Service.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    private final void probeUpdate(URL baseUrl)
    throws Exception {
        System.out.println("Testing update: ");
        try {
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target =
                client.target(baseUrl + "probe/" + this.createdProbeId);
            /* Request a probe with the id saved when created a probe*/
            Response response = target.request().get();
            String entity = response.readEntity(String.class);
            /* Try to parse the response*/
            JsonReader reader = Json.createReader(new StringReader(entity));
            JsonObject oldProbe = reader.readObject().getJsonObject("data");
            /* Change the hauptprobenNr*/
            String updatedEntity =
                oldProbe.toString().replace("1234567890", "2345678901");
            /* Send the updated probe via put reauest*/
            WebTarget putTarget = client.target(baseUrl + "probe");
            Response updated = putTarget.request().put(
                Entity.entity(updatedEntity, MediaType.APPLICATION_JSON));
            /* Try to parse the response*/
            JsonReader updatedReader = Json.createReader(
                new StringReader(updated.readEntity(String.class)));
            JsonObject updatedProbe = updatedReader.readObject();
            /* Verify the response*/
            Assert.assertTrue(updatedProbe.getBoolean("success"));
            Assert.assertEquals("200", updatedProbe.getString("message"));
            Assert.assertEquals("2345678901",
                updatedProbe.getJsonObject("data").getString("hauptprobenNr"));
        }
        catch(JsonException je) {
            Assert.fail(je.getMessage());
        }
        System.out.println("passed");
    }

    /**
     * Test the DELETE Service.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    private final void probeDelete(URL baseUrl) {
        System.out.println("Testing delete: ");
        try {
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target =
                client.target(baseUrl + "probe/" + this.createdProbeId);
            /* Delete a probe with th id saved when created a probe*/
            Response response = target.request().delete();
            String entity = response.readEntity(String.class);
            /* Try to parse the response*/
            JsonReader reader = Json.createReader(new StringReader(entity));
            JsonObject respObj = reader.readObject();
            /* Verify the response*/
            Assert.assertTrue(respObj.getBoolean("success"));
            Assert.assertEquals("200", respObj.getString("message"));
        }
        catch(JsonException je) {
            Assert.fail(je.getMessage());
        }
        System.out.println("passed");
    }

    /**
     * Test the GET service using filters.
     *
     * @param baseUrl The url poining to the test deployment.
     */
    private final void probeFilter(URL baseUrl) {
        System.out.println("Testing filter: ");
        try {
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target =
                client.target(baseUrl + "probe?qid=2&mst_id=11010&umw_id=N24");
            /* Request the probe objects using the filter*/
            Response response = target.request().get();
            String entity = response.readEntity(String.class);
            /* Try to parse the response*/
            JsonReader reader = Json.createReader(new StringReader(entity));
            JsonObject respObj = reader.readObject();
            /* Verify the response*/
            Assert.assertTrue(respObj.getBoolean("success"));
            Assert.assertEquals("200", respObj.getString("message"));
            Assert.assertNotNull(respObj.getJsonArray("data"));
        }
        catch(JsonException je) {
            Assert.fail(je.getMessage());
        }
        System.out.println("passed");
    }
}
