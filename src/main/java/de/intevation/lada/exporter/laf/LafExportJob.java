/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */

package de.intevation.lada.exporter.laf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.json.JsonNumber;
import javax.json.JsonValue;

import org.apache.log4j.Logger;

import de.intevation.lada.exporter.ExportJob;
import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.Probe;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Strings;

/**
 * Job class for exporting records to a laf file
 */
public class LafExportJob extends ExportJob {

    public LafExportJob(String jobId) {
        super(jobId);
        this.format = "laf";
        this.downloadFileName = "export.laf";
        this.logger = Logger.getLogger(String.format("LafExportJob[%s]", jobId));
    }

    /**
     * Start the export
     */
    public void run() {
        super.run();
        logger.debug(String.format("Starting LAF export", jobId));
        List<Integer> probeIds = new ArrayList<Integer>();
        List<Integer> messungIds = new ArrayList<Integer>();
        if (exportParameters.getJsonArray("proben") != null) {
            for (JsonValue id : exportParameters.getJsonArray("proben")) {
                if (id instanceof JsonNumber) {
                    probeIds.add(((JsonNumber)id).intValue());
                }
            }
        }
        if (exportParameters.getJsonArray("messungen") != null) {
            for (JsonValue id : exportParameters.getJsonArray("messungen")) {
                if (id instanceof JsonNumber) {
                    messungIds.add(((JsonNumber)id).intValue());
                }
            }
        }

        //Get probe and messung records
        List<Integer> pIds = new ArrayList<Integer>();
        if (!probeIds.isEmpty()) {
            QueryBuilder<Probe> pBuilder = new QueryBuilder<Probe>(
                repository.entityManager(Strings.LAND), Probe.class);
            pBuilder.andIn("id", probeIds);
            List<Probe> pObjects = repository.filterPlain(
                pBuilder.getQuery(), Strings.LAND);
            for (Probe p : pObjects) {
                pIds.add(p.getId());
            }
        }
        
        List<Integer> mIds = new ArrayList<Integer>();
        if (!messungIds.isEmpty()) {
            QueryBuilder<Messung> mBuilder = new QueryBuilder<Messung>(
                repository.entityManager(Strings.LAND), Messung.class);
            mBuilder.andIn("id", messungIds);
            List<Messung> mObjects = repository.filterPlain(
                mBuilder.getQuery(), Strings.LAND);
            for (Messung m : mObjects) {
                mIds.add(m.getId());
            }
        }

        //Export and write to file
        InputStream exported = exporter.export(pIds, mIds, encoding, userInfo);
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
        logger.debug(String.format("Finished LAF export"));
        setCurrentStatus(status.finished);
    }
}