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
import javax.json.JsonValue.ValueType;
import javax.persistence.EntityManager;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.Logger;

import de.intevation.lada.model.stammdaten.Filter;
import de.intevation.lada.model.stammdaten.GridColumn;
import de.intevation.lada.model.stammdaten.Query;
import de.intevation.lada.model.stammdaten.Result;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;


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

    @Inject
    private Logger logger;

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getResultForQuery(
        MultivaluedMap<String, String> params,
        Integer qId
    ) {
 /*       QueryBuilder<Query> builder = new QueryBuilder<Query>(
            repository.entityManager(Strings.STAMM),
            Query.class
        );
        builder.and("id", qId);
        Query query = repository.filterPlain(builder.getQuery(), Strings.STAMM).get(0);

        String sql = query.getSql();

        List<Filter> filters = new ArrayList();
        List<GridColumn> columns = query.getGridColumns();
        for (GridColumn column : columns) {
            filters.add(column.getFilter());
        }

        if (params.containsKey("sort")) {
            String sort = params.getFirst("sort");
            try (JsonReader reader = Json.createReader(new StringReader(sort))) {
                JsonObject sortProperties = reader.readArray().getJsonObject(0);
                sql += " ORDER BY ";
                sql += sortProperties.getJsonString("property").getString() + " ";
                sql += sortProperties.getJsonString("direction").getString();
            }
        }
        javax.persistence.Query q = prepareQuery(
            sql,
            filters,
            params,
            repository.entityManager(Strings.LAND));
        if (q == null) {
            return new ArrayList<>();
        }
        return prepareResult(q.getResultList(), results);
        */
        return null;
    }

    public List<Map<String, Object>> filterResult(
        String filter,
        List<Map<String, Object>> items
    ) {
        /*
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
                ValueType type = o.get("value").getValueType();
                if (type.equals(ValueType.ARRAY)) {
                    // Compare with array
                    JsonArray value = o.getJsonArray("value");
                    String p = property.toString().replaceAll("\"", "");
                    if (value != null &&
                        entry.containsKey(p) &&
                        entry.get(p) != null
                    ) {
                        for (JsonValue v : value) {
                            String filterValue = v.toString().replaceAll("\"", "");
                            if (entry.get(p).toString().contains(filterValue) &&
                                (ndx == 0 || (ndx > 0 && filtermatch == true))) {
                                filtermatch = true;
                            }
                            else {
                                filtermatch = false;
                            }
                        }
                    }
                }
                else {
                    JsonString value = o.getJsonString("value");
                    if (property != null &&
                        value != null
                    ) {
                        String p = property.toString().replaceAll("\"", "");
                        String v = value.toString().replaceAll("\"", "");
                        if (entry.containsKey(p) &&
                            entry.get(p) != null &&
                            entry.get(p).toString().contains(v) &&
                            (ndx == 0 || (ndx > 0 && filtermatch == true))
                        ) {
                            filtermatch = true;
                        }
                        else {
                            filtermatch = false;
                        }
                    }
                }
                ndx++;
            }
            if (filtermatch) {
                filtered.add(entry);
            }
        }
        return filtered;
*/
        return null;
    }

    public javax.persistence.Query prepareQuery(
        String sql,
        List<Filter> filters,
        MultivaluedMap<String, String> params,
        EntityManager manager
    ) {
        /*
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
        */
        return null;
    }

    public List<Map<String, Object>> prepareResult(
        List<Object[]> result,
        List<Result> names
    ) {
        /*
        List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
        for (Object[] row: result) {
            Map<String, Object> set = new HashMap<String, Object>();
            for (int i = 0; i < row.length; i++) {
                set.put(names.get(i).getDataIndex(), row[i]);
            }
            ret.add(set);
        }
        return ret;
        */
        return null;
    }
}