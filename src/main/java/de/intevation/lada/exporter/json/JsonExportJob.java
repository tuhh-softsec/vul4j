/* Copyright (C) 2020 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.exporter.json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.json.Json;
import javax.json.JsonObject;

import org.apache.log4j.Logger;

import de.intevation.lada.exporter.QueryExportJob;
import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.Messwert;
import de.intevation.lada.query.QueryTools;

/**
 * Job class for exporting records to a JSON file.
 *
 * @author <a href="mailto:awoestmann@intevation.de">Alexander Woestmann</a>
 */
public class JsonExportJob extends QueryExportJob {

    private static final int LENGTH = 1024;
    private String subDataJsonKey;

    public JsonExportJob(String jobId, QueryTools queryTools) {
        super(jobId, queryTools);
        this.format = "json";
        this.downloadFileName = "export.json";
        this.logger =
            Logger.getLogger(String.format("JsonExportJob[%s]", jobId));
    }

    /**
     * Merge sub data into the primary query result
     *
     * For JSON export, the sub data records will be inserted as an array into
     * the corresponding primary record.
     * @param subData Data to merge into result
     * @throws QueryExportException Thrown if merging fails
     * @return Merged data as list
     */
    @Override
    @SuppressWarnings("unchecked")
    protected List<Map<String, Object>> mergeSubData(
        List<?> subData
    ) throws QueryExportException {
        List<Map<String, Object>> mergedData;
        logger.debug(
            String.format(
                "Merging %d sub data records into %d primary record(s)",
                subData.size(),
                primaryData.size()));
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
            throw new QueryExportException(
                "Failed merging subdata into query data");
        }
        return mergedData;
    }

    /**
     * Merge primary result and messung data.
     * @param messungData Data to merge
     * @return Merged data as list
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> mergeMessungData(
        List<Messung> messungData
    ) {
        // Create a map of id->record
        Map<Integer, Map<String, Object>> idMap =
            new HashMap<Integer, Map<String, Object>>();
        String sDataJsonKey = "messungen";
        primaryData.forEach(record -> {
            idMap.put((Integer) record.get(idColumn), record);
        });

        AtomicBoolean success = new AtomicBoolean(true);
        List<Map<String, Object>> merged = primaryData;
        messungData.forEach(messung -> {
            Integer primaryId = messung.getProbeId();
            if (primaryId == null) {
                logger.error("No primary id set");
                success.set(false);
                return;
            }
            Map<String, Object> mergedMessung = new HashMap<String, Object>();
            // Add sub data
            subDataColumns.forEach(subDataColumn -> {
                Object fieldValue = null;
                // Check if column needs seperate handling or is a valid
                // messung field
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
                mergedMessung.put(subDataColumn, fieldValue);
            });
            //Append messung to probe
            Map<String, Object> primaryRecord = idMap.get(primaryId);
            if (primaryRecord.get(sDataJsonKey) == null) {
                primaryRecord.put(sDataJsonKey, new ArrayList<Object>());
            }
            ArrayList<Map<String, Object>> messungenList =
                (ArrayList<Map<String, Object>>) primaryRecord.get("messungen");
            messungenList.add(mergedMessung);
        });
        if (!success.get()) {
            return null;
        }
        this.subDataJsonKey = sDataJsonKey;
        return merged;
    }

    /**
     * Merge primary result and messung data.
     * @param messwertData Data to merge
     * @return Merged data as list
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> mergeMesswertData(
        List<Messwert> messwertData
    ) {
        // Create a map of id->record
        Map<Integer, Map<String, Object>> idMap =
            new HashMap<Integer, Map<String, Object>>();
        String sDataJsonKey = "messwerte";
        primaryData.forEach(record -> {
            idMap.put((Integer) record.get(idColumn), record);
        });

        AtomicBoolean success = new AtomicBoolean(true);
        List<Map<String, Object>> merged = primaryData;
        messwertData.forEach(messwert -> {
            Integer primaryId = messwert.getMessungsId();
            if (primaryId == null) {
                logger.error("No primary id set");
                success.set(false);
                return;
            }
            Map<String, Object> mergedMesswert = new HashMap<String, Object>();
            // Add sub data
            subDataColumns.forEach(subDataColumn -> {
                Object fieldValue = null;
                // Check if column needs seperate handling or is a valid
                // messung field
                switch (subDataColumn) {
                    case "messungId":
                        fieldValue = getFieldByName("messungsId", messwert);
                        break;
                    default:
                        fieldValue = getFieldByName(subDataColumn, messwert);
                }
                mergedMesswert.put(subDataColumn, fieldValue);
            });
            //Append messung to probe
            Map<String, Object> primaryRecord = idMap.get(primaryId);
            if (primaryRecord.get(sDataJsonKey) == null) {
                primaryRecord.put(sDataJsonKey, new ArrayList<Object>());
            }
            ArrayList<Map<String, Object>> messwertList =
                (ArrayList<Map<String, Object>>) primaryRecord.get("messwerte");
            messwertList.add(mergedMesswert);
        });
        if (!success.get()) {
            return null;
        }
        this.subDataJsonKey = sDataJsonKey;
        return merged;
    }


    @Override
    public void run() {
        super.run();
        // Check encoding
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

        // Fetch primary records
        try {
            primaryData = getQueryResult();
        } catch (QueryExportException qee) {
            fail("Fetching primary data failed");
            return;
        }
        List<Map<String, Object>> exportData = primaryData;
        ArrayList<String> exportColumns = new ArrayList<String>();
        exportColumns.addAll(this.columnsToExport);

        // If needed, fetch and merge sub data
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

        //Export data to json
        if (exportData == null || exportData.size() == 0) {
            fail("Export data is empty");
            return;
        }
        InputStream exported;
        JsonObject exportOptions = Json.createObjectBuilder()
            .add("id", idColumn)
            .add("subData", exportSubdata ? subDataJsonKey : "")
            .add("timezone", timezone)
            .build();
        try {
            exported = exporter.export(
                exportData, encoding, exportOptions, exportColumns);
        } catch (Exception e) {
            logger.error("Error creating json");
            e.printStackTrace();
            fail("Error creating json");
            return;
        }
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[LENGTH];
            int length;
            while ((length = exported.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            String resultString = result.toString(encoding);
            if (!writeResultToFile(resultString)) {
                fail("Error on writing export result.");
                return;
            }

        } catch (IOException ioe) {
            logger.error("Error on writing export result. IOException");
            ioe.printStackTrace();
            fail("Error on writing export result.");
            return;
        }
        logger.debug(String.format("Finished JSON export"));
        finish();
    }
}
