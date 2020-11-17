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
 * Test messung entities.
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class MessungTest extends ServiceTest {

    private static final int ID1000 = 1000;
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
            "letzteAenderung",
            "messzeitpunkt",
            "treeModified"
        });

        // Prepare expected probe object
        JsonObject content = readJsonResource("/datasets/dbUnit_messung.json");
        JsonObject messung =
            content.getJsonArray("land.messung").getJsonObject(0);
        JsonObjectBuilder builder = convertObject(messung);
        JsonObject trans =
            content.getJsonArray("land.messung_translation").getJsonObject(0);
        builder.add("externeMessungsId", trans.get("messungs_ext_id"));
        builder.add("parentModified", TS1);
        builder.add("readonly", JsonValue.FALSE);
        builder.add("owner", JsonValue.TRUE);
        builder.add("statusEdit", JsonValue.TRUE);
        builder.add("status", ID1000);
        expectedById = builder.build();
        Assert.assertNotNull(expectedById);

        // Load probe object to test POST request
        create = readJsonResource("/datasets/messung.json");
        Assert.assertNotNull(create);
    }

    /**
     * Execute the tests.
     */
    public final void execute() {
        getAll("messung", "rest/messung");
        getById("messung", "rest/messung/1200", expectedById);
        JsonObject created = create("messung", "rest/messung", create);
        update("messung", "rest/messung/1200", "nebenprobenNr", "T100", "U200");
        delete(
            "messung",
            "rest/messung/" + created.getJsonObject("data").get("id"));
    }
}
