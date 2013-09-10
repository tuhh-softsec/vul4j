package de.intevation.lada.data.importer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import de.intevation.lada.data.QueryBuilder;
import de.intevation.lada.data.Repository;
import de.intevation.lada.model.LKommentarM;
import de.intevation.lada.model.LKommentarP;
import de.intevation.lada.model.LMessung;
import de.intevation.lada.model.LMessungId;
import de.intevation.lada.model.LMesswert;
import de.intevation.lada.model.LProbe;
import de.intevation.lada.model.LProbeInfo;
import de.intevation.lada.model.LZusatzWert;
import de.intevation.lada.model.SDatenbasis;
import de.intevation.lada.model.SMessEinheit;
import de.intevation.lada.model.SMessgroesse;
import de.intevation.lada.model.SProbenZusatz;
import de.intevation.lada.model.SProbenart;
import de.intevation.lada.model.SUmwelt;
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
    @Named("ortrepository")
    private Repository ortRepo;

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
        if ("datenbasis_s".equals(key) && probe.getDatenbasisId() == null) {
            Integer v = Integer.valueOf(value.toString());
            probe.setDatenbasisId(v);
        }
        else if ("datenbasis_s".equals(key) && probe.getDatenbasisId() != null){
            this.warnings.add(new ReportData(key, value.toString(), 672));
        }

        if ("datenbasis".equals(key) && probe.getDatenbasisId() == null) {
            QueryBuilder<SDatenbasis> builder =
                new QueryBuilder<SDatenbasis>(
                    this.sRepository.getEntityManager(), SDatenbasis.class);
            builder.and("datenbasis", value.toString());
            Response response = sRepository.filter(builder.getQuery());
            List<SDatenbasis> datenbasis = (List<SDatenbasis>)response.getData();
            Integer v = Integer.valueOf(datenbasis.get(0).getDatenbasisId());
            probe.setDatenbasisId(v);
        }
        else if ("datenbasis".equals(key) && probe.getDatenbasisId() != null){
            this.warnings.add(new ReportData(key, value.toString(), 672));
        }

        if ("probe_id".equals(key)) {
            Response response =
                probeRepo.findById(LProbeInfo.class, value.toString());
            List<LProbeInfo> info = (List<LProbeInfo>)response.getData();
            if (info != null && info.size() > 0) {
                errors.add(new ReportData("probe_id", value.toString(), 671));
                return null;
            }
            probe.setProbeId(value.toString());
        }

        if ("hauptprobennummer".equals(key)) {
            probe.setHauptprobenNr(value.toString());
        }

        if ("mpr_id".equals(key)) {
            Integer v = Integer.valueOf(value.toString());
            probe.setMprId(v);
        }

        if ("netzkennung".equals(key)) {
            probe.setNetzbetreiberId(value.toString());
        }

        if ("messstelle".equals(key)) {
            probe.setMstId(value.toString());
        }

        if ("messprogramm_s".equals(key) && probe.getBaId() == null) {
            probe.setBaId(value.toString());
        }
        else if ("messprogramm_s".equals(key) && probe.getBaId() != null){
            this.warnings.add(new ReportData(key, value.toString(), 672));
        }

        if ("soll_datum_uhrzeit_a".equals(key)) {
            try {
                Date d = format.parse(value.toString());
                probe.setSolldatumBeginn(d);
            }
            catch (ParseException e) {
                this.warnings.add(new ReportData(key, value.toString(), 674));
            }
        }
        if ("soll_datum_uhrzeit_e".equals(key)) {
            try {
                Date d = format.parse(value.toString());
                probe.setSolldatumEnde(d);
            }
            catch (ParseException e) {
                this.warnings.add(new ReportData(key, value.toString(), 674));
            }
        }
        if ("probenahme_datum_uhrzeit_a".equals(key)) {
            try {
                Date d = format.parse(value.toString());
                probe.setProbeentnahmeBeginn(d);
            }
            catch (ParseException e) {
                this.warnings.add(new ReportData(key, value.toString(), 674));
            }
        }
        if ("probenahme_datum_uhrzeit_e".equals(key)) {
            try {
                Date d = format.parse(value.toString());
                probe.setProbeentnahmeEnde(d);
            }
            catch (ParseException e) {
                this.warnings.add(new ReportData(key, value.toString(), 674));
            }
        }

        if ("umweltbereich_s".equals(key) && probe.getUmwId() == null) {
            probe.setUmwId(value.toString());
        }
        else if ("umweltbereich_s".equals(key) && probe.getUmwId() != null){
            this.warnings.add(new ReportData(key, value.toString(), 672));
        }
        if ("umweltbereich_c".equals(key) && probe.getUmwId() == null) {
            QueryBuilder<SUmwelt> builder =
                new QueryBuilder<SUmwelt>(
                    sRepository.getEntityManager(), SUmwelt.class);
            int length = value.toString().length() > 80 ? 80 : value.toString().length();
            builder.and("umweltBereich", value.toString().substring(0, length));
            Response response = sRepository.filter(builder.getQuery());
            List<SUmwelt> umw = (List<SUmwelt>)response.getData();
            probe.setUmwId(umw.get(0).getUmwId());
        }
        else if ("umweltbereich_c".equals(key) && probe.getUmwId() != null){
            this.warnings.add(new ReportData(key, value.toString(), 672));
        }

        if ("deskriptoren".equals(key)) {
            probe.setMediaDesk(value.toString());
        }

        if ("testdaten".equals(key)) {
            if (!value.toString().equals("0")) {
                probe.setTest(true);
            }
            else {
                probe.setTest(false);
            }
        }

        if ("medium".equals(key)) {
            probe.setMedia(value.toString());
        }

        if ("probenart".equals(key)) {
            QueryBuilder<SProbenart> builder =
                new QueryBuilder<SProbenart>(
                    sRepository.getEntityManager(), SProbenart.class);
            builder.and("probenart", value.toString());
            Response response = sRepository.filter(builder.getQuery());
            List<SProbenart> art = (List<SProbenart>)response.getData();
            probe.setProbenartId(Integer.valueOf(art.get(0).getProbenartId()));
        }
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
            this.warnings.add(new ReportData(key, values.toString(), 674));
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
            this.warnings.add(new ReportData(key, values.toString(), 674));
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
                this.warnings.add(new ReportData(key, values.toString(), 674));
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
                this.errors.add(new ReportData("messeinheit", "null", 673));
                return null;
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
                this.errors.add(new ReportData("messgroesse", "null", 673));
                return null;
            }
        }
        return messwert;
    }

    public OrtCreator addAttribute(
        String key,
        Object values,
        OrtCreator ort
    ) {
        if ("ort_code".equals(key)) {
            ort.setOrtCode(values.toString());
        }
        if ("ort_typ".equals(key)) {
            ort.setOrtTyp(values.toString());
        }
        if ("ort_zusatz".equals(key)) {
            ort.setZusatztext(values.toString());
        }
        if ("ort_land_lang".equals(key)) {
            ort.setLandLang(values.toString());
        }
        if ("ort_land_kurz".equals(key)) {
            ort.setLandKurz(values.toString());
        }
        if ("ort_land_s".equals(key)) {
            ort.setLandS(values.toString());
        }
        if ("ort_gemeindeschlüssel".equals(key)) {
            ort.setGemSchluessel(values.toString());
        }
        if ("ort_bezeichnung".equals(key)) {
            ort.setBezeichnung(values.toString());
        }
        if ("ort_beschreibung".equals(key)) {
            ort.setBeschreibung(values.toString());
        }
        if ("ort_nuts_code".equals(key)) {
            ort.setNuts(values.toString());
        }
        if ("ort_hoehe_land".equals(key)) {
            ort.setHoehe(values.toString());
        }
        if ("ort_koordinaten".equals(key)) {
            ort.setKoordinaten(values.toString());
        }
        if ("ort_koordinaten_s".equals(key)) {
            ort.setKoordinatenS(values.toString());
        }
        return ort;
    }

    public LZusatzWert addAttribute(
        String lKey,
        Object values,
        LZusatzWert wert
    ) {
        String v = values.toString().substring(1);
        int ndx = v.indexOf("\"");
        String groesse = v.substring(0, ndx);
        v = v.substring(ndx + 2);
        ndx = v.indexOf(" ");
        String w = v.substring(0, ndx);
        v = v.substring(ndx + 2);
        ndx = v.indexOf("\"");
        String einheit = v.substring(0, ndx);
        String fehler = v.substring(ndx + 2);
        QueryBuilder<SProbenZusatz> builder =
            new QueryBuilder<SProbenZusatz>(
                sRepository.getEntityManager(), SProbenZusatz.class);
        builder.and("zusatzwert", groesse);
        Response response = sRepository.filter(builder.getQuery());
        List<SProbenZusatz> list = (List<SProbenZusatz>)response.getData();
        if (list == null || list.isEmpty()) {
            this.errors.add(new ReportData(lKey, "zusatzwert", 673));
            return null;
        }
        wert.getId().setPzsId(list.get(0).getPzsId());
        wert.setPzsId(list.get(0).getPzsId());
        wert.setMesswertPzs(Float.valueOf(w));
        wert.setMessfehler(Float.valueOf(fehler));
        return wert;
    }

    public LZusatzWert addAttributeS(
        String lKey,
        Object values,
        LZusatzWert wert
    ) {
        String v = values.toString().substring(1);
        int ndx = v.indexOf("\"");
        String groesse = v.substring(0, ndx);
        v = v.substring(ndx + 2);
        ndx = v.indexOf(" ");
        String w = v.substring(0, ndx);
        v = v.substring(ndx + 2);
        ndx = v.indexOf(" ");
        String einheit = v.substring(0, ndx);
        String fehler = v.substring(ndx + 2);
        wert.getId().setPzsId(groesse);
        wert.setPzsId(groesse);
        wert.setMesswertPzs(Float.valueOf(w));
        wert.setMessfehler(Float.valueOf(fehler));
        return wert;
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
        errors = new ArrayList<ReportData>();
        warnings = new ArrayList<ReportData>();
    }
}
