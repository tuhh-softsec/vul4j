package de.intevation.lada.data.importer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import de.intevation.lada.data.Repository;
import de.intevation.lada.model.LKommentarM;
import de.intevation.lada.model.LKommentarP;
import de.intevation.lada.model.LMessung;
import de.intevation.lada.model.LMessungId;
import de.intevation.lada.model.LMesswert;
import de.intevation.lada.model.LOrt;
import de.intevation.lada.model.LProbe;
import de.intevation.lada.model.LProbeInfo;
import de.intevation.lada.model.SMessEinheit;
import de.intevation.lada.model.SMessgroesse;
import de.intevation.lada.rest.Response;

@Stateless
public class AttributeMapper
{
    @Inject
    private EntityManager em;

    @Inject
    @Named("lproberepository")
    private Repository probeRepo;

    @Inject
    @Named("readonlyrepository")
    private Repository sRepository;

    private List<ReportData> warnings;
    private List<ReportData> errors;

    public AttributeMapper() {
        this.warnings = new ArrayList<ReportData>();
        this.errors = new ArrayList<ReportData>();
    }
    public LProbe addAttribute(String key, Object value, LProbe probe) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd HHmm");
        if ("datenbasis_s".equals(key)) {
            Integer v = Integer.valueOf(value.toString());
            probe.setDatenbasisId(v);
        }
        else if ("probe_id".equals(key)) {
            if (probeRepo.findById(LProbeInfo.class, value.toString()) != null) {
                errors.add(new ReportData("probe_id", value.toString(), 662));
                return null;
            }
            probe.setProbeId(value.toString());
        }
        else if ("hauptprobennummer".equals(key)) {
            probe.setHauptprobenNr(value.toString());
        }
        else if ("mpr_id".equals(key)) {
            Integer v = Integer.valueOf(value.toString());
            probe.setMprId(v);
        }
        else if ("netzkennung".equals(key)) {
            probe.setNetzbetreiberId(value.toString());
        }
        else if ("messprogramm_land".equals(key)) {
            probe.setMplId(value.toString());
        }
        else if ("messstelle".equals(key)) {
            probe.setMstId(value.toString());
        }
        else if ("betriebsart".equals(key)) {
            probe.setBaId(value.toString());
        }
        else if ("soll_datum_uhrzeit_a".equals(key)) {
            try {
                Date d = format.parse(value.toString());
                probe.setSolldatumBeginn(d);
            }
            catch (ParseException e) {
                //TODO handle warning.
            }
        }
        else if ("soll_datum_uhrzeit_e".equals(key)) {
            try {
                Date d = format.parse(value.toString());
                probe.setSolldatumEnde(d);
            }
            catch (ParseException e) {
                //TODO handle warning.
            }
        }
        else if ("probenahme_datum_uhrzeit_a".equals(key)) {
            try {
                Date d = format.parse(value.toString());
                probe.setProbeentnahmeBeginn(d);
            }
            catch (ParseException e) {
                //TODO handle warning.
            }
        }
        else if ("probenahme_datum_uhrzeit_e".equals(key)) {
            try {
                Date d = format.parse(value.toString());
                probe.setProbeentnahmeEnde(d);
            }
            catch (ParseException e) {
                //TODO handle warning.
            }
        }
        else if ("umweltbereich_s".equals(key)) {
            probe.setUmwId(value.toString());
        }
        else if ("deskriptoren".equals(key)) {
            probe.setMediaDesk(value.toString());
        }
        else if ("testdaten".equals(key)) {
            if (!value.toString().equals("0")) {
                probe.setTest(true);
            }
            else {
                probe.setTest(false);
            }
        }
        
        probe.setProbenartId(1);
        return probe;
    }

    public LKommentarP addAttribute(
        String key,
        Object values,
        LKommentarP kommentar
    ) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd HHmm");
        String v = values.toString();
        String erzeuger = v.substring(1, 5);
        String date = v.substring(8, 21);
        Date d;
        try {
            d = format.parse(date);
            kommentar.setKDatum(d);
        }
        catch (ParseException e) {
            //TODO: handle warning.
        }
        String text = v.substring(23, v.length() -1);
        kommentar.setErzeuger(erzeuger);
        kommentar.setKText(text);
        return kommentar;
    }

    public LKommentarM addAttribute(
        String key,
        Object values,
        LKommentarM kommentar
    ) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd HHmm");
        String v = values.toString();
        String erzeuger = v.substring(1, 5);
        String date = v.substring(8, 21);
        Date d;
        try {
            d = format.parse(date);
            kommentar.setKDatum(d);
        }
        catch (ParseException e) {
            //TODO: handle warning.
        }
        String text = v.substring(23, v.length() -1);
        kommentar.setErzeuger(erzeuger);
        kommentar.setKText(text);
        return kommentar;
    }

    public LMessung addAttribute(
        String key,
        Object values,
        LMessung messung
    ) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd HHmm");
        if ("messungs_id".equals(key)) {
            LMessungId id = messung.getId();
            Integer v = Integer.valueOf(values.toString());
            id.setMessungsId(v);
            messung.setMessungsId(v);
            messung.setId(id);
        }
        else if ("nebenprobennummer".equals(key)) {
            messung.setNebenprobenNr(values.toString());
        }
        else if ("mess_datum_uhrzeit".equals(key)) {
            try {
                Date d = format.parse(values.toString());
                messung.setMesszeitpunkt(d);
            }
            catch (ParseException e) {
                //TODO: handle warnings.
            }
        }
        else if ("messzeit_sekunden".equals(key)) {
            Integer i = Integer.valueOf(values.toString());
            messung.setMessdauer(i);
        }
        else if ("messmethode_s".equals(key)) {
            messung.setMmtId(values.toString());
        }
        else if ("bearbeitungsstatus".equals(key)) {
            //ignored.!?
        }
        else if ("erfassung_abgeschlossen".equals(key)) {
            if(!values.toString().equals("0")) {
                messung.setFertig(true);
            }
            else {
                messung.setFertig(false);
            }
        }
        return messung;
    }

    public LMesswert addAttribute(
        String key,
        Object values,
        LMesswert messwert
    ) {
        Pattern p = Pattern.compile(
            "(\".+\")( .+ )(\".+\")( .*)( .{1,12})( .{1,9})(.{0,9})(.{0,3})");
        //TODO Does not perfectly match... Use better matching for floats.
        Matcher m = p.matcher(values.toString());
        if (m.matches()) {
            String messgroesse = m.group(1).substring(1, m.group(1).length() - 1);
            String wert = m.group(2);
            String einheit = m.group(3).substring(1, m.group(3).length() - 1);
            if (wert.startsWith(" <")) {
                wert = wert.substring(2);
                messwert.setGrenzwertueberschreitung(false);
            }
            else if (wert.startsWith(" >")) {
                wert = wert.substring(2);
                messwert.setGrenzwertueberschreitung(true);
            }
            float fWert = Float.valueOf(wert);
            messwert.setMesswert(fWert);
            Response responseEinheit = sRepository.findAll(SMessEinheit.class);
            List<SMessEinheit> einheiten = 
                (List<SMessEinheit>)responseEinheit.getData();
            boolean foundEinheit = false;
            for (SMessEinheit e: einheiten) {
                if(e.getEinheit().equals(einheit)) {
                    foundEinheit = true;
                    messwert.setMehId((int) e.getMehId());
                }
            }
            if (!foundEinheit) {
                return null;
                //TODO: handle warning!
            }
            Response responseGroesse = sRepository.findAll(SMessgroesse.class);
            List<SMessgroesse> messgroessen = 
                (List<SMessgroesse>)responseGroesse.getData();
            boolean foundGroesse = false;
            for (SMessgroesse g: messgroessen) {
                if(g.getMessgro0esse().equals(messgroesse)) {
                    foundGroesse = true;
                    messwert.setMessgroesseId(g.getMessgroesseId());
                    messwert.getId().setMessgroesseId(g.getMessgroesseId());
                }
            }
            if (!foundGroesse) {
                return null;
                //TODO: handle warning!
            }
        }
        //TODO: Match the other values.
        return messwert;
    }

    public LOrt addAttribute(
        String key,
        Object values,
        LOrt ort
    ) {
        if ("ort_id".equals(key)) {
            Integer v = Integer.valueOf(values.toString());
            ort.setOrtId(v);
        }
        else if ("ort_typ".equals(key)) {
            ort.setOrtsTyp(values.toString());
        }
        return ort;
    }

    /**
     * @return the warnings
     */
    public List<ReportData> getWarnings() {
        return warnings;
    }

    /**
     * @return the errors
     */
    public List<ReportData> getErrors() {
        return errors;
    }

    public void reset() {
        errors.clear();
        warnings.clear();
    }
}
