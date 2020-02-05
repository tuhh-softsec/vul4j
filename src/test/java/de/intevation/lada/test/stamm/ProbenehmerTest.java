/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.test.stamm;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.junit.Assert;

import de.intevation.lada.Protocol;
import de.intevation.lada.test.ServiceTest;

public class ProbenehmerTest extends ServiceTest {
    private JsonObject expectedById;
    private JsonObject create;

    /**
     * @return The test protocol
     */
    public List<Protocol> getProtocol() {
        return protocol;
    }

    @Override
    public void init(
        URL baseUrl,
        List<Protocol> protocol
    ) {
        super.init(baseUrl, protocol);
        // Attributes with timestamps
        timestampAttributes = Arrays.asList(new String[]{
            "letzteAenderung"
        });

        // Prepare expected object
        JsonObject content = readJsonResource("/datasets/dbUnit_probenehmer.json");
        JsonObject probenehmer = content.getJsonArray("stamm.probenehmer").getJsonObject(0);
        JsonObjectBuilder builder = convertObject(probenehmer);
        expectedById = builder.build();
        Assert.assertNotNull(expectedById);

        // Load object to test POST request
        create = readJsonResource("/datasets/probenehmer.json");
        Assert.assertNotNull(create);
    }

    public final void execute() {
        getAll("probenehmer", "rest/probenehmer");
        getById("probenehmer", "rest/probenehmer/1000", expectedById);
        update("probenehmer", "rest/probenehmer/1000", "bezeichnung", "Testbezeichnung", "geändert");
        JsonObject created = create("probenehmer", "rest/probenehmer", create);
        delete("probenehmer", "rest/probenehmer/" + created.getJsonObject("data").get("id"));
    }
}
