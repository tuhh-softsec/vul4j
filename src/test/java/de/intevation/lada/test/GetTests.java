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
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.Assert;

import de.intevation.lada.Protocol;

/**
 * Class to test all GET services.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class GetTests {

    private static List<Protocol> protocol;

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

    private static final String COMPARE_MESSUNG =
        "{\"id\":1,\"fertig\":true,\"letzteAenderung\":1331536340000," +
        "\"messdauer\":73929,\"messzeitpunkt\":1329139620000,\"mmtId\":" +
        "\"G1\",\"probeId\":575,\"nebenprobenNr\":\"01G1\",\"geplant\":true," +
        "\"messungsIdAlt\":1}";

    private static final String COMPARE_KOMMENTARP = "";

    /**
     * @return the protocol
     */
    public static List<Protocol> getProtocol() {
        return protocol;
    }

    /**
     * Main entry point in this class to start the tests.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    public final void test(URL baseUrl) throws Exception {
        protocol = new ArrayList<Protocol>();
        queryGetService(baseUrl);
        probeGetAllService(baseUrl);
        probeGetByIdService(baseUrl);
        probeFilterService(baseUrl);
        messungGetAllService(baseUrl);
        messungGetByIdService(baseUrl);
        messungFilterService(baseUrl);
        pkommentarGetAllService(baseUrl);
        //pkommentarGetByIdService(baseUrl);
    }

    /**
     * Test the GET Service by requesting all queries.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    private final void queryGetService(URL baseUrl)
    throws Exception {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName("QueryService");
        prot.setType("get all");
        prot.setPassed(false);
        protocol.add(prot);
        /* Create a client*/
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(baseUrl + "query");
        /* Request all queries*/
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        try{
            /* Try to parse the response*/
            JsonReader reader = Json.createReader(new StringReader(entity));
            JsonObject content = reader.readObject();
            /* Verfiy the response*/
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
     * Test the GET Service by requesting all probe objects.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    private final void probeGetAllService(URL baseUrl)
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
        /* Request all probe objects*/
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
     * Test the GET Service by requesting a single probe object by id.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    private final void probeGetByIdService(URL baseUrl)
    throws Exception {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName("ProbeService");
        prot.setType("get by Id");
        prot.setPassed(false);
        protocol.add(prot);
        try {
            /* Create a json object from static probe string*/
            JsonReader fromStringRreader =
                Json.createReader(new StringReader(COMPARE_PROBE));
            JsonObject staticProbe = fromStringRreader.readObject();
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(baseUrl + "probe/1");
            prot.addInfo("probeId", 1);
            /* Request a probe object by id*/
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
     * Test the GET probe service using filters.
     *
     * @param baseUrl The url poining to the test deployment.
     */
    private final void probeFilterService(URL baseUrl) {
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
            /* Request the probe objects using the filter*/
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
     * Test the GET Service by requesting all messung objects.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    private final void messungGetAllService(URL baseUrl)
    throws Exception {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName("MessungService");
        prot.setType("get all");
        prot.setPassed(false);
        protocol.add(prot);
        /* Create a client*/
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(baseUrl + "messung");
        /* Request all probe objects*/
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
     * Test the GET Service by requesting a single messung object by id.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    private final void messungGetByIdService(URL baseUrl)
    throws Exception {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName("MessungService");
        prot.setType("get by Id");
        prot.setPassed(false);
        protocol.add(prot);
        try {
            /* Create a json object from static messung string*/
            JsonReader fromStringRreader =
                Json.createReader(new StringReader(COMPARE_MESSUNG));
            JsonObject staticMessung = fromStringRreader.readObject();
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(baseUrl + "messung/1");
            prot.addInfo("messungId", 1);
            /* Request a probe object by id*/
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
     * Test the GET messung service using filters.
     *
     * @param baseUrl The url poining to the test deployment.
     */
    private final void messungFilterService(URL baseUrl) {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName("MessungService");
        prot.setType("get by filter");
        prot.setPassed(false);
        protocol.add(prot);
        try {
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target =
                client.target(baseUrl + "messung?probeId=1");
            prot.addInfo("filter", "probeId=1");
            /* Request the probe objects using the filter*/
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

}
