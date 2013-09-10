package de.intevation.lada.data.importer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import de.intevation.lada.auth.AuthenticationResponse;
import de.intevation.lada.data.Repository;
import de.intevation.lada.validation.Validator;

/**
 * This importer uses the LAFParser to read data and create error/warning reports.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Named("lafimporter")
@Stateless
public class LAFImporter
implements Importer
{

    @Inject
    private EntityManager em;

    @Inject
    private LAFParser parser;

    @Inject
    @Named("lprobevalidator")
    private Validator probeValidator;
    @Inject
    @Named("lmessungvalidator")
    private Validator messungValidator;
    @Inject
    @Named("lortvalidator")
    private Validator ortValidator;
    @Inject
    @Named("lmesswertvalidator")
    private Validator messwertValidator;

    @Inject
    @Named("lproberepository")
    private Repository probeRepository;
    @Inject
    @Named("lmessungrepository")
    private Repository messungRepository;
    @Inject
    @Named("lortrepository")
    private Repository ortRepository;
    @Inject
    @Named("lkommentarRepository")
    private Repository pKommentarRepository;
    @Inject
    @Named("lkommentarmrepository")
    private Repository mKommentarRepository;
    @Inject
    @Named("lmesswertrepository")
    private Repository messwertRepository;

    private Map<String, List<ReportData>> warnings;
    private Map<String, List<ReportData>> errors;

    /**
     * Default constructor.
     */
    public LAFImporter() {
        warnings = new HashMap<String, List<ReportData>>();
        errors = new HashMap<String, List<ReportData>>();
    }

    /**
     * @return the warnings
     */
    public Map<String, List<ReportData>> getWarnings() {
        return warnings;
    }

    /**
     * @return the errors
     */
    public Map<String, List<ReportData>> getErrors() {
        return errors;
    }

    /**
     * Import LAF formated data using authentication information.
     *
     * @param content   The LAF file content.
     * @param auth      The authentication information.
     * @return success.
     */
    @Override
    public boolean importData(String content, AuthenticationResponse auth) {
        this.warnings.clear();
        this.errors.clear();
        this.parser.reset();
        try {
            boolean success = parser.parse(auth, content);
            if (!success) {
                List<ReportData> report = new ArrayList<ReportData>();
                report.add(new ReportData("parser", "no success", 660));
                errors.put("parser", report);
                warnings.put("parser", new ArrayList<ReportData>());
                return false;
            }
        }
        catch (LAFParserException e) {
            List<ReportData> report = new ArrayList<ReportData>();
            report.add(new ReportData("parser", e.getMessage(), 670));
            errors.put("parser", report);
            warnings.put("parser", new ArrayList<ReportData>());
            return false;
        }
        this.warnings.putAll(this.parser.getWarnings());
        this.errors.putAll(this.parser.getErrors());
        return true;
    }

    /**
     * Reset the errors and warnings. Use this before calling importData()
     * to have a clean error and warning report.
     */
    public void reset() {
        parser.reset();
        this.warnings = new HashMap<String, List<ReportData>>();
        this.errors = new HashMap<String, List<ReportData>>();
    }
}
