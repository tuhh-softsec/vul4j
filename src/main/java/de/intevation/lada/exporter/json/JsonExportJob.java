package de.intevation.lada.exporter.json;

import org.apache.log4j.Logger;

import de.intevation.lada.exporter.ExportJob;

public class JsonExportJob extends ExportJob{

    public JsonExportJob(String jobId) {
        super(jobId);
        this.format = "json";
        this.downloadFileName = "export.json";
        this.logger = Logger.getLogger(String.format("JsonExportJob[%s]", jobId));
    }
    
}