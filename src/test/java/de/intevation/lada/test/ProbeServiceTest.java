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
        "\"solldatumEnde\":1336939199000}";

    public final void test(URL baseUrl) throws Exception {
        probeGetAllService(baseUrl);
        probeGetByIdService(baseUrl);
    }

    private final void probeGetAllService(URL baseUrl)
    throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(baseUrl + "probe");
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        try{
            JsonReader reader = Json.createReader(new StringReader(entity));
            JsonObject content = reader.readObject();
            Assert.assertTrue(content.getBoolean("success"));
            Assert.assertEquals("200", content.getString("message"));
        }
        catch(JsonException je) {
            Assert.fail(je.getMessage());
        }
    }

    private final void probeGetByIdService(URL baseUrl)
    throws Exception {
        try {
            JsonReader fromStrinRreader =
                Json.createReader(new StringReader(COMPARE_PROBE));
            JsonObject staticProbe = fromStrinRreader.readObject();
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(baseUrl + "probe/1");
            Response response = target.request().get();
            String entity = response.readEntity(String.class);
            JsonReader fromServiceReader =
                Json.createReader(new StringReader(entity));
            JsonObject content = fromServiceReader.readObject();
            Assert.assertTrue(content.getBoolean("success"));
            Assert.assertEquals("200", content.getString("message"));
            Assert.assertEquals(staticProbe,
                content.getJsonObject("data"));
        }
        catch(JsonException je) {
            Assert.fail(je.getMessage());
        }
    }
}
