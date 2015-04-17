package de.intevation.lada.importer.laf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.intevation.lada.importer.ReportItem;
import de.intevation.lada.model.Ort;
import de.intevation.lada.model.land.LKommentarM;
import de.intevation.lada.model.land.LKommentarP;
import de.intevation.lada.model.land.LMessung;
import de.intevation.lada.model.land.LMesswert;
import de.intevation.lada.model.land.LOrt;
import de.intevation.lada.model.land.LProbe;
import de.intevation.lada.model.land.LZusatzWert;
import de.intevation.lada.model.land.MessungTranslation;
import de.intevation.lada.model.land.ProbeTranslation;
import de.intevation.lada.model.stamm.SOrt;

/**
 * The LAFProducer creates entity objects form key-value pairs using the
 * AttributeMapper.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class LafProducer
{
    @Inject
    private Logger logger;

    private LProbe probe;
    private ProbeTranslation probeTranslation;
    private LMessung messung;
    private MessungTranslation messungTranslation;

    @Inject
    private OrtCreator ort;

    private List<LKommentarP> pKommentare;
    private Map<LMessung, List<LKommentarM>> mKommentare;
    private Map<LMessung, MessungTranslation> messungen;
    private List<LOrt> lorte;
    private List<SOrt> orte;
    private Map<LMessung, List<LMesswert>> messwerte;
    private List<LZusatzWert> zusatzwerte;

    private List<EntryFormat> probenFormat;
    private List<EntryFormat> messungFormat;
    private List<EntryFormat> ortFormat;

    private Map<String, List<ReportItem>> warnings;
    private Map<String, List<ReportItem>> errors;

    @Inject
    private AttributeMapper mapper;

    /**
     * Default contructor. Initializes the producer and reads the config file
     * using the systemproperty "de.intevation.lada.importconfig".
     */
    public LafProducer() {
        this.warnings = new HashMap<String, List<ReportItem>>();
        this.errors = new HashMap<String, List<ReportItem>>();
        this.probe = new LProbe();
        this.probeTranslation = new ProbeTranslation();
        this.pKommentare = new ArrayList<LKommentarP>();
        this.mKommentare = new HashMap<LMessung, List<LKommentarM>>();
        this.messungen = new HashMap<LMessung, MessungTranslation>();
        this.lorte = new ArrayList<LOrt>();
        this.messwerte = new HashMap<LMessung, List<LMesswert>>();
        String fileName = "/import.json";
        LafFormat format = new LafFormat();
        format.readConfigFile(fileName);
        probenFormat = format.getFormat("probe");
        messungFormat = format.getFormat("messung");
        ortFormat = format.getFormat("ort");
    }

    /**
     * Add data to the producer.
     * This triggers the producer to create a new object or add data to
     * existing objects.
     *
     * @param key       The key.
     * @param values    The value
     * @throws LafParserException
     */
    public void addData(String key, Object values)
    throws LafParserException {
        logger.debug("adding " + key + ": " + values);
        String lKey = key.toLowerCase();
        if(lKey.equals("probenkommentar")) {
            LKommentarP kommentar = new LKommentarP();
            kommentar.setProbeId(this.probe.getId());
            this.pKommentare.add(
                mapper.addAttribute(lKey, values, kommentar));
        }
        else if (lKey.equals("kommentar")) {
            LKommentarM kommentar = new LKommentarM();
            kommentar.setMessungsId(this.messung.getId());
            if (this.mKommentare.get(this.messung) == null) {
                this.mKommentare.put(this.messung, new ArrayList<LKommentarM>());
            }
            this.mKommentare.get(this.messung).add(
                mapper.addAttribute(lKey, values, kommentar));
        }
        else if (lKey.equals("probenzusatzbeschreibung")) {
            LZusatzWert wert = new LZusatzWert();
            LZusatzWert zusatzWert = mapper.addAttribute(lKey, values, wert);
            if (zusatzWert != null) {
                zusatzWert.setProbeId(probe.getId());
                this.zusatzwerte.add(zusatzWert);
            }
            else {
                String ekey = probe.getId() == null ?
                    "probeId" : probe.getId().toString();
                List<ReportItem> err = this.errors.get(ekey);
                if (err == null) {
                    this.errors.put(ekey, mapper.getErrors());
                }
                else {
                    err.addAll(mapper.getErrors());
                }
            }
        }
        else if (lKey.equals("pzb_s")) {
            LZusatzWert wert = new LZusatzWert();
            LZusatzWert zusatzWert = mapper.addAttributeS(lKey, values, wert);
            if (zusatzWert != null) {
                zusatzWert.setProbeId(probe.getId());
                this.zusatzwerte.add(zusatzWert);
            }
            else {
                String ekey = probe.getId() == null ?
                    "probeId" : probe.getId().toString();
                List<ReportItem> err = this.errors.get(ekey);
                if (err == null) {
                    this.errors.put(ekey, mapper.getErrors());
                }
                else {
                    err.addAll(mapper.getErrors());
                }
            }
        }
        else if (lKey.equals("messwert")) {
            LMesswert m = new LMesswert();
            m.setMessungsId(this.messung.getId());
            LMesswert wert = mapper.addAttribute(lKey, values, m);
            if (wert != null) {
                if (this.messwerte.get(this.messung) == null) {
                    this.messwerte.put(this.messung, new ArrayList<LMesswert>());
                }
                this.messwerte.get(this.messung).add(
                    mapper.addAttribute(lKey, values, wert));
            }
            else {
                String ekey = probe.getId() == null ?
                    "probeId" : probe.getId().toString();
                List<ReportItem> err = this.errors.get(ekey);
                if (err == null) {
                    this.errors.put(ekey, mapper.getErrors());
                }
                else {
                    err.addAll(mapper.getErrors());
                }
            }
        }
        else if (isValidMessung(lKey, values.toString())) {
            this.messung = mapper.addAttribute(lKey, values, this.messung);
            logger.debug("####### " + lKey + ": " + values);
            this.messungTranslation =
                mapper.addAttribute(lKey, values, this.messungTranslation);
        }
        else if (isValidProbe(lKey, values.toString())) {
            this.probe = mapper.addAttribute(lKey, values, this.probe);
            this.probeTranslation = mapper.addAttribute(lKey, values, this.probeTranslation);
            if (this.probe == null || this.probeTranslation == null) {
                this.errors.put(values.toString(), mapper.getErrors());
                throw new LafParserException(values.toString() + " exists");
            }
        }
        else if (isValidOrt(lKey, values.toString())) {
            this.ort.addAttribute(lKey, values);
        }
    }

    /**
     * Check if the key is defined in the config file and validate the value
     * using the pattern defined for this key.
     *
     * @param key   The key.
     * @param value The value.
     * @return valid or not.
     */
    private boolean isValidOrt(String key, String value) {
        for (EntryFormat ef: ortFormat) {
            if (ef.getKey().equals(key.toLowerCase())) {
                if (ef.getPattern().matcher(value).matches()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if the key is defined in the config file and validate the value
     * using the pattern defined for this key.
     *
     * @param key   The key.
     * @param value The value.
     * @return valid or not.
     */
    private boolean isValidMessung(String key, String value) {
        for (EntryFormat ef: messungFormat) {
            if (ef.getKey().equals(key.toLowerCase())) {
                if (ef.getPattern().matcher(value).matches()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if the key is defined in the config file and validate the value
     * using the pattern defined for this key.
     *
     * @param key   The key.
     * @param value The value.
     * @return valid or not.
     */
    private boolean isValidProbe(String key, String value) {
        for (EntryFormat ef: probenFormat) {
            if (ef.getKey().equals(key.toLowerCase())) {
                if (ef.getPattern().matcher(value).matches()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return the {@link LProbe} entity.
     */
    public LProbe getProbe() {
        return this.probe;
    }

    public ProbeTranslation getProbeTranslation() {
        return this.probeTranslation;
    }

    /**
     * @return List of {@link LMessung} entities.
     */
    public Map<LMessung, MessungTranslation> getMessungen() {
        return this.messungen;
    }

    /**
     * @return List of {@link Ort} entities.
     */
    public List<SOrt> getOrte() {
        return this.orte;
    }

    /**
     * @return List of {@link LOrt} entities.
     */
    public List<LOrt> getLOrte() {
        return this.lorte;
    }

    /**
     * @return List of {@link LKommentarP} entities.
     */
    public List<LKommentarP> getProbenKommentare() {
        return this.pKommentare;
    }

    /**
     * @return List of {@link LKommentarM} entities.
     */
    public Map<LMessung, List<LKommentarM>> getMessungsKommentare() {
        return this.mKommentare;
    }

    /**
     * @return List of {@link LMesswert} entities.
     */
    public Map<LMessung, List<LMesswert>> getMesswerte() {
        return this.messwerte;
    }

    /**
     * @return List of {@link LZusatzWert} entities.
     */
    public List<LZusatzWert> getZusatzwerte() {
        return this.zusatzwerte;
    }

    /**
     * Reset errors and warnings.
     */
    public void reset() {
        this.errors = new HashMap<String, List<ReportItem>>();
        this.warnings = new HashMap<String, List<ReportItem>>();
        this.probe = new LProbe();
        this.messungen = new HashMap<LMessung, MessungTranslation>();
        this.messung = null;
        this.lorte = new ArrayList<LOrt>();
        this.orte = new ArrayList<SOrt>();
        this.ort.reset();
        this.messwerte = new HashMap<LMessung, List<LMesswert>>();
        this.mKommentare = new HashMap<LMessung, List<LKommentarM>>();
        this.pKommentare = new ArrayList<LKommentarP>();
        mapper.reset();
    }

    /**
     * Add the current {@link LMessung} entity to the List and create a new one.
     */
    public void newMessung() {
        if (this.messung != null && this.messungTranslation != null) {
            if (this.messung.getFertig() == null) {
                this.messung.setFertig(false);
            }
            if (this.messung.getGeplant() == null) {
                this.messung.setGeplant(false);
            }
            this.messungen.put(this.messung, this.messungTranslation);
        }
        this.messung = new LMessung();
        this.messungTranslation = new MessungTranslation();
    }

    /**
     * Add the {@link Ort} and {@link LOrt} entities to the lists and create
     * a new {@link OrtCreator}.
     */
    public void newOrt() {
        if (this.ort != null) {
            SOrt o = this.ort.toOrt();
            if (o != null) {
                this.orte.add(o);
            }
            logger.debug("ort: " + this.ort.getProbeId());
            LOrt lo = this.ort.toLOrt();
            logger.debug("lo is " + lo != null);
            if (lo != null) {
                this.lorte.add(lo);
            }
        }
        OrtCreator creator = this.ort;
        creator.reset();
        logger.debug(this.probe.getId());
        logger.debug(creator.hashCode());
        creator.setProbeId(this.probe.getId());
        logger.debug("ort: " + creator.getProbeId());
        logger.debug(creator.hashCode());
    }

    /**
     * @return the warnings
     */
    public Map<String, List<ReportItem>> getWarnings() {
        if (this.probe == null) {
            return this.warnings;
        }
        if (mapper.getWarnings() == null || mapper.getWarnings().size() == 0) {
            return this.warnings;
        }
        String key = probe.getId() == null ? "probeId" : probe.getId().toString();
        List<ReportItem> warn = this.warnings.get(key);
        if (warn == null) {
            this.warnings.put(key, mapper.getWarnings());
        }
        else {
            warn.addAll(mapper.getWarnings());
        }
        return this.warnings;
    }

    /**
     * @return the errors
     */
    public Map<String, List<ReportItem>> getErrors() {
        if (this.probe == null || this.probeTranslation == null) {
            return this.errors;
        }
        if (mapper.getErrors() == null || mapper.getErrors().size() == 0) {
            return this.errors;
        }
        String key = probeTranslation.getProbeIdAlt() == null ?
            "probeId" : probeTranslation.getProbeIdAlt();
        List<ReportItem> err = this.errors.get(key);
        if (err == null) {
            this.errors.put(key, mapper.getErrors());
        }
        else {
            err.addAll(mapper.getErrors());
        }
        return this.errors;
    }

    public void finishOrt() {
        if (orte.isEmpty()) {
            return;
        }
    }
}
