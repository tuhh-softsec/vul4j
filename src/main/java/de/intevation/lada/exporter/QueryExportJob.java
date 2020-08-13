/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.exporter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue.ValueType;

import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.Messwert;
import de.intevation.lada.model.land.StatusProtokoll;
import de.intevation.lada.model.stammdaten.Filter;
import de.intevation.lada.model.stammdaten.FilterType;
import de.intevation.lada.model.stammdaten.GridColumn;
import de.intevation.lada.model.stammdaten.GridColumnValue;
import de.intevation.lada.model.stammdaten.StatusKombi;
import de.intevation.lada.model.stammdaten.StatusStufe;
import de.intevation.lada.model.stammdaten.StatusWert;
import de.intevation.lada.query.QueryTools;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Strings;

/**
 * Abstract class for an export of query result.
 */
public abstract class QueryExportJob extends ExportJob {

    /**
     * True if subdata shall be fetched from the database and exported
     */
    protected boolean exportSubdata;

    /**
     * Sub data column names to export
     */
    protected List<String> subDataColumns;

    /**
     * Column containing the id
     */
    protected String idColumn;

    /**
     * Identifier type
     */
    protected String idType;

    /**
     * Ids of record that shall be exported
     */
    private Integer[] idsToExport;

    /**
     * Query result
     */
    protected List<GridColumnValue> columns;

    /**
     * Columns to use for export
     */
    protected List<String> columnsToExport;

    /**
     * Map of data types and the according sub data types
     */
    private Map<String, String> mapPrimaryToSubDataTypes;

    /**
     * Map id result types to filter sql statementsm for the id columns
     */
    private Map<String, String> dataTypeToIdFilterQuery = new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;
        {
            put("probeId", "probe.id");
            put("messungId", "messung.id");
            put("mpId", "messprogramm.id");
            put("ortId", "ort.id");
            put("dsatzerz", "datensatz_erzeuger.id");
            put("mprkat", "messprogramm_kategorie.id");
            put("probenehmer", "probenehmer.id");
    }};;

    /**
     * Query id
     */
    private Integer qId;

    /**
     * Query tools used to load query data
     */
    private QueryTools queryTools;

    /**
     * Primary data query result
     */
    protected List<Map<String, Object>> primaryData;

    /**
     * Constructor
     * @param jobId Job id
    * @param queryTools Query tools instance
     */
    public QueryExportJob (String jobId, QueryTools queryTools) {
        super(jobId);
        this.queryTools = queryTools;
        columns = new ArrayList <GridColumnValue>();
        columnsToExport = new ArrayList<String>();

        mapPrimaryToSubDataTypes = new HashMap<String, String>();
        mapPrimaryToSubDataTypes.put("probeId", "messung");
        mapPrimaryToSubDataTypes.put("messungId", "messwert");

    }

    /**
     * Creates a id list filter for the given dataIndex
     * @param dataType ID column data type, e.g. mpId
     * @return Filter object
     */
    private Filter createIdListFilter(String dataType) {

        //Get Filter type from db
        QueryBuilder<FilterType> builder = new QueryBuilder<FilterType>(repository.entityManager(Strings.STAMM), FilterType.class);
        builder.and("type", "listnumber");
        FilterType filterType = repository.filterPlain(builder.getQuery(), Strings.STAMM).get(0);

        //Create filter object
        String parameter = dataType + "s";
        Filter filter = new Filter();
        filter.setFilterType(filterType);
        filter.setParameter(parameter);
        filter.setSql(String.format("%s in ( :%s )", dataTypeToIdFilterQuery.get(dataType), parameter));
        return filter;
    }

    /**
     * Get the value of an object's field by calling its getter.
     * @param fieldName field name
     * @param object object
     * @return Field value
     */
    protected Object getFieldByName(String fieldName, Object object) {

        String capitalizedName;
        String methodName = "";
        Method method;
        try {
            capitalizedName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            methodName = "get" + capitalizedName;
            method = object.getClass().getMethod(methodName);
            return method.invoke(object);
        } catch (NoSuchMethodException nsme) {
            logger.error(String.format("Can not get field %s(%s) for class %s", fieldName, methodName, object.getClass().toString()));
            return null;
        }
        catch (IllegalAccessException | InvocationTargetException exc) {
            logger.error(String.format("Can not call %s for class %s", methodName, object.getClass().toString()));
            return null;
        }
    }

    /**
     * Execute the query.
     * @throws QueryExportException Thrown if loading the query data fails
     * @return Query result as list
     */
    protected List<Map<String, Object>> getQueryResult() throws QueryExportException{
        try {
            List<Map<String, Object>> result = queryTools.getResultForQuery(columns, qId);
            logger.debug(String.format("Fetched %d primary records", result.size()));
            return result;
        } catch (Exception e) {
            logger.error("Failed loading query result");
            e.printStackTrace();
            throw new QueryExportException("Failed loading query result");
        }
    }

    /**
     * Get the sub data for the query
     * @throw QueryExportException Thrown if fetching subdata fails
     * @return Query result as list
     */
    protected List<?> getSubData() throws QueryExportException{
        if (primaryData == null) {
            return null;
        }
        //Get ids of primary records
        List<Integer> primaryDataIds = new ArrayList<Integer>();
        primaryData.forEach(item -> {
            primaryDataIds.add((Integer) item.get(idColumn));
        });

        //Get subdata
        String subDataType = mapPrimaryToSubDataTypes.get(idType);
        if (subDataType == null) {
            throw new QueryExportException(String.format("Unknown id type: %s", idType));
        }
        switch (subDataType) {
            case "messung": return getMessungSubData(primaryDataIds);
            case "messwert": return getMesswertSubData(primaryDataIds);
            default: return null;
        }
    }

    /**
     * Load messung data filtered by the given ids.
     * @param primaryDataIds Ids to filter for
     * @return Messwert records as list
     */
    private List<Messung> getMessungSubData(List<Integer> primaryDataIds) {
        QueryBuilder<Messung> messungBuilder = new QueryBuilder<Messung>(
            repository.entityManager(Strings.LAND), Messung.class);
        messungBuilder.andIn("probeId", primaryDataIds);
        return repository.filterPlain(messungBuilder.getQuery(), Strings.LAND);
    }

    /**
     * Load messwert data filtered by the given ids.
     * @param primaryDataIds Ids to filter for
     * @return Messwert records as list
     */
    private List<Messwert> getMesswertSubData(List<Integer> primaryDataIds) {
        QueryBuilder<Messwert> messwertBuilder = new QueryBuilder<Messwert>(
            repository.entityManager(Strings.LAND), Messwert.class);
        messwertBuilder.andIn("messungsId", primaryDataIds);
        return repository.filterPlain(messwertBuilder.getQuery(), Strings.LAND);
    }

    protected String getStatusString(Messung messung) {
        StatusProtokoll protokoll = repository.getByIdPlain(StatusProtokoll.class, messung.getStatus(), Strings.LAND);
        StatusKombi kombi = repository.getByIdPlain(StatusKombi.class, protokoll.getStatusKombi(), Strings.STAMM);
        StatusStufe stufe = kombi.getStatusStufe();
        StatusWert wert = kombi.getStatusWert();
        return String.format("%s - %s", stufe.getStufe(), wert.getWert());
    }

    protected int getMesswertCount(Messung messung) {
        QueryBuilder<Messwert> builder = new QueryBuilder<Messwert>(
            repository.entityManager(Strings.LAND), Messwert.class
        );
        builder.and("messungsId", messung.getId());
        return repository.filterPlain(builder.getQuery(), Strings.LAND).size();
    }

    /**
     * Get the sub data type to the given primary data type.
     * @param primaryDataType Primary data type
     * @return Sub data type as String
     */
    protected String getSubDataType(String primaryDataType) {
        return mapPrimaryToSubDataTypes.get(primaryDataType);
    }

    /**
     * Merge sub data into the primary query result
     * @param subData Data to merge into result
     * @throws QueryExportException Thrown if merging fails
     * @return Merge data as list
     */
    protected abstract List<Map<String, Object>> mergeSubData(List<?> subData) throws QueryExportException;

    /**
     * Parse export parameters
     */
    protected void parseExportParameters(){
        if (exportParameters == null) {
            return;
        }
        //Check if subdata shall be exported
        exportSubdata = exportParameters.getBoolean("exportSubData");
        //Get identifier type
        idColumn = exportParameters.getString("idField");

        //Check if sub data columns are present if subdata is exported
        if (exportSubdata
            && !exportParameters.containsKey("subDataColumns")
            && exportParameters.get("subDataColumns") != null) {
            throw new IllegalArgumentException("Subdata is export but not subdata columns are present");
        }

        //Get sub data columns
        if (exportSubdata && exportParameters.containsKey("subDataColumns")) {
            subDataColumns = new ArrayList<String>();
            JsonArray columnJson = exportParameters.getJsonArray("subDataColumns");
            int columnCount = columnJson.size();
            for (int i = 0; i < columnCount; i++) {
                subDataColumns.add(columnJson.getString(i));
            }
        }
        ArrayList<Integer> idFilterList = new ArrayList<Integer>();
        JsonArray idJsonArray = exportParameters.getJsonArray("idFilter");
        int idJsonArrayCount = idJsonArray.size();
        for (int i = 0; i< idJsonArrayCount; i++) {
            idFilterList.add(idJsonArray.getInt(i));
        }

        idsToExport = new Integer[idFilterList.size()];
        idFilterList.toArray(idsToExport);

        exportParameters.getJsonArray("columns").forEach(jsonValue -> {
            JsonObject columnObj = (JsonObject) jsonValue;
            GridColumnValue columnValue = new GridColumnValue();
            GridColumn gridColumn;
            columnValue.setgridColumnId(columnObj.getInt("gridColumnId"));
            String sort = columnObj.get("sort") != null 
                && columnObj.get("sort").getValueType() == ValueType.STRING ?
                columnObj.getString("sort"): null;
            columnValue.setSort(sort);
            Integer sortIndex = columnObj.get("sortIndex") != null 
                && columnObj.get("sortIndex").getValueType() == ValueType.NUMBER ?
                columnObj.getInt("sortIndex"): null;
            columnValue.setSortIndex(sortIndex);
            columnValue.setFilterValue(columnObj.getString("filterValue"));
            columnValue.setFilterActive(columnObj.getBoolean("filterActive"));
            gridColumn = repository.getByIdPlain(
                GridColumn.class,
                columnValue.getGridColumnId(),
                Strings.STAMM);

            columnValue.setGridColumn(gridColumn);
            //Check if the column contains the id
            if (columnValue.getGridColumn().getDataIndex().equals(idColumn)) {
                //Get the column type
                idType = gridColumn.getDataType().getName();
                if (idsToExport != null && idsToExport.length > 0) {
                    //Get query result type
                    String dataType = gridColumn.getDataType().getName();
                    Filter filter = createIdListFilter(dataType);
                    gridColumn.setFilter(filter);
                    columnValue.setFilterActive(true);
                    StringBuilder filterValue = new StringBuilder();
                    for (int i = 0; i < idsToExport.length; i++) {
                        filterValue.append(idsToExport[i]);
                        if (i != idsToExport.length -1) {
                            filterValue.append(",");
                        }
                    }
                    columnValue.setFilterValue(filterValue.toString());
                    columnValue.setFilterIsNull(false);
                    columnValue.setFilterNegate(false);
                    columnValue.setFilterRegex(false);
                }

            }
            columns.add(columnValue);
            if (columnObj.getBoolean("export")) {
                columnsToExport.add(columnValue.getGridColumn().getDataIndex());
            }
        });

        if (columns.size() == 0 || columnsToExport.size() == 0) {
            throw new IllegalArgumentException("No columns to export given");
        }

        //Get query id
        GridColumn gridColumn = repository.getByIdPlain(
            GridColumn.class,
            Integer.valueOf(columns.get(0).getGridColumnId()),
        Strings.STAMM);
        qId = gridColumn.getBaseQuery();
    }

    @Override
    public void run() {
        super.run();
    }

    public static class QueryExportException extends Exception {
        private static final long serialVersionUID = 1L;

        public QueryExportException(String msg) {
            super(msg);
        }
    }
}