/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.exporter.csv;

import org.apache.log4j.Logger;

import de.intevation.lada.exporter.ExportJob;

public class CsvExportJob extends ExportJob{

    public CsvExportJob(String jobId) {
        super(jobId);
        this.format = "csv";
        this.downloadFileName = "export.csv";
        this.logger = Logger.getLogger(String.format("CsvExportJob[%s]", jobId));
    }
}