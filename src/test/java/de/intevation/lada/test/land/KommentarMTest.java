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

public class KommentarMTest extends ServiceTest {

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
        JsonObject content = readJsonResource("/datasets/dbUnit_mkommentar.json");
        JsonObject messung = content.getJsonArray("land.kommentar_m").getJsonObject(0);
        JsonObjectBuilder builder = convertObject(messung);
        builder.add("parentModified", 1450371851654L);
        builder.add("readonly", JsonValue.FALSE);
        builder.add("owner", JsonValue.TRUE);
        expectedById = builder.build();
        Assert.assertNotNull(expectedById);

        // Load probe object to test POST request
        create = readJsonResource("/datasets/mkommentar.json");
        Assert.assertNotNull(create);
    }

    public final void execute() {
        getAll("mkommentar", "rest/mkommentar?messungsId=1000");
        getById("mkommentar", "rest/mkommentar/1000", expectedById);
        JsonObject created = create("mkommentar", "rest/mkommentar", create);
        update("mkommentar", "rest/mkommentar/1000", "text", "Testkommentar", "Testkommentar geändert");
        delete("mkommentar", "rest/mkommentar/" + created.getJsonObject("data").get("id"));
    }

}
