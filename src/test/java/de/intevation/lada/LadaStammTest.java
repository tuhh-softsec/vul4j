/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3) 
 * and comes with ABSOLUTELY NO WARRANTY! Check out 
 * the documentation coming with IMIS-Labordaten-Application for details. 
 */
package de.intevation.lada;

import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;

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

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import de.intevation.lada.test.stamm.Stammdaten;


/**
 * Class to test the Lada server stammdaten services.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LadaStammTest extends BaseTest {

    private static Logger logger = Logger.getLogger(LadaStammTest.class);

    private static Integer createdOrtId;

    private Stammdaten stammdatenTest;

    public LadaStammTest () {
        stammdatenTest = new Stammdaten();
        testProtocol = new ArrayList<Protocol>();
        verboseLogging = false;
    }

    @BeforeClass
    public static void beforeTests() {
        logger.info("---------- Testing Lada Stamm Services ----------");
    }

    @Test
    @RunAsClient
    public final void testDatenbasisAll(@ArquillianResource URL baseUrl) {
        stammdatenTest.getAll(baseUrl, "datenbasis", testProtocol);
    }

    @Test
    @RunAsClient
    public final void testDatenbasisById(@ArquillianResource URL baseUrl) {
        stammdatenTest.getById(baseUrl, "datenbasis", 9, testProtocol);
    }

    @Test
    @RunAsClient
    public final void testMesseinheitAll(@ArquillianResource URL baseUrl) {
        stammdatenTest.getAll(baseUrl, "messeinheit", testProtocol);
    }

    @Test
    @RunAsClient
    public final void testMesseinheitById(@ArquillianResource URL baseUrl) {
        stammdatenTest.getById(baseUrl, "messeinheit", 207, testProtocol);
    }

    @Test
    @RunAsClient
    public final void testMessgroesseAll(@ArquillianResource URL baseUrl) {
        stammdatenTest.getAll(baseUrl, "messgroesse", testProtocol);
    }

    @Test
    @RunAsClient
    public final void testMessgroesseById(@ArquillianResource URL baseUrl) {
        stammdatenTest.getById(baseUrl, "messgroesse", 56, testProtocol);
    }

    @Test
    @RunAsClient
    public final void testMessmethodeAll(@ArquillianResource URL baseUrl) {
        stammdatenTest.getAll(baseUrl, "messmethode", testProtocol);
    }

    @Test
    @RunAsClient
    public final void testMessmethodeById(@ArquillianResource URL baseUrl) {
        stammdatenTest.getById(baseUrl, "messmethode", "GI", testProtocol);
    }

    @Test
    @RunAsClient
    public final void testMessstelleAll(@ArquillianResource URL baseUrl) {
        stammdatenTest.getAll(baseUrl, "messstelle", testProtocol);
    }

    @Test
    @RunAsClient
    public final void testMessstelleById(@ArquillianResource URL baseUrl) {
        stammdatenTest.getById(baseUrl, "messstelle", "06010", testProtocol);
    }

    @Test
    @RunAsClient
    public final void testNetzbetreiberAll(@ArquillianResource URL baseUrl) {
        stammdatenTest.getAll(baseUrl, "netzbetreiber", testProtocol);
    }

    @Test
    @RunAsClient
    public final void testNetzbetreiberById(@ArquillianResource URL baseUrl) {
        stammdatenTest.getById(baseUrl, "netzbetreiber", "06", testProtocol);
    }

    @Test
    @RunAsClient
    public final void testPflichtmessgroesseAll(@ArquillianResource URL baseUrl) {
        stammdatenTest.getAll(baseUrl, "pflichtmessgroesse", testProtocol);
    }

    @Test
    @RunAsClient
    public final void testPflichtmessgroesseById(@ArquillianResource URL baseUrl) {
        stammdatenTest.getById(baseUrl, "pflichtmessgroesse", 33, testProtocol);
    }

    @Test
    @RunAsClient
    public final void testProbenartAll(@ArquillianResource URL baseUrl) {
        stammdatenTest.getAll(baseUrl, "probenart", testProtocol);
    }

    @Test
    @RunAsClient
    public final void testProbenartById(@ArquillianResource URL baseUrl) {
        stammdatenTest.getById(baseUrl, "probenart", 1, testProtocol);
    }

    @Test
    @RunAsClient
    public final void testProbenzusatzAll(@ArquillianResource URL baseUrl) {
        stammdatenTest.getAll(baseUrl, "probenzusatz", testProtocol);
    }

    @Test
    @RunAsClient
    public final void testProbenzusatzById(@ArquillianResource URL baseUrl) {
        stammdatenTest.getById(baseUrl, "probenzusatz", "A74", testProtocol);
    }

    @Test
    @RunAsClient
    public final void testLocationAll(@ArquillianResource URL baseUrl) {
        stammdatenTest.getAll(baseUrl, "location", testProtocol);
    }

    @Test
    @RunAsClient
    public final void testLocationById(@ArquillianResource URL baseUrl) {
        stammdatenTest.getById(baseUrl, "location", "19", testProtocol);
    }

    @Test
    @RunAsClient
    public final void testKoordinatenartAll(@ArquillianResource URL baseUrl) {
        stammdatenTest.getAll(baseUrl, "koordinatenart", testProtocol);
    }

    @Test
    @RunAsClient
    public final void testKoordinatenartById(@ArquillianResource URL baseUrl) {
        stammdatenTest.getById(baseUrl, "koordinatenart", 2, testProtocol);
    }

    @Test
    @RunAsClient
    public final void testStaatAll(@ArquillianResource URL baseUrl) {
        stammdatenTest.getAll(baseUrl, "staat", testProtocol);
    }

    @Test
    @RunAsClient
    public final void testStaatById(@ArquillianResource URL baseUrl) {
        stammdatenTest.getById(baseUrl, "staat", 322, testProtocol);
    }

    @Test
    @RunAsClient
    public final void testUmweltAll(@ArquillianResource URL baseUrl) {
        stammdatenTest.getAll(baseUrl, "umwelt", testProtocol);
    }

    @Test
    @RunAsClient
    public final void testUmweltById(@ArquillianResource URL baseUrl) {
        stammdatenTest.getById(baseUrl, "umwelt", "L6", testProtocol);
    }

    @Test
    @RunAsClient
    public final void testVerwaltungseinheitAll(@ArquillianResource URL baseUrl) {
        stammdatenTest.getAll(baseUrl, "verwaltungseinheit", testProtocol);
    }

    @Test
    @RunAsClient
    public final void testVerwaltungseinheitById(@ArquillianResource URL baseUrl) {
        stammdatenTest.getById(baseUrl, "verwaltungseinheit", "09575134", testProtocol);
    }

    @Test
    @RunAsClient
    public final void testLocation1CreateService(@ArquillianResource URL baseUrl)
    throws Exception {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName("locationService");
        prot.setType("create");
        prot.setPassed(false);
        testProtocol.add(prot);
        try {
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(baseUrl + "location");
            /* Send a post request containing a new kommentar*/
            String newObj = "{\"beschreibung\":\"Neuer Ort\"," +
                "\"bezeichnung\":\"T123456\",\"hoeheLand\":null," +
                "\"koordXExtern\":\"32531152\",\"koordYExtern\":\"5684269\"," +
                "\"latitude\":51.30888,\"letzteAenderung\":1376287046332," +
                "\"longitude\":9.44693,\"nutsCode\":\"DE731\",\"unscharf\":" +
                "\"0\",\"netzbetreiberId\":null,\"staatId\":0," +
                "\"verwaltungseinheitId\":\"06611000\",\"otyp\":\"Z\"," +
                "\"koordinatenartId\":5}";

            Response response = target.request().post(
                    Entity.entity(newObj, MediaType.APPLICATION_JSON));
            String entity = response.readEntity(String.class);
            /* Try to parse the response*/
            JsonReader fromServiceReader =
                Json.createReader(new StringReader(entity));
            JsonObject content = fromServiceReader.readObject();
            /* Save the id*/
            createdOrtId =
                content.getJsonObject("data").getJsonNumber("id").intValue();
            prot.addInfo("ortId", createdOrtId);
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
    @Test
    @RunAsClient
    public final void testLocation2UpdateService(@ArquillianResource URL baseUrl)
    throws Exception {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName("locationService");
        prot.setType("update");
        prot.setPassed(false);
        testProtocol.add(prot);
        try {
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target =
                client.target(baseUrl + "location/" + createdOrtId);
            prot.addInfo("locationId", createdOrtId);
            /* Request a kommentar with the id saved when created a kommentar*/
            Response response = target.request().get();
            String entity = response.readEntity(String.class);
            /* Try to parse the response*/
            JsonReader reader = Json.createReader(new StringReader(entity));
            JsonObject oldObj = reader.readObject().getJsonObject("data");
            /* Change the text*/
            String updatedEntity =
                oldObj.toString().replace("Neuer Ort", "Neuerer Ort");
            prot.addInfo("updated field", "beschreibung");
            prot.addInfo("updated value", "Neuer Ort");
            prot.addInfo("updated to", "Neuerer Ort");
            /* Send the updated kommentar via put reauest*/
            WebTarget putTarget = client.target(baseUrl + "location/" + createdOrtId);
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
            Assert.assertEquals("Neuerer Ort",
                updatedObj.getJsonObject("data").getString("beschreibung"));
        }
        catch(JsonException je) {
            prot.addInfo("exception", je.getMessage());
            Assert.fail(je.getMessage());
        }
        prot.setPassed(true);
    }

    @Test
    @RunAsClient
    public final void testLocation3DeleteService(@ArquillianResource URL baseUrl)
    throws Exception {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName("locationService");
        prot.setType("delete");
        prot.setPassed(false);
        testProtocol.add(prot);
        try {
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target =
                client.target(baseUrl + "location/" + createdOrtId);
            prot.addInfo("locationId", createdOrtId);
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
