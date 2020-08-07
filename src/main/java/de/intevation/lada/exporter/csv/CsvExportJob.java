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