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
 * Class to test all CREATE services.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class CreateTests {

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

    private static final String CREATE_MESSUNG =
        "{\"probeId\":\"PID\",\"mmtId\":\"A4\",\"nebenprobenNr\":\"10R1\"," +
        "\"messdauer\":10,\"fertig\":false,\"letzteAenderung\":null," +
        "\"geplant\":true,\"messzeitpunkt\":\"2015-02-09T10:58:36\"}";


    private static Integer createdProbeId;
    private static Integer createdMessungId;

    public Integer getCreatedProbeId() {
        return createdProbeId;
    }

    public Integer getCreatedMessungId() {
        return createdMessungId;
    }

    /**
     * Main entry point in this class to start the tests.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    public final void test(URL baseUrl) throws Exception {
        System.out.println("\nStarting test on CREATE Services:");
        probeCreateService(baseUrl);
        messungCreateService(baseUrl);
    }

    /**
     * Test the CREATE Service.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    private final void probeCreateService(URL baseUrl)
    throws Exception {
        System.out.println("Testing ProbeService: ");
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
            createdProbeId =
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
     * Test the messung CREATE Service.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    private final void messungCreateService(URL baseUrl)
    throws Exception {
        System.out.println("Testing MessungService: ");
        try {
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(baseUrl + "messung");
            /* Send a post request containing a new probe*/
            String mess = CREATE_MESSUNG.replace("PID", createdProbeId.toString());
            Response response = target.request().post(
                    Entity.entity(mess, MediaType.APPLICATION_JSON));
            String entity = response.readEntity(String.class);
            /* Try to parse the response*/
            JsonReader fromServiceReader =
                Json.createReader(new StringReader(entity));
            JsonObject content = fromServiceReader.readObject();
            /* Save the probeid*/
            createdMessungId =
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
}
