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

public class ZusatzwertTest extends ServiceTest {

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
            "treeModified"
        });

        // Prepare expected probe object
        JsonObject content = readJsonResource("/datasets/dbUnit_zusatzwert.json");
        JsonObject messung = content.getJsonArray("land.zusatz_wert").getJsonObject(0);
        JsonObjectBuilder builder = convertObject(messung);
        builder.add("parentModified", 1450371851654L);
        builder.add("readonly", JsonValue.FALSE);
        builder.add("owner", JsonValue.TRUE);
        expectedById = builder.build();
        Assert.assertNotNull(expectedById);

        // Load probe object to test POST request
        create = readJsonResource("/datasets/zusatzwert.json");
        Assert.assertNotNull(create);
    }

    public final void execute() {
        getAll("zusatzwert", "rest/zusatzwert");
        getById("zusatzwert", "rest/zusatzwert/1000", expectedById);
        JsonObject created = create("zusatzwert", "rest/zusatzwert", create);
        update("zusatzwert", "rest/zusatzwert/1000", "pzsId", "A77", "A78");
        delete("zusatzwert", "rest/zusatzwert/" + created.getJsonObject("data").get("id"));
    }
}
