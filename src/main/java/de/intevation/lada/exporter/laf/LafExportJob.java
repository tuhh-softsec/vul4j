/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */

package de.intevation.lada.exporter.laf;

import de.intevation.lada.exporter.ExportJob;

/**
 * Job class for exporting records to a laf file
 */
public class LafExportJob extends ExportJob {

    public LafExportJob(String jobId) {
        super(jobId);
        this.format = "laf";
        this.downloadFileName = "export.laf";
    }

    public void run() {
        super.run();
        //TODO: Implement export
    }
}