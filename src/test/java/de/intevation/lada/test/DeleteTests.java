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
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.Assert;

/**
 * Class to test all DELETE services.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class DeleteTests {

    private Integer probeId;

    private Integer messungId;

    /**
     * Main entry point in this class to start the tests.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    public final void test(
        URL baseUrl,
        Integer probeId,
        Integer messungId)
    throws Exception {
        System.out.println("\nStarting test on DELETE Services:");
        this.probeId = probeId;
        this.messungId = messungId;
        messungDeleteService(baseUrl);
        probeDeleteService(baseUrl);
    }

    /**
     * Test the DELETE Service.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    private final void probeDeleteService(URL baseUrl) {
        System.out.println("Testing delete: ");
        try {
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target =
                client.target(baseUrl + "probe/" + this.probeId);
            /* Delete a probe with the id saved when created a probe*/
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
     * Test the messung DELETE Service.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    private final void messungDeleteService(URL baseUrl) {
        System.out.println("Testing Messung: ");
        try {
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target =
                client.target(baseUrl + "messung/" + this.messungId);
            /* Delete a probe with the id saved when created a probe*/
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
}
