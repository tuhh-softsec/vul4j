package de.intevation.lada.test.stamm;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.junit.Assert;

import de.intevation.lada.Protocol;
import de.intevation.lada.test.ServiceTest;

public class OrtTest extends ServiceTest {
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
        JsonObject content = readJsonResource("/datasets/dbUnit_ort.json");
        JsonObject erzeuger = content.getJsonArray("stammdaten.ort").getJsonObject(0);
        JsonObjectBuilder builder = convertObject(erzeuger);
        expectedById = builder.build();
        Assert.assertNotNull(expectedById);

        // Load object to test POST request
        create = readJsonResource("/datasets/ort.json");
        Assert.assertNotNull(create);
    }

    public final void execute() {
        getAll("ort", "rest/ort");
        getById("ort", "rest/ort/1000", expectedById);
        update("ort", "rest/ort/1000", "langtext", "Langer Text", "Längerer Text");
        JsonObject created = create("ort", "rest/ort", create);
        delete("ort", "rest/ort/" + created.getJsonObject("data").get("id"));
    }
}
