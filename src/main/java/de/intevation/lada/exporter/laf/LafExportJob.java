/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */

package de.intevation.lada.exporter.laf;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.json.JsonNumber;
import javax.json.JsonValue;

import de.intevation.lada.exporter.ExportConfig;
import de.intevation.lada.exporter.ExportFormat;
import de.intevation.lada.exporter.ExportJob;
import de.intevation.lada.exporter.Exporter;
import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.Probe;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;

/**
 * Job class for exporting records to a laf file
 */
public class LafExportJob extends ExportJob {

    /**
     * The exporter.
     */
    @Inject
    @ExportConfig(format=ExportFormat.LAF)
    private Exporter exporter;

    /**
     * The data repository granting read-only access.
     */
    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    public LafExportJob(String jobId) {
        super(jobId);
        this.format = "laf";
        this.downloadFileName = "export.laf";
    }

    /**
     * Start the export
     */
    public void run() {
        logger.debug(String.format("Jobid %s: Starting LAF export", jobId));
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

        InputStream exported = exporter.export(pIds, mIds, encoding, userInfo);
        try {
            byte[] buffer = new byte[exported.available()];
            String resultString = new String(buffer, encoding);
            if(!writeResultToFile(resultString)) {
                fail("Error on writing export result.");
            }

        } catch (IOException ioe) {
            logger.error(String.format("Jobid %s: Error on writing export result. IOException: %s", jobId, ioe.getStackTrace()));
            fail("Error on writing export result.");
        }
        logger.debug(String.format("Jobid %s: Finished LAF export", jobId));
        super.run();
    }
}