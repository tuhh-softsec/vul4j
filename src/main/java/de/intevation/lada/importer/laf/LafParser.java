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
import de.intevation.lada.validation.Violation;
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
        String probeId = producer.getProbeTranslation().getProbeIdAlt() == null ?
            "probeId" : producer.getProbeTranslation().getProbeIdAlt().toString();
        Violation violation = validateProbe(producer.getProbe());
        if (violation.hasErrors()) {
            ReportItem err = new ReportItem("validation", violation.getErrors(), null);
            List<ReportItem> errs= new ArrayList<ReportItem>();
            errs.add(err);
            this.appendErrors(probeId, errs);
            return;
        }
        boolean p = writer.writeProbe(userInfo, producer.getProbe(), producer.getProbeTranslation());
        if (!p) {
            this.errors.put(probeId, writer.getErrors());
            return;
        }
        writer.writeProbenKommentare(userInfo, producer.getProbenKommentare());
        for (LMessung messung: producer.getMessungen().keySet()) {
            Violation mViolation = messungValidator.validate(messung);
            if (mViolation.hasErrors()) {
                ReportItem mErr = new ReportItem("validation", mViolation.getErrors(), null);
                List<ReportItem> mErrs = new ArrayList<ReportItem>();
                mErrs.add(mErr);
                this.appendErrors(probeId, mErrs);
                continue;
            }
            boolean m = writer.writeMessungen(userInfo, messung, producer.getMessungen().get(messung));
            if (!m) {
                return;
            }
        }
        writer.writeOrte(userInfo, producer.getOrte());
        logger.debug("### i have " + producer.getLOrte().size() + " orte");
        writer.writeLOrte(userInfo, producer.getLOrte());
        writer.writeMessungKommentare(userInfo, producer.getMessungsKommentare());
        writer.writeMesswerte(userInfo, producer.getMesswerte());
        Violation postViolation = validateProbe(producer.getProbe());
        if (postViolation.hasWarnings()) {
            ReportItem warn = new ReportItem("validation", postViolation.getWarnings(), null);
            List<ReportItem> warns = new ArrayList<ReportItem>();
            warns.add(warn);
            this.appendWarnings(probeId, warns);
        }
        for (LMessung messung: producer.getMessungen().keySet()) {
            Violation mViolation = messungValidator.validate(messung);
            if (mViolation.hasWarnings()) {
                ReportItem mWarn = new ReportItem("validation", mViolation.getWarnings(), null);
                List<ReportItem> mWarns = new ArrayList<ReportItem>();
                mWarns.add(mWarn);
                this.appendWarnings(probeId, mWarns);
            }
        }
    }

    private Violation validateProbe(LProbe probe) {
        return probeValidator.validate(probe);
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
                ReportItem item = new ReportItem("parser", "general error", 603);
                List<ReportItem> items = new ArrayList<ReportItem>();
                items.add(item);
                this.appendErrors("parser", items);
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
