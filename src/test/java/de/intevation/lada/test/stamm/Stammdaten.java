package de.intevation.lada.test.stamm;

import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class Stammdaten {

    private static Map<String, String> compare;

    public Stammdaten() {
        compare = new HashMap<String, String>();
        compare.put("datenbasis",
            "{\"id\":9,\"beschreibung\":\"Europa\",\"datenbasis\":\"Europa\"}");
        compare.put("messeinheit",
            "{\"id\":207,\"beschreibung\":\"Becquerel pro Stunde\"," +
            "\"einheit\":\"Bq/h\",\"eudfMesseinheitId\":null," +
            "\"umrechnungsFaktorEudf\":null}");
        compare.put("messgroesse",
            "{\"id\":56,\"beschreibung\":\"Mangan\",\"defaultFarbe\":" +
            "\"175175075\",\"eudfNuklidId\":50,\"idfNuklidKey\":\"Mn54\"," +
            "\"istLeitnuklid\":false,\"kennungBvl\":\"1925054\"," +
            "\"messgroesse\":\"Mn 54\"}");
        compare.put("messmethode",
            "{\"id\":\"GI\",\"beschreibung\":null,\"messmethode\":" +
            "\"Iod, Gamma-Spektrometrie\"}");
    }

    /**
     * Test the GET Service by requesting all objects.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    public final void getAll(URL baseUrl, String type, List<Protocol> protocol) {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName(type + "Service");
        prot.setType("get all");
        prot.setPassed(false);
        protocol.add(prot);
        Assert.assertNotNull(type);
        /* Create a client*/
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(baseUrl + type);
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

    public final void getById(
        URL baseUrl,
        String type,
        Object id,
        List<Protocol> protocol
    ) {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName(type + "Service");
        prot.setType("get by Id");
        prot.setPassed(false);
        protocol.add(prot);
        try {
            /* Create a json object from static string*/
            JsonReader fromStringRreader =
                Json.createReader(new StringReader(compare.get(type)));
            JsonObject comp = fromStringRreader.readObject();
            /* Create a client*/
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(baseUrl + type +"/" + id);
            prot.addInfo(type + "Id", id);
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
            Assert.assertEquals(comp,
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
