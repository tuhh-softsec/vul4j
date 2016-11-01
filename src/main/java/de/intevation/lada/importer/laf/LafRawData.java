package de.intevation.lada.importer.laf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LafRawData {

    private List<LafRawData.Probe> probe;

    public LafRawData () {
        this.probe = new ArrayList<LafRawData.Probe>();
    }

    public class Probe {
        private Map<String, String> attributes;
        private List<LafRawData.Messung> messung;
        private List<Map<String, String>> kommentar;
        private List<Map<String, String>> zusatzwert;
        private List<Map<String, String>> ort;

        public Probe() {
            this.attributes = new HashMap<String, String>();
            this.ort = new ArrayList<Map<String, String>>();
            this.kommentar = new ArrayList<Map<String, String>>();
            this.zusatzwert = new ArrayList<Map<String, String>>();
            this.messung = new ArrayList<LafRawData.Messung>();
        }

        public void addAttribute(String key, String value) {
            this.attributes.put(key, value);
        }

        public Map<String, String> getAttributes() {
            return this.attributes;
        }

        public void addMessung(LafRawData.Messung messung) {
            this.messung.add(messung);
        }

        public List<LafRawData.Messung> getMessungen() {
            return this.messung;
        }

        public void addKommentar(Map<String, String> kommentar) {
            this.kommentar.add(kommentar);
        }

        public List<Map<String, String>> getKommentare() {
            return this.kommentar;
        }

        public void addZusatzwert(Map<String, String> zusatzwert) {
            this.zusatzwert.add(zusatzwert);
        }

        public List<Map<String, String>> getZusatzwerte() {
            return this.zusatzwert;
        }

        public void addOrt(Map<String, String> ort) {
            this.ort.add(ort);
        }

        public List<Map<String, String>> getOrte() {
            return this.ort;
        }

        // helper method to get identifying attribute
        public String getIdentifier() {
            String identifier = this.getAttributes().get("PROBE_ID");
            identifier = identifier == null
                ? this.getAttributes().get("PROBEN_NR")
                : identifier;
            identifier = identifier == null
                ? this.getAttributes().get("HAUPTPROBENNUMMER")
                : identifier;
            identifier = identifier == null
                ? "not identified"
                : identifier;
            return identifier;
        }

    };

    public class Messung {
        private Map<String, String> attributes;
        private List<Map<String, String>> messwert;
        private List<Map<String, String>> kommentar;

        public Messung() {
            this.attributes = new HashMap<String, String>();
            this.messwert = new ArrayList<Map<String, String>>();
            this.kommentar = new ArrayList<Map<String, String>>();
        }

        public void addAttribute(String key, String value) {
            this.attributes.put(key, value);
        }

        public Map<String, String> getAttributes() {
            return this.attributes;
        }

        public void addMesswert(Map<String, String> messwert) {
            this.messwert.add(messwert);
        }

        public List<Map<String, String>> getMesswerte() {
            return this.messwert;
        }

        public void addKommentar(Map<String, String> kommentar) {
            this.kommentar.add(kommentar);
        }

        public List<Map<String, String>> getKommentare() {
            return this.kommentar;
        }
    }

    public void addProbe(LafRawData.Probe probe) {
        this.probe.add(probe);
    }

    public List<LafRawData.Probe> getProben() {
        return this.probe;
    }

    public int count() {
        return this.probe.size();
    }
}
