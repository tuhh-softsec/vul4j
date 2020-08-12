package de.intevation.lada.exporter.json;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.intevation.lada.exporter.QueryExportJob;
import de.intevation.lada.query.QueryTools;

public class JsonExportJob extends QueryExportJob{

    public JsonExportJob(String jobId, QueryTools queryTools) {
        super(jobId, queryTools);
        this.format = "json";
        this.downloadFileName = "export.json";
        this.logger = Logger.getLogger(String.format("JsonExportJob[%s]", jobId));
    }

    @Override
    protected List<Map<String, Object>> mergeSubData(List<?> subData) throws QueryExportException {return null;}
}