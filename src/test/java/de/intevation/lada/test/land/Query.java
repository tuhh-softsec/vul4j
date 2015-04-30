/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.test.land;

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


/**
 * Class containing test cases for query config.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class Query {

    private List<Protocol> protocol;

    /**
     * @return The test protocol
     */
    public List<Protocol> getProtocol() {
        return protocol;
    }

    /**
     * Test the GET Service by requesting all queries.
     *
     * @param baseUrl The url pointing to the test deployment.
     */
    public final void getAllService(URL baseUrl, List<Protocol> protocol)
    throws Exception {
        System.out.print(".");
        Protocol prot = new Protocol();
        prot.setName("QueryService");
        prot.setType("get all");
        prot.setPassed(false);
        protocol.add(prot);
        /* Create a client*/
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(baseUrl + "query/probe");
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
}
