/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.test.land;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import org.junit.Assert;

import de.intevation.lada.Protocol;
import de.intevation.lada.test.ServiceTest;

/**
 * Test messung kommentar entities.
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class KommentarMTest extends ServiceTest {

    private static final long TS1 = 1450371851654L;
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
            "datum",
            "treeModified"
        });

        // Prepare expected probe object
        JsonObject content =
            readJsonResource("/datasets/dbUnit_mkommentar.json");
        JsonObject messung =
            content.getJsonArray("land.kommentar_m").getJsonObject(0);
        JsonObjectBuilder builder = convertObject(messung);
        builder.add("parentModified", TS1);
        builder.add("readonly", JsonValue.FALSE);
        builder.add("owner", JsonValue.TRUE);
        expectedById = builder.build();
        Assert.assertNotNull(expectedById);

        // Load probe object to test POST request
        create = readJsonResource("/datasets/mkommentar.json");
        Assert.assertNotNull(create);
    }

    /**
     * Execute the tests.
     */
    public final void execute() {
        getAll("mkommentar", "rest/mkommentar?messungsId=1000");
        getById("mkommentar", "rest/mkommentar/1000", expectedById);
        JsonObject created = create("mkommentar", "rest/mkommentar", create);
        update(
            "mkommentar",
            "rest/mkommentar/1000",
            "text", "Testkommentar",
            "Testkommentar geändert");
        delete(
            "mkommentar",
            "rest/mkommentar/" + created.getJsonObject("data").get("id"));
    }

}
