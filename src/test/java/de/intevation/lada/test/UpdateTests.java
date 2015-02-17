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
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Assert;

import de.intevation.lada.Protocol;

/**
 * Class to test all UPDATE services.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class UpdateTests {

    private static List<Protocol> protocol;

    private Integer probeId;

    private Integer messungId;

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
    public final void test(URL baseUrl, Integer probeId, Integer messungId)
    throws Exception {
        protocol = new ArrayList<Protocol>();
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
                client.target(baseUrl + "probe/" + this.probeId);
            prot.addInfo("probeId", this.probeId);
            /* Request a probe with the id saved when created a probe*/
            Response response = target.request().get();
            String entity = response.readEntity(String.class);
            /* Try to parse the response*/
            JsonReader reader = Json.createReader(new StringReader(entity));
            JsonObject oldProbe = reader.readObject().getJsonObject("data");
            /* Change the hauptprobenNr*/
            String updatedEntity =
                oldProbe.toString().replace("1234567890", "2345678901");
            prot.addInfo("updated datafield", "hauptprobenNr");
            prot.addInfo("updated value", "1234567890");
            prot.addInfo("updated to", "1234567890");
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
     * Test the messung UPDATE Service.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    private final void messungUpdate(URL baseUrl)
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
                client.target(baseUrl + "messung/" + this.messungId);
            prot.addInfo("messungId", this.messungId);
            /* Request a probe with the id saved when created a probe*/
            Response response = target.request().get();
            String entity = response.readEntity(String.class);
            /* Try to parse the response*/
            JsonReader reader = Json.createReader(new StringReader(entity));
            JsonObject oldMessung = reader.readObject().getJsonObject("data");
            /* Change the hauptprobenNr*/
            String updatedEntity =
                oldMessung.toString().replace("A4", "G1");
            prot.addInfo("updated field", "mmtId");
            prot.addInfo("updated value", "A4");
            prot.addInfo("updated to", "G1");
            /* Send the updated probe via put reauest*/
            WebTarget putTarget = client.target(baseUrl + "messung");
            Response updated = putTarget.request().put(
                Entity.entity(updatedEntity, MediaType.APPLICATION_JSON));
            /* Try to parse the response*/
            JsonReader updatedReader = Json.createReader(
                new StringReader(updated.readEntity(String.class)));
            JsonObject updatedMessung = updatedReader.readObject();
            /* Verify the response*/
            Assert.assertTrue(updatedMessung.getBoolean("success"));
            prot.addInfo("success", updatedMessung.getBoolean("success"));
            Assert.assertEquals("200", updatedMessung.getString("message"));
            prot.addInfo("message", updatedMessung.getString("message"));
            Assert.assertEquals("G1",
                updatedMessung.getJsonObject("data").getString("mmtId"));
        }
        catch(JsonException je) {
            prot.addInfo("exception", je.getMessage());
            Assert.fail(je.getMessage());
        }
        prot.setPassed(true);
    }

}
