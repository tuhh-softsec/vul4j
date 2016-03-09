/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.query;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.persistence.EntityManager;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.Logger;

import de.intevation.lada.model.stamm.Filter;
import de.intevation.lada.model.stamm.Query;
import de.intevation.lada.model.stamm.Result;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.Response;


/**
 * Utility class to handle the SQL query configuration.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class QueryTools
{

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    private static String PROBE_CONFIG = "/probequery.json";
    private static String MESSPROGRAMM_CONFIG = "/messprogrammquery.json";
    private static String STAMMDATEN_CONFIG = "/stammdatenquery.json";

    @Inject
    private Logger logger;

    /**
     * Read the config file using the system property
     * "de.intevation.lada.sqlconfig".
     *
     * @return The file content.
     */
    public static String readConfigFile(String file) {
        try {
            InputStream inputStream = QueryConfig.class.getResourceAsStream(file);
            Scanner scanner = new Scanner(inputStream, "UTF-8");
            scanner.useDelimiter("\\A");
            String configString = scanner.next();
            scanner.close();
            return configString;
        }
        catch (Exception ioe) {
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
    private static List<QueryConfig> getConfig(String file) {
        String content = readConfigFile(file);
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
                qConf.setType(query.getString("type"));
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

    public static List<QueryConfig> getProbeConfig() {
        return getConfig(PROBE_CONFIG);
    }

    public static List<QueryConfig> getMessprogrammConfig() {
        return getConfig(MESSPROGRAMM_CONFIG);
    }

    public static List<QueryConfig> getStammdatenConfig() {
        return getConfig(STAMMDATEN_CONFIG);
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
            String content = readConfigFile(PROBE_CONFIG);
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

    public List<Map<String, Object>> getResultForQuery(MultivaluedMap<String, String> params, Integer qId, String type) {
        QueryBuilder<Query> builder = new QueryBuilder<Query>(
            repository.entityManager("stamm"),
            Query.class
        );
        builder.and("id", qId);
        Query query = repository.filterPlain(builder.getQuery(), "stamm").get(0);
        if (!query.getType().equals(type)) {
            return null;
        }

        String sql = query.getSql();

        List<Filter> filters = query.getFilters();
        QueryBuilder<Result> rBuilder = new QueryBuilder<Result>(
            repository.entityManager("stamm"),
            Result.class
        );
        rBuilder.and("query", qId);
        rBuilder.orderBy("index", true);
        List<Result> results = repository.filterPlain(rBuilder.getQuery(), "stamm");
        Result idResult = new Result();
        idResult.setDataIndex("id");
        results.add(0, idResult);
        if (params.containsKey("sort")) {
            String sort = params.getFirst("sort");
            logger.debug("Sort parameter: " + sort);
            JsonReader reader = Json.createReader(new StringReader(sort));
            JsonObject sortProperties = reader.readArray().getJsonObject(0);
            sql += " ORDER BY ";
            sql += sortProperties.getJsonString("property").getString() + " ";
            sql += sortProperties.getJsonString("direction").getString();
        }
        javax.persistence.Query q = prepareQuery(
            sql,
            filters,
            params,
            repository.entityManager("land"));
        if (q == null) {
            return new ArrayList();
        }
        return prepareResult(q.getResultList(), results);
    }

    public javax.persistence.Query prepareQuery(
        String sql,
        List<Filter> filters,
        MultivaluedMap<String, String> params,
        EntityManager manager
    ) {
        javax.persistence.Query query = manager.createNativeQuery(sql);
        for (Filter filter: filters) {
            List<String> param = params.get(filter.getDataIndex());
            if (param == null) {
                return null;
            }
            List<String> clean = new ArrayList<String>();
            for(String p : param) {
                p = p.trim();
                clean.add(p.replace(" ", "|"));
            }
            query.setParameter(filter.getDataIndex(), clean);
        }
        return query;
    }

    public List<Map<String, Object>> prepareResult(
        List<Object[]> result,
        List<Result> names
    ) {
        List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
        for (Object[] row: result) {
            Map<String, Object> set = new HashMap<String, Object>();
            for (int i = 0; i < row.length; i++) {
                set.put(names.get(i).getDataIndex(), row[i]);
            }
            ret.add(set);
        }
        return ret;
    }

    public static JsonObject getMpQueryById(String id) {
        try {
            String content = readConfigFile(MESSPROGRAMM_CONFIG);
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
