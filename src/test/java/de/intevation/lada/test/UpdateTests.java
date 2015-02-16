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
 * Class to test all UPDATE services.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class UpdateTests {

    private Integer probeId;

    private Integer messungId;

    /**
     * Main entry point in this class to start the tests.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    public final void test(URL baseUrl, Integer probeId, Integer messungId)
    throws Exception {
        System.out.println("\nStarting test on UPDATE Services:");
        this.probeId = probeId;
        this.messungId = messungId;
        probeUpdateService(baseUrl);
        messungUpdate(baseUrl);
    }

    /**
     * Test the probe update service.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    private final void probeUpdateService(URL baseUrl)
    throws Exception {
        System.out.println("Testing ProbeService: ");
        try {
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target =
                client.target(baseUrl + "probe/" + this.probeId);
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
     * Test the messung UPDATE Service.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    private final void messungUpdate(URL baseUrl)
    throws Exception {
        System.out.println("Testing MessungService: ");
        try {
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target =
                client.target(baseUrl + "messung/" + this.messungId);
            /* Request a probe with the id saved when created a probe*/
            Response response = target.request().get();
            String entity = response.readEntity(String.class);
            /* Try to parse the response*/
            JsonReader reader = Json.createReader(new StringReader(entity));
            JsonObject oldProbe = reader.readObject().getJsonObject("data");
            /* Change the hauptprobenNr*/
            String updatedEntity =
                oldProbe.toString().replace("A4", "G1");
            /* Send the updated probe via put reauest*/
            WebTarget putTarget = client.target(baseUrl + "messung");
            Response updated = putTarget.request().put(
                Entity.entity(updatedEntity, MediaType.APPLICATION_JSON));
            /* Try to parse the response*/
            JsonReader updatedReader = Json.createReader(
                new StringReader(updated.readEntity(String.class)));
            JsonObject updatedProbe = updatedReader.readObject();
            /* Verify the response*/
            Assert.assertTrue(updatedProbe.getBoolean("success"));
            Assert.assertEquals("200", updatedProbe.getString("message"));
            Assert.assertEquals("G1",
                updatedProbe.getJsonObject("data").getString("mmtId"));
        }
        catch(JsonException je) {
            Assert.fail(je.getMessage());
        }
        System.out.println("passed");
    }

}
