package de.intevation.lada.data.importer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import de.intevation.lada.auth.AuthenticationResponse;

/**
 * This parser is used to read data in LAF based key-value pair structure.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class LAFParser {

    private static final String PROBE_NEXT = "\n%PROBE%";

    private boolean dryRun;

    @Inject
    @Named("lafproducer")
    private Producer producer;

    @Inject
    @Named("lafwriter")
    private Writer writer;

    private Map<String, List<ReportData>> warnings;
    private Map<String, List<ReportData>> errors;

    /**
     * Default constructor.
     */
    public LAFParser() {
        this.warnings = new HashMap<String, List<ReportData>>();
        this.errors = new HashMap<String, List<ReportData>>();
        this.setDryRun(false);
    }

    /**
     * Read and parse the data and write the objects to the database.
     *
     * @param auth  Authentication information
     * @param laf   The LAF formated data.
     * @return success
     * @throws LAFParserException
     */
    public boolean parse(AuthenticationResponse auth, String laf)
    throws LAFParserException
    {
        if (!laf.startsWith("%PROBE%\n")) {
            throw new LAFParserException("No %PROBE% at the begining.");
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
                    readAll(single);
                    this.warnings.putAll(producer.getWarnings());
                    this.errors.putAll(producer.getErrors());
                    writeAll(auth);
                    this.producer.reset();
                    this.writer.reset();
                }
                catch (LAFParserException lpe) {
                    Map<String, List<ReportData>> pErr = producer.getErrors();
                    if (pErr.isEmpty()) {
                        List<ReportData> err = new ArrayList<ReportData>();
                        err.add(new ReportData("parser", lpe.getMessage(), 673));
                        this.errors.put("parser", err);
                        this.warnings.put("parser", new ArrayList<ReportData>());
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
                    readAll(laf);
                    this.warnings.putAll(producer.getWarnings());
                    this.errors.putAll(producer.getErrors());
                    writeAll(auth);
                    this.producer.reset();
                    this.writer.reset();
                    laf = "";
                }
                catch (LAFParserException lpe) {
                    Map<String, List<ReportData>> pErr = producer.getErrors();
                    if (pErr.isEmpty()) {
                        List<ReportData> err = new ArrayList<ReportData>();
                        err.add(new ReportData("parser", lpe.getMessage(), 673));
                        this.errors.put("parser", err);
                        this.warnings.put("parser", new ArrayList<ReportData>());
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
    private void writeAll(AuthenticationResponse auth) {
        String probeId = producer.getProbe().getProbeId() == null ? "probeId" : producer.getProbe().getProbeId();
        boolean p = writer.writeProbe(auth, producer.getProbe());
        if (!p) {
            this.errors.put(probeId, writer.getErrors());
            this.warnings.put(probeId, writer.getWarnings());
            return;
        }
        writer.writeProbenKommentare(auth, producer.getProbenKommentare());
        boolean m = writer.writeMessungen(auth, producer.getMessungen());
        if (!m) {
            return;
        }
        writer.writeOrte(auth, producer.getOrte());
        writer.writeLOrte(auth, producer.getLOrte());
        writer.writeMessungKommentare(auth, producer.getMessungsKommentare());
        writer.writeMesswerte(auth, producer.getMesswerte());
        List<ReportData> err = this.errors.get(probeId);
        if (err == null) {
            this.errors.put(probeId, writer.getErrors());
        }
        else {
            err.addAll(writer.getErrors());
        }
        List<ReportData> warn = this.warnings.get(probeId);
        if (warn == null) {
            this.warnings.put(probeId, writer.getWarnings());
        }
        else {
            warn.addAll(writer.getWarnings());
        }
    }

    /**
     * Read all attributes from a single probe block and create entity objects.
     *
     * @param content   Single probe block enclosed by %PROBE%
     * @throws LAFParserException
     */
    private void readAll(String content)
    throws LAFParserException
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
                throw new LAFParserException("No value for key: " + keyString);
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
     * Reset errors and warnings.
     */
    public void reset() {
        producer.reset();
        this.errors = new HashMap<String, List<ReportData>>();
        this.warnings = new HashMap<String, List<ReportData>>();
    }
}
