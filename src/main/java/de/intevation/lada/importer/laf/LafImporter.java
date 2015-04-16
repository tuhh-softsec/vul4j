package de.intevation.lada.importer.laf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.intevation.lada.importer.ImportConfig;
import de.intevation.lada.importer.ImportFormat;
import de.intevation.lada.importer.Importer;
import de.intevation.lada.importer.ReportItem;
import de.intevation.lada.util.auth.UserInfo;

@ImportConfig(format=ImportFormat.LAF)
@Stateless
public class LafImporter implements Importer {

    @Inject
    private Logger logger;

    @Inject
    private LafParser parser;

    private Map<String, List<ReportItem>> warnings;
    private Map<String, List<ReportItem>> errors;

    /**
     * Default constructor.
     */
    public LafImporter() {
        warnings = new HashMap<String, List<ReportItem>>();
        errors = new HashMap<String, List<ReportItem>>();
    }

    /**
     * @return the warnings
     */
    @Override
    public Map<String, List<ReportItem>> getWarnings() {
        return warnings;
    }

    /**
     * @return the errors
     */
    @Override
    public Map<String, List<ReportItem>> getErrors() {
        return errors;
    }

    /**
     * Reset the errors and warnings. Use this before calling doImport()
     * to have a clean error and warning report.
     */
    @Override
    public void reset() {
        parser.reset();
        warnings = new HashMap<String, List<ReportItem>>();
        errors = new HashMap<String, List<ReportItem>>();
    }

    @Override
    public void doImport(String content, UserInfo userInfo) {
        this.warnings.clear();
        this.errors.clear();
        this.parser.reset();
        logger.debug("doing import");
        boolean success = parser.parse(userInfo, content);
        logger.debug("import success: " + success);
        if (!success) {
                List<ReportItem> report = new ArrayList<ReportItem>();
                report.add(new ReportItem("parser", "no success", 660));
                errors.put("parser", report);
                warnings.put("parser", new ArrayList<ReportItem>());
        }
        this.warnings.putAll(this.parser.getWarnings());
        this.errors.putAll(this.parser.getErrors());
    }
}
