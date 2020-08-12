/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.exporter.csv;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.json.JsonObject;

import org.apache.log4j.Logger;

import de.intevation.lada.exporter.QueryExportJob;
import de.intevation.lada.exporter.QueryExportJob.QueryExportException;
import de.intevation.lada.model.land.Messung;
import de.intevation.lada.query.QueryTools;

/**
 * Job class for exporting records to a CSV file
 *
 * @author <a href="mailto:awoestmann@intevation.de">Alexander Woestmann</a>
 */
public class CsvExportJob extends QueryExportJob{

    public CsvExportJob(String jobId, QueryTools queryTools) {
        super(jobId, queryTools);
        this.format = "csv";
        this.downloadFileName = "export.csv";
        this.logger = Logger.getLogger(String.format("CsvExportJob[%s]", jobId));
    }

    @Override
    protected List<Map<String, Object>> mergeSubData(List<?> subData) throws QueryExportException {
        List<Map<String, Object>> mergedData;
        switch (getSubDataType(idType)) {
            case "messung":
                mergedData = mergeMessungData((List<Messung>) subData);
                break;
            default: return null;
        }
        if (mergedData == null) {
            throw new QueryExportException("Failed merging subdata into query data");
        }
        return mergedData;
    }

    /**
     * Merge primary result and messung data
     * @param messungData Data to merge
     * @return Merged data as list
     */
    private List<Map<String, Object>> mergeMessungData(List<Messung> messungData) {
        //Create a map of id->record
        Map<Integer, Map<String, Object>> idMap = new HashMap<Integer, Map<String, Object>> ();
        primaryData.forEach(record -> {
            idMap.put((Integer) record.get(idType), record);
        });
        AtomicBoolean success = new AtomicBoolean(true);
        List<Map<String, Object>> merged = new ArrayList<Map<String, Object>>();
        messungData.forEach(messung -> {
            Integer primaryId = (Integer) getFieldByName(idType, messung);
            if (primaryId == null) {
                success.set(false);
                return;
            }
            Map<String, Object> mergedRow = new HashMap<String, Object>();
            //Add sub data
            subDataColumns.forEach(subDataColumn -> {
                Object fieldValue = getFieldByName(subDataColumn, messung);
                if (fieldValue == null) {
                    success.set(false);
                    return;
                }
                mergedRow.put(subDataColumn, fieldValue);
            });
            //Add primary record
            Map<String, Object> primaryRecord = idMap.get(primaryId);
            primaryRecord.forEach((key, value) -> {
                mergedRow.put(key, value);
            });
            merged.add(mergedRow);
        });
        if (!success.get()) {
            return null;
        }
        return null;
    }

    /**
     * Get the value of an object's field by calling its getter.
     * @param fieldName field name
     * @param object object
     * @return Field value
     */
    private Object getFieldByName(String fieldName, Object object) {

        String capitalizedName;
        String methodName = "";
        Method method;
        try {
            capitalizedName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            methodName = String.format("get%s", capitalizedName);
            method = object.getClass().getMethod(methodName);
            return method.invoke(object);
        } catch (NoSuchMethodException nsme) {
            logger.error(String.format("Can not get field %s for class %s", fieldName, object.getClass().toString()));
            return null;
        }
        catch (IllegalAccessException | InvocationTargetException exc) {
            logger.error(String.format("Can not call %s for class %s", methodName, object.getClass().toString()));
            return null;
        }
    }

    /**
     * Start the CSV export;
     */
    @Override
    public void run() {
        super.run();
        try {
            primaryData = getQueryResult();
        } catch (QueryExportException qee) {
            fail("Fetching primary data failed");
            return;
        }
        List<Map<String, Object>> exportData = primaryData;
        List<String> exportColumns = new ArrayList<String>();
        exportColumns.addAll(this.columnsToExport);
        if (exportSubdata) {
            try {
                exportData = mergeSubData(getSubData());
                exportColumns.addAll(subDataColumns);
            } catch (QueryExportException ee) {
                fail("Fetching export data failed");
                return;
            }
        }
        JsonObject exportOptions = exportParameters.getJsonObject("csvOtions");

        InputStream exported = exporter.export(exportData, encoding, exportOptions, exportColumns);
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = exported.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            String resultString = result.toString(encoding);
            if(!writeResultToFile(resultString)) {
                fail("Error on writing export result.");
                return;
            }

        } catch (IOException ioe) {
            logger.error(String.format("Error on writing export result. IOException: %s", ioe.getStackTrace().toString()));
            fail("Error on writing export result.");
            return;
        }
        logger.debug(String.format("Finished CSV export"));
        finish();
    }
}