package de.intevation.lada.data.exporter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import de.intevation.lada.data.QueryBuilder;
import de.intevation.lada.data.Repository;
import de.intevation.lada.model.LKommentarM;
import de.intevation.lada.model.LKommentarP;
import de.intevation.lada.model.LMessung;
import de.intevation.lada.model.LMesswert;
import de.intevation.lada.model.LOrt;
import de.intevation.lada.model.LProbe;
import de.intevation.lada.model.LProbeInfo;
import de.intevation.lada.model.LZusatzWert;
import de.intevation.lada.model.Ort;
import de.intevation.lada.model.SKoordinatenArt;
import de.intevation.lada.model.SMessEinheit;
import de.intevation.lada.model.SMessgroesse;
import de.intevation.lada.model.SProbenZusatz;
import de.intevation.lada.model.SProbenart;
import de.intevation.lada.rest.Response;

/**
 * This creator produces a LAF conform String containing all information about
 * a single {@link LProbe} object including subobjects like
 * {@link LMessung}, {@link LMesswert}, {@link LKommentarP}...
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Named("lafcreator")
public class LAFCreator
implements Creator
{

    @Inject
    @Named("lproberepository")
    private Repository proben;

    @Inject
    @Named("lmessungrepository")
    private Repository messungRepo;

    @Inject
    @Named("lortrepository")
    private Repository ortRepo;

    @Inject
    @Named("lmessungrepository")
    private Repository messwertRepo;

    @Inject
    @Named("lkommentarmrepository")
    private Repository mkommentarRepo;

    @Inject
    @Named("lkommentarRepository")
    private Repository pkommentarRepo;

    @Inject
    @Named("lzusatzwertrepository")
    private Repository zusatzwertRepo;

    @Inject
    @Named("readonlyrepository")
    private Repository readonlyRepo;

    /**
     * Create the LAF conform String.
     *
     * @param probeId   The {@link LProbe} id.
     */
    @Override
    public String create(String probeId) {
        String lafProbe = "%PROBE%\n";
        lafProbe += probeToLAF(probeId);
        return lafProbe;
    }

    /**
     * Find the {@link LProbe} object and produce the LAF conform string.
     * @param probeId The {@link LProbe} id.
     * @return LAF conform string.
     */
    private String probeToLAF(String probeId) {
        Response found = this.proben.findById(LProbe.class, probeId);
        if (found.getData() == null) {
            return null;
        }
        ArrayList<LProbeInfo> aProbe = (ArrayList<LProbeInfo>)found.getData();
        LProbeInfo probe = aProbe.get(0);
        String lafProbe = writeAttributes(probe);
        return lafProbe;
    }

    /**
     * Write the attributes and subobjects.
     *
     * @param probe The {@link LProbeInfo} object.
     * @return LAF conform string.
     */
    private String writeAttributes(LProbeInfo probe) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd HHmm");
        QueryBuilder<LKommentarP> kommBuilder =
            new QueryBuilder<LKommentarP>(
                pkommentarRepo.getEntityManager(), LKommentarP.class);
        kommBuilder.and("probeId", probe.getProbeId());
        Response kommentar = mkommentarRepo.filter(kommBuilder.getQuery());
        List<LKommentarP> kommentare = (List<LKommentarP>)kommentar.getData();

        QueryBuilder<SProbenart> artBuilder =
            new QueryBuilder<SProbenart>(
                readonlyRepo.getEntityManager(), SProbenart.class);
        artBuilder.and("probenartId", probe.getProbenartId());
        Response art = readonlyRepo.filter(artBuilder.getQuery());
        List<SProbenart> probenart = (List<SProbenart>)art.getData();

        QueryBuilder<LZusatzWert> zusatzBuilder =
            new QueryBuilder<LZusatzWert>(
                zusatzwertRepo.getEntityManager(), LZusatzWert.class);
        zusatzBuilder.and("probeId", probe.getProbeId());
        Response zusatz = zusatzwertRepo.filter(zusatzBuilder.getQuery());
        List<LZusatzWert> zusatzwerte = (List<LZusatzWert>)zusatz.getData();

        String laf = "";
        laf += probe.getDatenbasisId() == null ?
            "": lafLine("DATENBASIS_S", probe.getDatenbasisId().toString());
        laf += probe.getNetzbetreiberId() == null ?
            "" : lafLine("NETZKENNUNG", probe.getNetzbetreiberId());
        laf += probe.getMstId() == null ?
            "" : lafLine("MESSSTELLE", probe.getMstId());
        laf += lafLine("PROBE_ID", probe.getProbeId());
        laf += lafLine("HAUPTPROBENNUMMER", probe.getHauptprobenNr());
        laf += probe.getBaId() == null ?
            "" : lafLine("MESSPROGRAMM_S", "\"" + probe.getBaId() + "\"");
        laf += probe.getProbenartId() == null ?
            "" : lafLine("PROBENART",
                "\"" + probenart.get(0).getProbenart() + "\"");
        laf += probe.getSolldatumBeginn() == null ?
            "" : lafLine("SOLL_DATUM_UHRZEIT_A",
                format.format(probe.getSolldatumBeginn()));
        laf += probe.getSolldatumEnde() == null ?
            "" : lafLine("SOLL_DATUM_UHRZEIT_E",
                format.format(probe.getSolldatumEnde()));
        laf += probe.getProbeentnahmeBeginn() == null ?
            "" : lafLine("PROBENAHME_DATUM_UHRZEIT_A",
                format.format(probe.getProbeentnahmeBeginn()));
        laf += probe.getProbeentnahmeEnde() == null ?
            "" : lafLine("PROBENAHME_DATUM_UHRZEIT_E",
                format.format(probe.getProbeentnahmeEnde()));
        laf += probe.getUmwId() == null ?
            "" : lafLine("UMWELTBEREICH_S", probe.getUmwId());
        laf += probe.getMedia() == null ?
            "" : lafLine("MEDIUM", "\"" + probe.getMedia() + "\"");
        laf += probe.getMediaDesk() == null ?
            "" : lafLine("DESKRIPTOREN", "\"" + probe.getMediaDesk() + "\"");
        laf += probe.getTest() == Boolean.TRUE ?
            lafLine("TESTDATEN", "1") : lafLine("TESTDATEN", "0");
        for (LZusatzWert zw : zusatzwerte) {
            laf += writeZusatzwert(zw);
        }
        for (LKommentarP kp : kommentare) {
            laf += writeKommentar(kp);
        }
        laf += writeMessung(probe);
        laf += writeOrt(probe);
        return laf;
    }

    /**
     * Write {@link LZusatzWert} attributes.
     *
     * @param zw    The {@link LZusatzWert}.
     * @return Single LAF line.
     */
    private String writeZusatzwert(LZusatzWert zw) {
        QueryBuilder<SProbenZusatz> builder =
            new QueryBuilder<SProbenZusatz>(
                readonlyRepo.getEntityManager(), SProbenZusatz.class);
        builder.and("pzsId", zw.getPzsId());
        List<SProbenZusatz> zusaetze = 
            (List<SProbenZusatz>)readonlyRepo.filter(builder.getQuery());

        String value = "\"" + zusaetze.get(0).getBeschreibung() + "\"";
        value += " " + zw.getMesswertPzs();
        value += " " + zusaetze.get(0).getMehId();
        value += " " + zw.getMessfehler();
        return lafLine("PZB_S", value);
    }

    /**
     * Write {@link LOrt} attributes.
     *
     * @param probe The {@link LProbeInfo} object.
     * @return LAF conform string
     */
    private String writeOrt(LProbeInfo probe) {
        QueryBuilder<LOrt> builder =
            new QueryBuilder<LOrt>(
                ortRepo.getEntityManager(),
                LOrt.class);
        builder.and("probeId", probe.getProbeId());
        Response objects = ortRepo.filter(builder.getQuery());
        List<LOrt> orte = (List<LOrt>)objects.getData();

        String laf = "";
        for(LOrt o : orte) {
            laf += "%ORT%\n";
            QueryBuilder<Ort> oBuilder =
                new QueryBuilder<Ort>(
                    readonlyRepo.getEntityManager(), Ort.class);
            oBuilder.and("ortId", o.getOrtId());
            Response resp = readonlyRepo.filter(oBuilder.getQuery());
            List<Ort> ort = (List<Ort>)resp.getData();
            laf += lafLine("ORT_CODE",
                "\"" + ort.get(0).getBezeichnung() + "\"");
            laf += lafLine("ORT_TYP", "\"" + o.getOrtsTyp() + "\"");
            laf += o.getOrtszusatztext() == null ? "":
                lafLine("ORT_ZUSATZTEXT", "\"" + o.getOrtszusatztext() + "\"");
            laf += lafLine("ORT_LAND_S", String.valueOf(ort.get(0).getStaatId()));
            QueryBuilder<SKoordinatenArt> kaBuilder =
                new QueryBuilder<SKoordinatenArt>(
                    readonlyRepo.getEntityManager(), SKoordinatenArt.class);
            String koord = "";
            koord += ort.get(0).getLatitude() + " ";
            koord += ort.get(0).getLongitude() + " ";
            koord += "UTM (WGS84)";
            //TODO: use table koordinatenart and koord*extern!
            laf += lafLine("ORT_KOORDINATEN_S", koord);
            laf += lafLine("ORT_GEMEINDESCHLUESSEL", ort.get(0).getGemId());
        }
        return laf;
    }

    /**
     * Write {@link LKommentarP} attributes.
     *
     * @param kp    The {@link LKommentarP} object.
     * @return Single LAF line.
     */
    private String writeKommentar(LKommentarP kp) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd HHmm");
        String value = "\"" + kp.getErzeuger() + "\" " +
            format.format(kp.getKDatum()) + " " +
            "\"" + kp.getKText() + "\"";
        return lafLine("PROBENKOMMENTAR", value);
    }

    /**
     * Write {@link LMessung} attributes.
     *
     * @param probe The {@link LProbeInfo} object.
     * @return LAF conform string.
     */
    private String writeMessung(LProbeInfo probe) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd HHmm");
        // Get all messungen
        QueryBuilder<LMessung> builder =
            new QueryBuilder<LMessung>(
                messungRepo.getEntityManager(),
                LMessung.class);
        builder.and("probeId", probe.getProbeId());
        Response objects = messungRepo.filter(builder.getQuery());
        List<LMessung> mess = (List<LMessung>)objects.getData();

        String laf = "";
        for(LMessung m : mess) {
            laf += "%MESSUNG%\n";
            QueryBuilder<LMesswert> wertBuilder =
                new QueryBuilder<LMesswert>(
                    messwertRepo.getEntityManager(), LMesswert.class);
            wertBuilder.and("probeId", probe.getProbeId()).and("messungsId", m.getMessungsId());
            Response messw = messwertRepo.filter(wertBuilder.getQuery());
            List<LMesswert> werte = (List<LMesswert>)messw.getData();
            QueryBuilder<LKommentarM> kommBuilder =
                new QueryBuilder<LKommentarM>(
                    mkommentarRepo.getEntityManager(), LKommentarM.class);
            kommBuilder.and("probeId", probe.getProbeId()).and("messungsId", m.getMessungsId());
            Response kommentar = mkommentarRepo.filter(kommBuilder.getQuery());
            List<LKommentarM> kommentare = (List<LKommentarM>)kommentar.getData();
            laf += lafLine("MESSUNGS_ID", m.getMessungsId().toString());
            laf += lafLine("NEBENPROBENNUMMER", m.getNebenprobenNr());
            laf += m.getMesszeitpunkt() == null ?
                "" : lafLine(
                    "MESS_DATUM_UHRZEIT",
                    format.format(m.getMesszeitpunkt()));
            laf += m.getMessdauer() == null ?
                "" : lafLine("MESSZEIT_SEKUNDEN", m.getMessdauer().toString());
            laf += m.getMmtId() == null ?
                "" : lafLine("MESSMETHODE_S", m.getMmtId());
            for (LMesswert mw : werte) {
                laf += writeMesswert(mw);
            }
            for (LKommentarM mk: kommentare) {
                laf += writeKommentar(mk);
            }
        }
        return laf;
    }

    /**
     * Write {@link LKommentarM} attributes.
     * @param mk    The {@link LKommentarM} object.
     * @return Single LAF line.
     */
    private String writeKommentar(LKommentarM mk) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd HHmm");
        String value = "\"" + mk.getErzeuger() + "\" " +
            format.format(mk.getKDatum()) + " " +
            "\"" + mk.getKText() + "\"";
        return lafLine("KOMMENTAR", value);
    }

    /**
     * Write {@link LMesswert} attributes.
     * @param mw    The {@link LMesswert} object.
     * @return Single LAF line.
     */
    private String writeMesswert(LMesswert mw) {
        QueryBuilder<SMessgroesse> builder =
            new QueryBuilder<SMessgroesse>(
                readonlyRepo.getEntityManager(), SMessgroesse.class);
        builder.and("messgroesseId", mw.getMessgroesseId());
        Response r = readonlyRepo.filter(builder.getQuery());
        List<SMessgroesse> sm = (List<SMessgroesse>)r.getData();
        QueryBuilder<SMessEinheit> einheitBuilder =
            new QueryBuilder<SMessEinheit>(
                readonlyRepo.getEntityManager(), SMessEinheit.class);
        einheitBuilder.and("mehId", mw.getMehId());
        Response re = readonlyRepo.filter(einheitBuilder.getQuery());
        List<SMessEinheit> me = (List<SMessEinheit>)re.getData();
        String value = "\"" + sm.get(0).getMessgro0esse() + "\"";
        if (mw.getGrenzwertueberschreitung() != null &&
            !mw.getGrenzwertueberschreitung()) {
            value += " <";
        }
        else {
            value += " ";
        }
        value += mw.getMesswert();
        value += " \"" + me.get(0).getEinheit() + "\"";
        value += mw.getMessfehler() == null ? " NULL" : " " + mw.getMessfehler();
        value += mw.getNwgZuMesswert() == null ? " NULL" : " " + mw.getNwgZuMesswert();
        value += mw.getGrenzwertueberschreitung() == null ? " N" :
            mw.getGrenzwertueberschreitung() ? " Y" : " N";
        return lafLine("MESSWERT", value);
    }

    /**
     * Write a single LAF conform line from key and value.
     *
     * @param key   The key.
     * @param value The value.
     * @return LAF conform line.
     */
    private String lafLine(String key, String value) {
        for (int i = key.length(); i < 30; i++) {
            key += " ";
        }
        return key + value + "\n";
    }
}
