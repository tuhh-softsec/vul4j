package de.intevation.lada.data.importer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import de.intevation.lada.data.QueryBuilder;
import de.intevation.lada.data.Repository;
import de.intevation.lada.model.LKommentarM;
import de.intevation.lada.model.LKommentarMId;
import de.intevation.lada.model.LKommentarP;
import de.intevation.lada.model.LMessung;
import de.intevation.lada.model.LMessungId;
import de.intevation.lada.model.LMesswert;
import de.intevation.lada.model.LMesswertId;
import de.intevation.lada.model.LOrt;
import de.intevation.lada.model.LProbe;
import de.intevation.lada.model.LProbe;
import de.intevation.lada.model.LZusatzWert;
import de.intevation.lada.model.LZusatzWertId;
import de.intevation.lada.model.Ort;
import de.intevation.lada.model.SProbenZusatz;

@Named("lafproducer")
public class LAFProducer
implements Producer
{

    @Inject
    @Named("readonlyrepository")
    private Repository sRepository;

    private LProbe probe;
    private LMessung messung;
    private OrtCreator ort;

    private List<LKommentarP> pKommentare;
    private List<LKommentarM> mKommentare;
    private List<LMessung> messungen;
    private List<LOrt> lorte;
    private List<Ort> orte;
    private List<LMesswert> messwerte;
    private List<LZusatzWert> zusatzwerte;

    private List<EntryFormat> probenFormat;
    private List<EntryFormat> messungFormat;
    private List<EntryFormat> ortFormat;

    private Map<String, List<ReportData>> warnings;
    private Map<String, List<ReportData>> errors;

    @Inject
    private AttributeMapper mapper;

    public LAFProducer() {
        this.warnings = new HashMap<String, List<ReportData>>();
        this.errors = new HashMap<String, List<ReportData>>();
        this.probe = new LProbe();
        this.pKommentare = new ArrayList<LKommentarP>();
        this.mKommentare = new ArrayList<LKommentarM>();
        this.messungen = new ArrayList<LMessung>();
        this.lorte = new ArrayList<LOrt>();
        this.messwerte = new ArrayList<LMesswert>();
        String fileName = System.getProperty("de.intevation.lada.importconfig");
        LAFFormat format = new LAFFormat();
        format.readConfigFile(fileName);
        probenFormat = format.getFormat("probe");
        messungFormat = format.getFormat("messung");
        ortFormat = format.getFormat("ort");
    }

    @Override
    public void addData(String key, Object values)
    throws LAFParserException {
        String lKey = key.toLowerCase();
        if(lKey.equals("probenkommentar")) {
            LKommentarP kommentar = new LKommentarP();
            kommentar.setProbeId(this.probe.getProbeId());
            this.pKommentare.add(
                mapper.addAttribute(lKey, values, kommentar));
        }
        else if (lKey.equals("kommentar")) {
            LKommentarMId id = new LKommentarMId();
            id.setMessungsId(this.messung.getMessungsId());
            id.setProbeId(this.probe.getProbeId());
            LKommentarM kommentar = new LKommentarM();
            kommentar.setId(id);
            kommentar.setMessungsId(this.messung.getMessungsId());
            kommentar.setProbeId(this.probe.getProbeId());
            this.mKommentare.add(
                mapper.addAttribute(lKey, values, kommentar));
        }
        else if (lKey.equals("probenzusatzbeschreibung")) {
            LZusatzWertId zusatzId = new LZusatzWertId();
            zusatzId.setProbeId(this.probe.getProbeId());
            LZusatzWert wert = new LZusatzWert();
            wert.setId(zusatzId);
            LZusatzWert zusatzWert = mapper.addAttribute(lKey, values, wert);
            if (zusatzWert != null) {
                this.zusatzwerte.add(zusatzWert);
            }
            else {
                String ekey = probe.getProbeId() == null ? "probeId" : probe.getProbeId();
                List<ReportData> err = this.errors.get(ekey);
                if (err == null) {
                    this.errors.put(ekey, mapper.getErrors());
                }
                else {
                    err.addAll(mapper.getErrors());
                }
            }
        }
        else if (lKey.equals("pzb_s")) {
            LZusatzWertId zusatzId = new LZusatzWertId();
            zusatzId.setProbeId(this.probe.getProbeId());
            LZusatzWert wert = new LZusatzWert();
            wert.setId(zusatzId);
            LZusatzWert zusatzWert = mapper.addAttributeS(lKey, values, wert);
            if (zusatzWert != null) {
                this.zusatzwerte.add(zusatzWert);
            }
            else {
                String ekey = probe.getProbeId() == null ? "probeId" : probe.getProbeId();
                List<ReportData> err = this.errors.get(ekey);
                if (err == null) {
                    this.errors.put(ekey, mapper.getErrors());
                }
                else {
                    err.addAll(mapper.getErrors());
                }
            }
        }
        else if (lKey.equals("messwert")) {
            LMesswertId id = new LMesswertId();
            id.setProbeId(this.probe.getProbeId());
            id.setMessungsId(this.messung.getId().getMessungsId());
            LMesswert m = new LMesswert();
            m.setId(id);
            m.setMessungsId(this.messung.getMessungsId());
            m.setProbeId(this.probe.getProbeId());
            LMesswert wert = mapper.addAttribute(lKey, values, m);
            if (wert != null) {
                this.messwerte.add(wert);
            }
            else {
                String ekey = probe.getProbeId() == null ? "probeId" : probe.getProbeId();
                List<ReportData> err = this.errors.get(ekey);
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
        }
        else if (isValidProbe(lKey, values.toString())) {
            this.probe = mapper.addAttribute(lKey, values, this.probe);
            if (this.probe == null) {
                this.errors.put(values.toString(), mapper.getErrors());
                throw new LAFParserException(values.toString() + " exists");
            }
        }
        else if (isValidOrt(lKey, values.toString())) {
            this.ort = mapper.addAttribute(lKey, values, this.ort);
        }
    }

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

    @Override
    public LProbe getProbe() {
        return this.probe;
    }

    @Override
    public List<LMessung> getMessungen() {
        return this.messungen;
    }

    @Override
    public List<Ort> getOrte() {
        return this.orte;
    }

    @Override
    public List<LOrt> getLOrte() {
        return this.lorte;
    }
    @Override
    public List<LKommentarP> getProbenKommentare() {
        return this.pKommentare;
    }

    @Override
    public List<LKommentarM> getMessungsKommentare() {
        return this.mKommentare;
    }

    @Override
    public List<LMesswert> getMesswerte() {
        return this.messwerte;
    }

    @Override
    public List<LZusatzWert> getZusatzwerte() {
        return this.zusatzwerte;
    }

    @Override
    public void reset() {
        this.errors = new HashMap<String, List<ReportData>>();
        this.warnings = new HashMap<String, List<ReportData>>();
        this.probe = new LProbe();
        this.messungen = new ArrayList<LMessung>();
        this.messung = null;
        this.lorte = new ArrayList<LOrt>();
        this.orte = new ArrayList<Ort>();
        this.ort = null;
        this.messwerte = new ArrayList<LMesswert>();
        this.mKommentare = new ArrayList<LKommentarM>();
        this.pKommentare = new ArrayList<LKommentarP>();
        mapper.reset();
    }

    public void newMessung() {
        if (this.messung != null) {
            this.messungen.add(this.messung);
        }
        LMessungId id = new LMessungId();
        id.setProbeId(this.probe.getProbeId());
        this.messung = new LMessung();
        this.messung.setProbeId(this.probe.getProbeId());
        this.messung.setId(id);
    }

    public void newOrt() {
        if (this.ort != null) {
            Ort o = this.ort.toOrt();
            if (o != null) {
                this.orte.add(o);
            }
            LOrt lo = this.ort.toLOrt();
            if (lo != null) {
                this.lorte.add(lo);
            }
        }
        this.ort = new OrtCreator();
        this.ort.setProbeId(this.probe.getProbeId());
    }

    /**
     * @return the warnings
     */
    public Map<String, List<ReportData>> getWarnings() {
        if (this.probe == null) {
            return this.warnings;
        }
        String key = probe.getProbeId() == null ? "probeId" : probe.getProbeId();
        List<ReportData> warn = this.warnings.get(key);
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
    public Map<String, List<ReportData>> getErrors() {
        if (this.probe == null) {
            return this.errors;
        }
        String key = probe.getProbeId() == null ? "probeId" : probe.getProbeId();
        List<ReportData> err = this.errors.get(key);
        if (err == null) {
            this.errors.put(key, mapper.getErrors());
        }
        else {
            err.addAll(mapper.getErrors());
        }
        return this.errors;
    }

    @Override
    public void finishOrt() {
        if (orte.isEmpty()) {
            return;
        }
        
    }
}
