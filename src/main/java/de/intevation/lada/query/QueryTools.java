/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.query;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.persistence.EntityManager;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.Logger;

import de.intevation.lada.model.stammdaten.Filter;
import de.intevation.lada.model.stammdaten.Query;
import de.intevation.lada.model.stammdaten.Result;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;


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

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getResultForQuery(
        MultivaluedMap<String, String> params,
        Integer qId,
        String type
    ) {
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
            return new ArrayList<>();
        }
        return prepareResult(q.getResultList(), results);
    }

    public List<Map<String, Object>> filterResult(
        String filter,
        List<Map<String, Object>> items
    ) {
        JsonReader jsonReader = Json.createReader(new StringReader(filter));
        JsonArray filters = jsonReader.readArray();
        jsonReader.close();
        List<Map<String, Object>> filtered = new ArrayList<>();
        for (Map<String, Object> entry : items) {
            int ndx = 0;
            boolean filtermatch = false;
            for (JsonValue f : filters) {
                JsonObject o = (JsonObject)f;
                JsonString property = o.getJsonString("property");
                JsonString value = o.getJsonString("value");
                if (property != null &&
                    value != null
                ) {
                    String p = property.toString().replaceAll("\"", "");
                    String v = value.toString().replaceAll("\"", "");
                    if (entry.containsKey(p) &&
                        entry.get(p).toString().contains(v) &&
                        (ndx == 0 || (ndx > 0 && filtermatch == true))
                    ) {
                        filtermatch = true;
                    }
                    else {
                        filtermatch = false;
                    }
                }
                ndx++;
            }
            if (filtermatch) {
                filtered.add(entry);
            }
        }
        return filtered;
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
                // replace multiSelect-delimiter set by ExtJS with
                // alternation metacharacter for PostgreSQL SIMILAR TO
                clean.add(p.replace(",", "|"));
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
}