/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.test.stamm;

import java.net.URL;
import java.util.List;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.junit.Assert;

import de.intevation.lada.Protocol;
import de.intevation.lada.test.ServiceTest;

public class DeskriptorenTest extends ServiceTest {
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

        // Prepare expected object
        JsonObject content = readJsonResource("/datasets/dbUnit_deskriptor.json")
            .getJsonArray("stamm.deskriptoren").getJsonObject(0);
        JsonObjectBuilder builder = convertObject(content);
        expectedById = builder.build();
        Assert.assertNotNull(expectedById);
    }

    public final void execute() {
        getAll("deskriptor", "rest/deskriptor");
        getAll("deskriptor", "rest/deskriptor?layer=1");
        getAll("deskriptor", "rest/deskriptor?layer=1&parents=1, 2");
        getById("deskriptor", "rest/deskriptor/1000", expectedById);
    }
}
