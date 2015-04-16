package de.intevation.lada.importer.laf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.intevation.lada.importer.ReportItem;
import de.intevation.lada.model.land.LMessung;
import de.intevation.lada.model.land.LMesswert;
import de.intevation.lada.model.land.LOrt;
import de.intevation.lada.model.land.LProbe;
import de.intevation.lada.model.land.MessungTranslation;
import de.intevation.lada.util.auth.UserInfo;
import de.intevation.lada.validation.Validator;
import de.intevation.lada.validation.annotation.ValidationConfig;

/**
 * This parser is used to read data in LAF based key-value pair structure.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class LafParser {

    @Inject
    private Logger logger;

    private static final String PROBE_NEXT = "\n%PROBE%";

    private boolean dryRun;

    @Inject
    private LafProducer producer;

    @Inject
    private LafWriter writer;

    @Inject
    @ValidationConfig(type="Probe")
    private Validator probeValidator;

    @Inject
    @ValidationConfig(type="Messung")
    private Validator messungValidator;

    //@Inject
    //@ValidationConfig(type="Messwert")
    //private Validator messwertValidator;

    //@Inject
    //@ValidationConfig(type="Ort")
    //private Validator ortValidator;

    private Map<String, List<ReportItem>> warnings;
    private Map<String, List<ReportItem>> errors;

    /**
     * Default constructor.
     */
    public LafParser() {
        this.warnings = new HashMap<String, List<ReportItem>>();
        this.errors = new HashMap<String, List<ReportItem>>();
        this.setDryRun(false);
    }

    /**
     * Read and parse the data and write the objects to the database.
     *
     * @param auth  Authentication information
     * @param laf   The LAF formated data.
     * @return success
     * @throws LafParserException
     */
    public boolean parse(UserInfo userInfo, String laf) {
        if (!laf.startsWith("%PROBE%\n")) {
            logger.debug("no %PROBE% tag found!");
            return false;
        }
        boolean parsed = false;
        while (laf.startsWith("%PROBE%\n")) {
            parsed = true;
            int nextPos = laf.indexOf(PROBE_NEXT);
            String single = "";
            if (nextPos > 0) {
                single = laf.substring(0, nextPos + 1);
                laf = laf.substring(nextPos + 1);
                try {
                    logger.debug("parsing probe");
                    readAll(single);
                    this.warnings.putAll(producer.getWarnings());
                    this.errors.putAll(producer.getErrors());
                    logger.debug("writing to database");
                    writeAll(userInfo);
                    this.producer.reset();
                    this.writer.reset();
                }
                catch (LafParserException lpe) {
                    Map<String, List<ReportItem>> pErr = producer.getErrors();
                    if (pErr.isEmpty()) {
                        List<ReportItem> err = new ArrayList<ReportItem>();
                        err.add(new ReportItem("parser", lpe.getMessage(), 673));
                        this.errors.put("parser", err);
                        this.warnings.put("parser", new ArrayList<ReportItem>());
                    }
                    else {
                        this.errors.putAll(pErr);
                        this.warnings.putAll(producer.getWarnings());
                    }
                    this.producer.reset();
                    continue;
                }
            }
            else {
                try {
                    logger.debug("parsing single probe");
                    readAll(laf);
                    this.warnings.putAll(producer.getWarnings());
                    this.errors.putAll(producer.getErrors());
                    logger.debug("writing single to database");
                    writeAll(userInfo);
                    this.producer.reset();
                    this.writer.reset();
                    laf = "";
                }
                catch (LafParserException lpe) {
                    Map<String, List<ReportItem>> pErr = producer.getErrors();
                    if (pErr.isEmpty()) {
                        List<ReportItem> err = new ArrayList<ReportItem>();
                        err.add(new ReportItem("parser", lpe.getMessage(), 673));
                        this.errors.put("parser", err);
                        this.warnings.put("parser", new ArrayList<ReportItem>());
                    }
                    else {
                        this.errors.putAll(pErr);
                        this.warnings.putAll(producer.getWarnings());
                    }
                    this.producer.reset();
                    laf = "";
                    continue;
                }
            }
        }
        return parsed;
    }

    /**
     * Write all created objects to the database.
     *
     * @param auth  The authentication information.
     */
    private void writeAll(UserInfo userInfo) {
        String probeId = producer.getProbe().getId() == null ?
            "probeId" : producer.getProbe().getId().toString();
        boolean p = writer.writeProbe(userInfo, producer.getProbe());
        logger.debug("write probe: " + p);
        if (!p) {
            this.errors.put(probeId, writer.getErrors());
            return;
        }
        writer.writeProbenKommentare(userInfo, producer.getProbenKommentare());
        boolean m = writer.writeMessungen(userInfo, producer.getMessungen());
        if (!m) {
            return;
        }
        for (LMessung tm : producer.getMessungen().keySet()) {
            logger.debug("messungsid: " + tm.getId());
        }
        writer.writeOrte(userInfo, producer.getOrte());
        logger.debug("### i have " + producer.getLOrte().size() + " orte");
        writer.writeLOrte(userInfo, producer.getLOrte());
        writer.writeMessungKommentare(userInfo, producer.getMessungsKommentare());
        writer.writeMesswerte(userInfo, producer.getMesswerte());
        this.validateProbe(producer.getProbe());
        this.validateMessungen(producer.getMessungen());
        this.validateMesswerte(producer.getMesswerte());
        this.validateLOrte(producer.getLOrte());
    }

    private void validateProbe(LProbe probe) {
    }

    private void validateMessungen(Map<LMessung, MessungTranslation> messungen) {
    }

    private void validateMesswerte(Map<LMessung, List<LMesswert>> werte) {
    }

    private void validateLOrte(List<LOrt> orte) {
    }

    /**
     * Read all attributes from a single probe block and create entity objects.
     *
     * @param content   Single probe block enclosed by %PROBE%
     * @throws LafParserException
     */
    private void readAll(String content)
    throws LafParserException
    {
        boolean key = false;
        boolean value = false;
        boolean header = false;
        boolean white = false;
        boolean string = false;
        boolean multiValue = false;
        String keyString = "";
        String valueString = "";
        String headerString = "";
        for (int i = 0; i < content.length(); i++) {
            char current = content.charAt(i);

            if ((current == '"' || (current == ' ' && !string)) &&
                value &&
                i < content.length() - 1 &&
                (content.charAt(i + 1) != '\n' &&
                content.charAt(i + 1) != '\r')) {
                multiValue = true;
            }

            if (current == '"' && !string) {
                string = true;
            }
            else if (current == '"' && string) {
                string = false;
            }

            if (current == ' ' && !value) {
                key = false;
                white = true;
                continue;
            }
            else if (current != ' ' &&
                current != '\n' &&
                current != '\r' &&
                white) {
                value = true;
                white = false;
            }
            else if (current == '%' && !header && !value) {
                headerString = "";
                producer.finishOrt();
                key = false;
                header = true;
            }
            else if ((current == '\n' || current == '\r') && header) {
                header = false;
                key = true;
                if (!dryRun) {
                    if (headerString.contains("MESSUNG")) {
                        producer.newMessung();
                    }
                    if (headerString.contains("ORT")) {
                        producer.newOrt();
                    }
                }
                if (headerString.contains("%ENDE%")) {
                    if (!dryRun) {
                        this.producer.newMessung();
                        this.producer.newOrt();
                    }
                    return;
                }
                continue;
            }
            else if (current == '"' && !value) {
                value = true;
            }
            else if ((current == '\n' || current == '\r') && value && !string) {
                if (!multiValue && valueString.startsWith("\"")) {
                    valueString =
                        valueString.substring(1, valueString.length() - 1);
                }
                value = false;
                multiValue = false;
                key = true;
                if (!this.dryRun) {
                    producer.addData(keyString, valueString);
                }
                keyString = "";
                valueString = "";
                continue;
            }
            if ((current == '\n' || current == '\r') && (key || white)) {
                //TODO error!!!
                return;
            }

            if (key) {
                keyString += current;
            }
            else if (value) {
                valueString += current;
            }
            else if (header) {
                headerString += current;
            }
        }
        if (!dryRun) {
            this.producer.newMessung();
            this.producer.newOrt();
        }
    }

    /**
     * @return if objects are or not.
     */
    public boolean isDryRun() {
        return dryRun;
    }

    /**
     * If set to true, no objects will be created and written to database.
     *
     * @param dryRun
     */
    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    /**
     * @return the warnings
     */
    public Map<String, List<ReportItem>> getWarnings() {
        return warnings;
    }

    /**
     * @return the errors
     */
    public Map<String, List<ReportItem>> getErrors() {
        return errors;
    }

    /**
     * Reset errors and warnings.
     */
    public void reset() {
        producer.reset();
        this.errors = new HashMap<String, List<ReportItem>>();
        this.warnings = new HashMap<String, List<ReportItem>>();
    }

    private void appendErrors(String probeId, List<ReportItem> errs) {
        List<ReportItem> err = this.errors.get(probeId);
        if (err == null) {
            this.errors.put(probeId, errs);
        }
        else {
            err.addAll(errs);
            this.errors.put(probeId, err);
        }
    }

    private void appendWarnings(String probeId, List<ReportItem> warns) {
        List<ReportItem> warn = this.warnings.get(probeId);
        if (warn == null) {
            this.warnings.put(probeId, warns);
        }
        else {
            warn.addAll(warns);
            this.warnings.put(probeId, warn);
        }
    }
}
