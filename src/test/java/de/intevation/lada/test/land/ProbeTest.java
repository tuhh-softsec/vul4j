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
 * Test probe entities.
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class ProbeTest extends ServiceTest {

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
            "probeentnahmeBeginn",
            "solldatumBeginn",
            "solldatumEnde",
            "treeModified"
        });

        // Prepare expected probe object
        JsonObject content = readJsonResource("/datasets/dbUnit_probe.json");
        JsonObject probe = content.getJsonArray("land.probe").getJsonObject(0);
        JsonObjectBuilder builder = convertObject(probe);
        JsonObject trans =
            content.getJsonArray("land.probe_translation").getJsonObject(0);
        builder.add("externeProbeId", trans.get("probe_ext_id"));
        builder.add("mittelungsdauer", JsonValue.NULL);
        builder.add("probeentnahmeEnde", JsonValue.NULL);
        builder.add("erzeugerId", JsonValue.NULL);
        builder.add("mplId", JsonValue.NULL);
        builder.add("readonly", JsonValue.FALSE);
        builder.add("owner", JsonValue.TRUE);
        expectedById = builder.build();
        Assert.assertNotNull(expectedById);

        // Load probe object to test POST request
        create = readJsonResource("/datasets/probe.json");
        Assert.assertNotNull(create);
    }

    /**
     * Execute the tests.
     */
    public final void execute() {
        getAll("probe", "rest/probe");
        getById("probe", "rest/probe/1000", expectedById);
        filter("probe", "rest/probe?qid=4&mst_id=11010&umw_id=N24");
        JsonObject created = create("probe", "rest/probe", create);
        update(
            "probe",
            "rest/probe/1000",
            "hauptprobenNr",
            "120510002",
            "130510002");
        delete(
            "probe",
            "rest/probe/" + created.getJsonObject("data").get("id"));
    }
}
