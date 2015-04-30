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
        compare.put("messstelle",
            "{\"id\":\"06010\",\"amtskennung\":null,\"beschreibung\":" +
            "\"Hessisches Landesamt für Umwelt und Geologie, Dienststelle " +
            "Kassel, Ludwig-Mond-Str. 33, 34121 Kassel\",\"messStelle\":" +
            "\"helm21-HLUG-Kassel\",\"mstTyp\":\"M\",\"netzbetreiberId\":" +
            "\"06\"}");
        compare.put("netzbetreiber",
            "{\"id\":\"06\",\"aktiv\":true,\"idfNetzbetreiber\":\"f\"," +
            "\"isBmn\":false,\"mailverteiler\":\"06\",\"netzbetreiber\":" +
            "\"Hessen\",\"zustMstId\":null}");
        compare.put("pflichtmessgroesse",
            "{\"id\":33,\"messgroesseId\":904,\"datenbasisId\":4,\"mmtId\":" +
            "\"A3\",\"umweltId\":\"B2\"}");
        compare.put("probenart",
            "{\"id\":1,\"beschreibung\":\"Einzelprobe\",\"probenart\":\"E\"," +
            "\"probenartEudfId\":\"A\"}");
        compare.put("probenzusatz",
            "{\"id\":\"A74\",\"beschreibung\":\"Volumenstrom\"," +
            "\"eudfKeyword\":null,\"zusatzwert\":\"VOLSTR\",\"mehId\":32}");
        compare.put("location",
            "{\"id\":19,\"beschreibung\":\"WW  Kassel\",\"bezeichnung\":" +
            "\"T060014\",\"hoeheLand\":null,\"koordXExtern\":\"32531152\"," +
            "\"koordYExtern\":\"5684269\",\"latitude\":51.30888," +
            "\"letzteAenderung\":1376287046332,\"longitude\":9.44693," +
            "\"nutsCode\":\"DE731\",\"unscharf\":\"0\",\"netzbetreiberId\":" +
            "null,\"staatId\":0,\"verwaltungseinheitId\":\"06611000\"," +
            "\"otyp\":\"Z\",\"koordinatenartId\":5}");
        compare.put("koordinatenart",
            "{\"id\":2,\"idfGeoKey\":\"D\",\"koordinatenart\":" +
            "\"geografisch-gradiell (WGS84)\"}");
        compare.put("staat",
            "{\"id\":322,\"eu\":\"0\",\"hklId\":322,\"koordXExtern\":" +
            "\"-59,6105\",\"koordYExtern\":\"13,0935\",\"staat\":" +
            "\"Barbados\",\"staatIso\":\"BB\",\"staatKurz\":\"BDS\"," +
            "\"koordinatenartId\":4}");
        compare.put("umwelt",
            "{\"id\":\"L6\",\"beschreibung\":null,\"umweltBereich\":" +
            "\"Spurenmessung Luft\",\"mehId\":62}");
        compare.put("verwaltungseinheit",
            "{\"id\":\"09575134\",\"bezeichnung\":\"Ippesheim\"," +
            "\"bundesland\":\"09000000\",\"isBundesland\":\"0\"," +
            "\"isGemeinde\":\"1\",\"isLandkreis\":\"0\",\"isRegbezirk\":" +
            "\"0\",\"koordXExtern\":\"32588490\",\"koordYExtern\":" +
            "\"5495240\",\"kreis\":\"09575000\",\"latitude\":49.60325," +
            "\"longitude\":10.2247,\"nuts\":\"DE25A09575\",\"plz\":null," +
            "\"regbezirk\":\"09500000\",\"koordinatenartId\":5}");
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
