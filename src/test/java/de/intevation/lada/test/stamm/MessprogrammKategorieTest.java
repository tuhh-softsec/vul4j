package de.intevation.lada.test.stamm;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import org.junit.Assert;

import de.intevation.lada.Protocol;
import de.intevation.lada.test.ServiceTest;

public class MessprogrammKategorieTest extends ServiceTest {
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
        JsonObject content = readJsonResource("/datasets/dbUnit_messprogrammkategorie.json");
        JsonObject erzeuger = content.getJsonArray("stammdaten.messprogramm_kategorie").getJsonObject(0);
        JsonObjectBuilder builder = convertObject(erzeuger);
        expectedById = builder.build();
        Assert.assertNotNull(expectedById);

        // Load object to test POST request
        create = readJsonResource("/datasets/messprogrammkategorie.json");
        Assert.assertNotNull(create);
    }

    public final void execute() {
        getAll("messprogrammkategorie", "rest/messprogrammkategorie");
        getById("messprogrammkategorie", "rest/messprogrammkategorie/1000", expectedById);
        update("messprogrammkategorie", "rest/messprogrammkategorie/1000", "bezeichnung", "Testbezeichnung", "geändert");
        JsonObject created = create("messprogrammkategorie", "rest/messprogrammkategorie", create);
        delete("messprogrammkategorie", "rest/messprogrammkategorie/" + created.getJsonObject("data").get("id"));
    }
}
