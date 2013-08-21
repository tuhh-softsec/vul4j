package de.intevation.lada.data.importer;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import de.intevation.lada.model.LKommentarM;
import de.intevation.lada.model.LKommentarP;
import de.intevation.lada.model.LMessung;
import de.intevation.lada.model.LMesswert;
import de.intevation.lada.model.LOrt;
import de.intevation.lada.model.LProbe;


public class LAFParser {

    private static final String PROBE_NEXT = "\n%PROBE%";

    private boolean dryRun;

    //@Inject
    //@Named("lafproducer")
    private Producer producer;

    List<LProbe> proben;
    List<LMessung> messungen;
    List<LOrt> orte;
    List<LMesswert> messwerte;
    List<LKommentarP> probeKommentare;
    List<LKommentarM> messungKommentare;

    public LAFParser() {
        this.setDryRun(false);
        this.producer = new LAFProducer();
        this.proben = new ArrayList<LProbe>();
        this.messungen = new ArrayList<LMessung>();
        this.orte = new ArrayList<LOrt>();
        this.messwerte = new ArrayList<LMesswert>();
        this.probeKommentare = new ArrayList<LKommentarP>();
        this.messungKommentare = new ArrayList<LKommentarM>();
    }

    public boolean parse(String laf)
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
                readAll(single);
            }
            else {
                readAll(laf);
                laf = "";
            }
            if (!this.dryRun) {
                proben.add(producer.getProbe());
                messungen.addAll(producer.getMessungen());
                orte.addAll(producer.getOrte());
                messwerte.addAll(producer.getMesswerte());
                probeKommentare.addAll(producer.getProbenKommentare());
                messungKommentare.addAll(producer.getMessungsKommentare());
                producer.reset();
            }
        }
        return parsed;
    }

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
            else if (current != ' ' && white) {
                value = true;
                white = false;
            }
            else if (current == '%' && !header && !value) {
                headerString = "";

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
            else if ((current == '\n' || current == '\r') && key) {
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

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }
}
