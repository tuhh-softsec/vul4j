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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

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
import de.intevation.lada.model.stammdaten.GridColumnValue;
import de.intevation.lada.model.stammdaten.BaseQuery;
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
        return null;
    }
    /**
     * Execute query and return results.
     * @param customColumns Customized column configs, containing filter, sorting and references to the respective column.
     * @param qId Query id.
     * @return List of result maps.
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getResultForQuery(
        List<GridColumnValue> customColumns,
        Integer qId
    ) {
        QueryBuilder<BaseQuery> builder = new QueryBuilder<BaseQuery>(
            repository.entityManager(Strings.STAMM),
            BaseQuery.class
        );
        builder.and("id", qId);
        BaseQuery query = repository.filterPlain(builder.getQuery(), Strings.STAMM).get(0);

        String sql = query.getSql();

        List<GridColumn> columns = new ArrayList<GridColumn>();
        //Map containing all sort statements, sorted by sortIndex
        TreeMap<Integer, String> sortIndMap = new TreeMap<Integer, String>();

        String filterSql = "";
        String sortSql = "";

        for (GridColumnValue customColumn : customColumns) {

            //Build ORDER BY clause
            columns.add(customColumn.getGridColumn());
            if (customColumn.getSort() != null
                && !customColumn.getSort().isEmpty()) {

                String sortValue = customColumn.getGridColumn().getDataIndex() + " "
                        + customColumn.getSort() + " ";
                Integer key = customColumn.getSortIndex() != null ? customColumn.getSortIndex() : -1;
                String value = sortIndMap.get(key);
                value = value != null ? value + ", "  + sortValue : sortValue;
                sortIndMap.put(key, value);
            }

            if (customColumn.getFilterActive() != null
                    && customColumn.getFilterActive() == true) {
                //Build WHERE clause
                if (filterSql.isEmpty()) {
                    filterSql += " WHERE ";
                } else {
                    filterSql += " AND ";
                }
                Filter filter = customColumn.getGridColumn().getFilter();
                String filterValue = customColumn.getFilterValue();
                filterSql += filter.getSql().replace(":" + filter.getParameter(), filterValue) + " ";
            }
        }

        if (sortIndMap.size() > 0) {
            NavigableMap <Integer, String> orderedSorts = sortIndMap.tailMap(0, true);
            String unorderedSorts = sortIndMap.get(-1);
            sortSql += "";
            for (String sortString : orderedSorts.values()) {
                if (sortSql.isEmpty()){
                    sortSql += " ORDER BY " + sortString;
                } else {
                    sortSql += ", " + sortString;
                }
            }
            if (unorderedSorts!= null && !unorderedSorts.isEmpty()) {
                if (sortSql.isEmpty()){
                    sortSql += " ORDER BY " + unorderedSorts;
                } else {
                    sortSql += ", " + unorderedSorts;
                }
            }
        }

        if (!filterSql.isEmpty()){
            sql += filterSql + " ";
        }
        sql += sortSql + ";";
        javax.persistence.Query q = prepareQuery(
            sql,
            null,
            null,
            repository.entityManager(Strings.LAND));
        if (q == null) {
            return new ArrayList<>();
        }
        return prepareResult(q.getResultList(), columns);
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
        javax.persistence.Query query = manager.createNativeQuery(sql);
        /*for (Filter filter: filters) {

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

        }*/
        return query;

    }

    /**
     * Prepares the query result for the client,
     * @param result A list of query results
     * @param names The columns queried by the client
     * @return List of result maps, containing only the configured columns
     */
    public List<Map<String, Object>> prepareResult(
        List<Object[]> result,
        List<GridColumn> names
    ) {
        if (result.size() == 0) {
            return null;
        }
        List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
        for (Object[] row: result) {
            Map<String, Object> set = new HashMap<String, Object>();
            for (int i = 0; i < names.size(); i++) {
                set.put(names.get(i).getDataIndex(), row[names.get(i).getPosition() - 1]);
            }
            ret.add(set);
        }
        return ret;
    }
}