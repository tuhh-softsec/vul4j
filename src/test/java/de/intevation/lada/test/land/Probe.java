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
 * Class containing test cases for probe objects.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class Probe {

    private static final String COMPARE_PROBE =
        "{\"id\":1,\"baId\":\"1\",\"datenbasisId\":2," +
        "\"letzteAenderung\":1339570306000,\"media\":\"Trinkwasser " +
        "Zentralversorgung Oberflächenwasser aufbereitet\",\"mediaDesk\":" +
        "\"D: 59 04 01 00 05 05 01 02 00 00 00 00\",\"mittelungsdauer\":" +
        "null,\"mstId\":\"06010\",\"netzbetreiberId\":\"06\"," +
        "\"probeentnahmeBeginn\":1336467600000,\"probeentnahmeEnde\":" +
        "null,\"probenartId\":1,\"test\":false,\"umwId\":\"N72\"," +
        "\"hauptprobenNr\":\"120510002\",\"erzeugerId\":null,\"mpKat\":\"1\"," +
        "\"mplId\":null,\"mprId\":3749,\"probeNehmerId\":726," +
        "\"solldatumBeginn\":1336341600000,\"solldatumEnde\":1336939199000," +
        "\"treeModified\":null,\"readonly\":false,\"owner\":false," +
        "\"probeIdAlt\":\"000007581034X\"}";

    private static final String CREATE_PROBE =
        "{\"baId\":\"1\",\"datenbasisId\":2,\"erzeugerId\":\"\"," +
        "\"hauptprobenNr\":\"4554567890\",\"media\":\"\",\"mediaDesk\":" +
        "\"\",\"mittelungsdauer\":\"\",\"mpKat\":\"\",\"mplId\":\"\"," +
        "\"mprId\":\"\",\"mstId\":\"11010\",\"netzbetreiberId\":\"11\"," +
        "\"probeNehmerId\":3,\"probenartId\":1,\"test\":true,\"umwId\":" +
        "\"A1\",\"letzteAenderung\":\"2015-02-09T10:58:36\"" +
        ",\"probeentnahmeBeginn\":\"2015-02-08T10:58:36\"," +
        "\"probeentnahmeEnde\":\"2015-02-09T10:58:36\",\"solldatumBeginn\":" +
        "\"2015-02-09T10:58:36\",\"solldatumEnde\":\"2015-02-09T10:58:36\"}";

    private List<Protocol> protocol;

    private static Integer createdProbeId;

    public Integer getCreatedProbeId() {
        return createdProbeId;
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
        prot.setName("ProbeService");
        prot.setType("get all");
        prot.setPassed(false);
        protocol.add(prot);
        /* Create a client*/
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(baseUrl + "probe");
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
        prot.setName("ProbeService");
        prot.setType("get by Id");
        prot.setPassed(false);
        protocol.add(prot);
        try {
            /* Create a json object from static string*/
            JsonReader fromStringRreader =
                Json.createReader(new StringReader(COMPARE_PROBE));
            JsonObject staticProbe = fromStringRreader.readObject();
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(baseUrl + "probe/1");
            prot.addInfo("probeId", 1);
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
            Assert.assertEquals(staticProbe,
                content.getJsonObject("data"));
            prot.addInfo("object", "equals");
        }
        catch(JsonException je) {
            prot.addInfo("exception",je.getMessage());
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
        prot.setName("ProbeService");
        prot.setType("get by filter");
        prot.setPassed(false);
        protocol.add(prot);
        try {
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target =
                client.target(baseUrl + "probe?qid=2&mst_id=11010&umw_id=N24");
            prot.addInfo("filter", "qid=2&mst_id=11010&umw_id=N24");
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
    public final void createService(URL baseUrl, List<Protocol> protocol)
    throws Exception {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName("ProbeService");
        prot.setType("create");
        prot.setPassed(false);
        protocol.add(prot);
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
            /* Save the id*/
            createdProbeId =
                content.getJsonObject("data").getJsonNumber("id").intValue();
            prot.addInfo("probeId", createdProbeId);
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
     * Test the probe update service.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    public final void updateService(URL baseUrl, List<Protocol> protocol)
    throws Exception {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName("ProbeService");
        prot.setType("update");
        prot.setPassed(false);
        protocol.add(prot);
        try {
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target =
                client.target(baseUrl + "probe/" + createdProbeId);
            prot.addInfo("probeId", createdProbeId);
            /* Request a with the saved id*/
            Response response = target.request().get();
            String entity = response.readEntity(String.class);
            /* Try to parse the response*/
            JsonReader reader = Json.createReader(new StringReader(entity));
            JsonObject oldProbe = reader.readObject().getJsonObject("data");
            /* Change the hauptprobenNr*/
            String updatedEntity =
                oldProbe.toString().replace("4554567890", "2345678901");
            prot.addInfo("updated datafield", "hauptprobenNr");
            prot.addInfo("updated value", "1234567890");
            prot.addInfo("updated to", "2345678901");
            /* Send the updated probe via put reauest*/
            WebTarget putTarget = client.target(baseUrl + "probe/" + createdProbeId);
            Response updated = putTarget.request().put(
                Entity.entity(updatedEntity, MediaType.APPLICATION_JSON));
            /* Try to parse the response*/
            JsonReader updatedReader = Json.createReader(
                new StringReader(updated.readEntity(String.class)));
            JsonObject updatedProbe = updatedReader.readObject();
            /* Verify the response*/
            Assert.assertTrue(updatedProbe.getBoolean("success"));
            prot.addInfo("success", updatedProbe.getBoolean("success"));
            Assert.assertEquals("200", updatedProbe.getString("message"));
            prot.addInfo("message", updatedProbe.getString("message"));
            Assert.assertEquals("2345678901",
                updatedProbe.getJsonObject("data").getString("hauptprobenNr"));
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
        prot.setName("ProbeService");
        prot.setType("delete");
        prot.setPassed(false);
        protocol.add(prot);
        try {
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target =
                client.target(baseUrl + "probe/" + createdProbeId);
            prot.addInfo("probeId", createdProbeId);
            /* Delete a probe with the id saved when created a probe*/
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
