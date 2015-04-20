/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.importer.laf;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 * The LAFFormat reads the config file
 * (https://bfs-intern.intevation.de/Server/Importer) and creates format
 * objects for each entry.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class LafFormat
{
    private JsonObject fileContent;

    /**
     * Reads the config file.
     *
     * @param fileName  Path to the config file.
     * @return success
     */
    public boolean readConfigFile(String fileName) {
        try {
            InputStream inputStream =
                LafFormat.class.getResourceAsStream(fileName);
            int ch;
            StringBuilder builder = new StringBuilder();
            while((ch = inputStream.read()) != -1) {
                builder.append((char)ch);
            }
            JsonReader reader =
                Json.createReader(new StringReader(builder.toString()));
            fileContent = reader.readObject();
            return true;
        }
        catch (IOException ioe) {
            return false;
        }
        catch (JsonException je) {
            return false;
        }
    }

    /**
     * Returns a List of EntryFormat for the requested entity type.
     * The Entity type can be one of:
     * * "probe"
     * * "messung"
     * * "ort"
     *
     * @param dataType The entity type
     * @return List of entry formats defined for the requested type.
     */
    public List<EntryFormat> getFormat(String dataType) {
        List<EntryFormat> formats = new LinkedList<EntryFormat>();
        try {
            JsonArray block = fileContent.getJsonArray(dataType);
            for (int i = 0; i < block.size(); i++) {
                JsonObject jEntry = block.getJsonObject(i);
                EntryFormat entry = new EntryFormat();
                entry.setKey(jEntry.getString("key"));
                Pattern pattern =
                    Pattern.compile(
                        jEntry.getString("regex"),
                        Pattern.MULTILINE);
                entry.setPattern(pattern);
                entry.setDefaultValue(jEntry.get("default"));
                formats.add(entry);
            }
            return formats;
        }
        catch (JsonException e) {
            return null;
        }
    }
}
