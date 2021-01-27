/* Copyright (C) 2015 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.importer.laf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Raw data parsed from a LAF data file.
 */
public class LafRawData {

    private List<LafRawData.Probe> probe;

    public LafRawData() {
        this.probe = new ArrayList<LafRawData.Probe>();
    }

    /**
     * Raw probe data with simple key value mapping.
     */
    public class Probe {
        private Map<String, String> attributes;
        private List<LafRawData.Messung> messung;
        private List<Map<String, String>> kommentar;
        private List<Map<String, String>> zusatzwert;
        private Map<String, String> eOrt;
        private List<Map<String, String>> uOrt;

        public Probe() {
            this.attributes = new HashMap<String, String>();
            this.eOrt = new HashMap<String, String>();
            this.uOrt = new ArrayList<Map<String, String>>();
            this.kommentar = new ArrayList<Map<String, String>>();
            this.zusatzwert = new ArrayList<Map<String, String>>();
            this.messung = new ArrayList<LafRawData.Messung>();
        }

        /**
         * Add a new key value pair.
         * @param key The key
         * @param value The value
         */
        public void addAttribute(String key, String value) {
            this.attributes.put(key, value);
        }

        /**
         * Get the attributes as key value map.
         * @return the attribute mapping
         */
        public Map<String, String> getAttributes() {
            return this.attributes;
        }

        /**
         * Add a messung raw data object.
         * @param m the messung
         */
        public void addMessung(LafRawData.Messung m) {
            this.messung.add(m);
        }

        /**
         * Get all messung raw data mappings.
         * @return list of all messung sets.
         */
        public List<LafRawData.Messung> getMessungen() {
            return this.messung;
        }

        /**
         * Add a kommentar raw data object.
         * @param k the kommentar
         */
        public void addKommentar(Map<String, String> k) {
            this.kommentar.add(k);
        }

        /**
         * Get all kommentar raw data mappings.
         * @return list of kommentar sets
         */
        public List<Map<String, String>> getKommentare() {
            return this.kommentar;
        }

        /**
         * Add a zusatzwert raw data object.
         * @param z the zusatzwert
         */
        public void addZusatzwert(Map<String, String> z) {
            this.zusatzwert.add(z);
        }

        /**
         * Get all zusatzwert raw data mappings.
         * @return list of zusatzwert sets.
         */
        public List<Map<String, String>> getZusatzwerte() {
            return this.zusatzwert;
        }

        /**
         * Add a ort raw data object.
         * @param o the ort
         */
        public void addEntnahmeOrt(Map<String, String> o) {
            this.eOrt.putAll(o);
        }

        /**
         * Get all ort raw data mappings.
         * @return list of ort sets.
         */
        public Map<String, String> getEntnahmeOrt() {
            return this.eOrt;
        }

        /**
         * Add a ort raw data object.
         * @param o the ort
         */
        public void addUrsprungsOrt(Map<String, String> o) {
            this.uOrt.add(new HashMap<String, String>(o));
        }

        /**
         * Get all ort raw data mappings.
         * @return list of ort sets.
         */
        public List<Map<String, String>> getUrsprungsOrte() {
            return this.uOrt;
        }

        /**
         * Helper method to get identifying attribute.
         * @return the identifier of the probe object
         */
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

    /**
     * Raw messung data with simple key value mapping.
     */
    public class Messung {
        private Map<String, String> attributes;
        private List<Map<String, String>> messwert;
        private List<Map<String, String>> kommentar;
        private boolean hasErrors;

        public Messung() {
            this.attributes = new HashMap<String, String>();
            this.messwert = new ArrayList<Map<String, String>>();
            this.kommentar = new ArrayList<Map<String, String>>();
        }

        /**
         * Add a new key value pair.
         * @param key The key
         * @param value The value
         */
        public void addAttribute(String key, String value) {
            this.attributes.put(key, value);
        }

        /**
         * Get the attributes as key value map.
         * @return the attribute mapping
         */
        public Map<String, String> getAttributes() {
            return this.attributes;
        }

        /**
         * Add a messwert raw data object.
         * @param m the messwert
         */
        public void addMesswert(Map<String, String> m) {
            this.messwert.add(m);
        }

        /**
         * Get all messwert raw data mappings.
         * @return list of messwert sets
         */
        public List<Map<String, String>> getMesswerte() {
            return this.messwert;
        }

        /**
         * Add a kommentar raw data object.
         * @param k the kommentar
         */
        public void addKommentar(Map<String, String> k) {
            this.kommentar.add(k);
        }

        /**
         * Get all kommentar raw data mappings.
         * @return list of kommentar sets
         */
        public List<Map<String, String>> getKommentare() {
            return this.kommentar;
        }

        /**
         * Check if the messung has errors.
         * @return true if there are errors
         */
        public boolean hasErrors() {
            return hasErrors;
        }

        /**
         * Indicate that there were errors.
         * @param hasErrors true if there were errors.
         */
        public void setHasErrors(boolean hasErrors) {
            this.hasErrors = hasErrors;
        }
    }

    /**
     * Add a probe raw data object.
     * @param p the probe
     */
    public void addProbe(LafRawData.Probe p) {
        this.probe.add(p);
    }

    public List<LafRawData.Probe> getProben() {
        return this.probe;
    }

    /**
     * Get the count of all probe objects.
     * @return the probe count
     */
    public int count() {
        return this.probe.size();
    }
}
