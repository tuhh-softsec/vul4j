package de.intevation.lada.test;

import java.io.StringReader;
import java.net.URL;
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

public class KommentarP {

    private List<Protocol> protocol;

    private static final String COMPARE_KOMMENTARP =
        "{\"datum\":1321002077000,\"erzeuger\":\"06010\",\"id\":1,\"text\":" +
        "\"Die Probe wurde in Darmstadt gammaspektrometrisch gemessen und " +
        "für die Sr-Bestimmung verascht. \",\"probeId\":361}";

    /**
     * @return the protocol
     */
    public List<Protocol> getProtocol() {
        return protocol;
    }

    /**
     * Test the GET Service by requesting all KommentarP objects.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    public final void getAllService(URL baseUrl, List<Protocol> protocol)
    throws Exception {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName("ProbeKommentarService");
        prot.setType("get all");
        prot.setPassed(false);
        protocol.add(prot);
        /* Create a client*/
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(baseUrl + "pkommentar");
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
     * Test the GET Service by requesting a single KommentarP object by id.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    public final void getByIdService(URL baseUrl, List<Protocol> protocol)
    throws Exception {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName("ProbeKommentarService");
        prot.setType("get by Id");
        prot.setPassed(false);
        protocol.add(prot);
        try {
            /* Create a json object from static string*/
            JsonReader fromStringRreader =
                Json.createReader(new StringReader(COMPARE_KOMMENTARP));
            JsonObject staticKommentar = fromStringRreader.readObject();
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(baseUrl + "pkommentar/1");
            prot.addInfo("kommentarId", 1);
            /* Request an object by id*/
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
            Assert.assertEquals(staticKommentar,
                content.getJsonObject("data"));
            prot.addInfo("object", "equals");
        }
        catch(JsonException je) {
            prot.addInfo("exception", je.getMessage());
            Assert.fail(je.getMessage());
        }
        prot.setPassed(true);
    }
}
