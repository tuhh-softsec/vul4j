/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.query;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;


/**
 * Utility class to handle the SQL query configuration.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class QueryTools
{
    private static String FILE = "/queryconf.json";
    /**
     * Read the config file using the system property
     * "de.intevation.lada.sqlconfig".
     *
     * @return The file content.
     */
    public static String readConfigFile() {
        try {
            InputStream inputStream = QueryConfig.class.getResourceAsStream(FILE);
            int ch;
            StringBuilder builder = new StringBuilder();
            while((ch = inputStream.read()) != -1) {
                builder.append((char)ch);
            }
            return builder.toString();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    /**
     * Get the configuration objects.
     * First reads the config file and creates {@link QueryConfig} objects
     * from JSON.
     *
     * @return List of {@link QueryConfig} objects.
     */
    public static List<QueryConfig> getConfig() {
        String content = readConfigFile();
        if (content == null) {
            return null;
        }
        List<QueryConfig> configs = new ArrayList<QueryConfig>();
        try {
            JsonReader reader = Json.createReader(new StringReader(content));
            JsonArray queries = reader.readArray();
            for (int i = 0; i < queries.size(); i++) {
                JsonObject query = queries.getJsonObject(i);
                QueryConfig qConf = new QueryConfig();
                qConf.setId(query.getString("id"));
                qConf.setName(query.getString("name"));
                qConf.setDescription(query.getString("description"));
                qConf.setSql(query.getString("sql"));
                JsonArray filters = query.getJsonArray("filters");
                List<QueryFilter> qFilters = new ArrayList<QueryFilter>();
                for (int j = 0; j < filters.size(); j++) {
                    JsonObject filter = filters.getJsonObject(j);
                    QueryFilter qFilter = new QueryFilter();
                    qFilter.setDataIndex(filter.getString("dataIndex"));
                    qFilter.setType(filter.getString("type"));
                    qFilter.setLabel(filter.getString("label"));
                    qFilter.setMultiSelect(filter.getBoolean("multiselect", false));
                    qFilters.add(qFilter);
                }
                qConf.setFilters(qFilters);
                JsonArray results = query.getJsonArray("result");
                List<ResultConfig> sResults = new ArrayList<ResultConfig>();
                for (int k = 0; k < results.size(); k++) {
                    JsonObject result = results.getJsonObject(k);
                    ResultConfig config = new ResultConfig();
                    config.setDataIndex(result.getString("dataIndex"));
                    config.setHeader(result.getString("header"));
                    config.setWidth(result.getInt("width", 100));
                    config.setFlex(result.getInt("flex", 0));
                    sResults.add(config);
                }
                qConf.setResults(sResults);
                configs.add(qConf);
            }
        }
        catch (JsonException e) {
            return null;
        }
        return configs;
    }

    /**
     * Get a query by id.
     * First reads the config file and returns the {@link QueryConfig}
     * identified by the given id.
     *
     * @param id {@link QueryConfig} id.
     * @return The query config as JSON object or null if no object was found.
     */
    public static JsonObject getQueryById(String id) {
        try {
            String content = readConfigFile();
            if (content != null) {
                JsonReader reader = Json.createReader(new StringReader(content));
                JsonArray queries = reader.readArray();
                for (int i = 0; i < queries.size(); i++) {
                    JsonObject query = queries.getJsonObject(i);
                    if (query.getString("id").equals(id)) {
                        return query;
                    }
                }
            }
            return null;
        }
        catch (JsonException e) {
            return null;
        }
    }
}
