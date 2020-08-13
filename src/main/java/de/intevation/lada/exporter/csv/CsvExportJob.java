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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.json.JsonObject;

import org.apache.log4j.Logger;

import de.intevation.lada.exporter.QueryExportJob;
import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.Messwert;
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
    @SuppressWarnings("unchecked")
    protected List<Map<String, Object>> mergeSubData(List<?> subData) throws QueryExportException {
        List<Map<String, Object>> mergedData;
        logger.debug(String.format("Merging %d sub data records into %d primary record(s)", subData.size(), primaryData.size()));
        switch (getSubDataType(idType)) {
            case "messung":
                mergedData = mergeMessungData((List<Messung>) subData);
                break;
            case "messwert":
                mergedData = mergeMesswertData((List<Messwert>) subData);
                break;
            default: return null;
        }
        if (mergedData == null) {
            throw new QueryExportException("Failed merging subdata into query data");
        }
        return mergedData;
    }

    /**
     * Merge records without sub data
     * @param objects Record list
     * @param ids list of ids to merge
     * @param subDataColumns Subdata columns
     * @param primaryColumns primary data columns
     * @return
     */
    private List<Map<String, Object>> mergeDataWithEmptySubdata(
        Map<Integer, Map<String, Object>> objects, List<Integer> ids,
        List<String> subDataColumns, List<String> primaryColumns) {

        List<Map<String, Object>> merged = new ArrayList<Map<String, Object>>();
        ids.forEach(id -> {
            Map<String, Object> mergedRow = new HashMap<String, Object>();
            subDataColumns.forEach(column -> {
                mergedRow.put(column, null);
            });
            Map<String, Object> primaryRecord = objects.get(id);
            primaryColumns.forEach(column -> {
                mergedRow.put(column, primaryRecord.get(column));
            });
            merged.add(mergedRow);
        });
        return merged;
    }

    /**
     * Merge primary result and messung data
     * @param messungData Data to merge
     * @return Merged data as list
     */
    private List<Map<String, Object>> mergeMessungData(List<Messung> messungData) {
        //Create a map of id->record
        Map<Integer, Map<String, Object>> idMap = new HashMap<Integer, Map<String, Object>> ();
        //Ids left for merging
        List<Integer> idsLeft = new ArrayList<Integer>();
        primaryData.forEach(record -> {
            idMap.put((Integer) record.get(idColumn), record);
            idsLeft.add((Integer) record.get(idColumn));
        });

        AtomicBoolean success = new AtomicBoolean(true);
        List<Map<String, Object>> merged = new ArrayList<Map<String, Object>>();
        messungData.forEach(messung -> {
            Integer primaryId = messung.getProbeId();
            if (primaryId == null) {
                logger.error("No primary id set");
                success.set(false);
                return;
            }
            Map<String, Object> mergedRow = new HashMap<String, Object>();
            //Add sub data
            subDataColumns.forEach(subDataColumn -> {
                Object fieldValue = null;
                //Check if column needs seperate handling or is a valid messung field
                switch (subDataColumn) {
                    case "statusKombi":
                        fieldValue = getStatusString(messung);
                        break;
                    case "messwerteCount":
                        fieldValue = getMesswertCount(messung);
                        break;
                    default:
                        fieldValue = getFieldByName(subDataColumn, messung);
                }
                mergedRow.put(subDataColumn, fieldValue);
            });
            //Add primary record
            Map<String, Object> primaryRecord = idMap.get(primaryId);
            primaryRecord.forEach((key, value) -> {
                mergedRow.put(key, value);
            });
            //Remove finished record from list
            idsLeft.remove(primaryId);
            merged.add(mergedRow);
        });

        //Merge any skipped records without sub data
        merged.addAll(mergeDataWithEmptySubdata(idMap, idsLeft, subDataColumns, columnsToExport));
        if (!success.get()) {
            return null;
        }
        return merged;
    }

    /**
     * Merge primary result and messung data
     * @param messwertData Data to merge
     * @return Merged data as list
     */
    private List<Map<String, Object>> mergeMesswertData(List<Messwert> messwertData) {
        //Create a map of id->record
        Map<Integer, Map<String, Object>> idMap = new HashMap<Integer, Map<String, Object>> ();
        //Ids left for merging
        List<Integer> idsLeft = new ArrayList<Integer>();
        primaryData.forEach(record -> {
            idMap.put((Integer) record.get(idColumn), record);
            idsLeft.add((Integer) record.get(idColumn));
        });
        AtomicBoolean success = new AtomicBoolean(true);
        List<Map<String, Object>> merged = new ArrayList<Map<String, Object>>();
        messwertData.forEach(messwert -> {
            Integer primaryId = messwert.getMessungsId();
            if (primaryId == null) {
                logger.error("No primary id set");
                success.set(false);
                return;
            }
            Map<String, Object> mergedRow = new HashMap<String, Object>();
            //Add sub data
            subDataColumns.forEach(subDataColumn -> {
                Object fieldValue = null;
                //Check if column needs seperate handling or is a valid messwert field
                switch (subDataColumn) {
                    case "messungId":
                        fieldValue = getFieldByName("messungsId", messwert);
                        break;
                    default:
                        fieldValue = getFieldByName(subDataColumn, messwert);
                }
                mergedRow.put(subDataColumn, fieldValue);
            });
            //Add primary record
            Map<String, Object> primaryRecord = idMap.get(primaryId);
            if (primaryRecord == null) {
                logger.error("Can not get primary record for merging");
                success.set(false);
                return;
            }
            primaryRecord.forEach((key, value) -> {
                mergedRow.put(key, value);
            });
            //Remove finished record from list
            idsLeft.remove(primaryId);
            merged.add(mergedRow);
        });
        //Merge any skipped records without sub data
        merged.addAll(mergeDataWithEmptySubdata(idMap, idsLeft, subDataColumns, columnsToExport));

        if (!success.get()) {
            return null;
        }
        return merged;
    }

    /**
     * Start the CSV export;
     */
    @Override
    public void run() {
        super.run();
        //Check encoding
        if (!isEncodingValid()) {
            String error = String.format("Invalid encoding: %s", this.encoding);
            fail(error);
            return;
        }
        try {
            parseExportParameters();
        } catch (Exception e) {
            logger.error(String.format("Error parsing export parameters"));
            e.printStackTrace();
            fail("Error parsing export parameters");
            return;
        }

        //Fetch primary records
        try {
            primaryData = getQueryResult();
        } catch (QueryExportException qee) {
            fail("Fetching primary data failed");
            return;
        }
        List<Map<String, Object>> exportData = primaryData;
        ArrayList<String> exportColumns = new ArrayList<String>();
        exportColumns.addAll(this.columnsToExport);

        //If needed, fetch and merge sub data
        if (exportSubdata) {
            try {
                exportData = mergeSubData(getSubData());
                exportColumns.addAll(subDataColumns);
            } catch (QueryExportException ee) {
                logger.error(ee.getMessage());
                fail("Fetching export sub data failed");
                return;
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
                fail("Error on fetching sub data");
                return;
            }
        }

        //Export data to csv
        JsonObject exportOptions = exportParameters.getJsonObject("csvOptions");
        if (exportData == null || exportData.size() == 0) {
            fail("Export data is empty");
            return;
        }
        InputStream exported;
        try {
            exported = exporter.export(exportData, encoding, exportOptions, exportColumns);
        } catch (Exception e) {
            logger.error("Error writing csv");
            e.printStackTrace();
            fail("Error creating csv");
            return;
        }
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
            logger.error("Error on writing export result. IOException");
            ioe.printStackTrace();
            fail("Error on writing export result.");
            return;
        }
        logger.debug(String.format("Finished CSV export"));
        finish();
    }
}