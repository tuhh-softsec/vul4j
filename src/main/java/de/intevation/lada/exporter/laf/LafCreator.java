/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.exporter.laf;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import de.intevation.lada.exporter.Creator;
import de.intevation.lada.model.land.KommentarM;
import de.intevation.lada.model.land.KommentarP;
import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.Messwert;
import de.intevation.lada.model.land.Ortszuordnung;
import de.intevation.lada.model.land.Probe;
import de.intevation.lada.model.land.ZusatzWert;
import de.intevation.lada.model.stammdaten.MessEinheit;
import de.intevation.lada.model.stammdaten.MessStelle;
import de.intevation.lada.model.stammdaten.Messgroesse;
import de.intevation.lada.model.stammdaten.Ort;
import de.intevation.lada.model.stammdaten.ProbenZusatz;
import de.intevation.lada.model.stammdaten.Probenart;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.Response;

/**
 * This creator produces a LAF conform String containing all information about
 * a single {@link LProbe} object including subobjects like
 * {@link LMessung}, {@link LMesswert}, {@link LKommentarP}...
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Named("lafcreator")
public class LafCreator
implements Creator
{
    @Inject
    private Logger logger;
    /**
     * The repository used to read data.
     */
    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    /**
     * Create the LAF conform String.
     *
     * @param probeId   The {@link LProbe} id.
     */
    @Override
    public String create(String probeId) {
        String lafProbe = "%PROBE%\n";
        lafProbe += "UEBERTRAGUNGSFORMAT           7\n";
        lafProbe += "VERSION                       0084\n";
        lafProbe += probeToLAF(probeId);
        return lafProbe;
    }

    /**
     * Find the {@link LProbe} object and produce the LAF conform string.
     * @param probeId The {@link LProbe} id.
     * @return LAF conform string.
     */
    private String probeToLAF(String probeId) {
        Response found = repository.getById(Probe.class, Integer.valueOf(probeId), "land");
        if (found.getData() == null) {
            return null;
        }
        Probe aProbe = (Probe)found.getData();
        String lafProbe = writeAttributes(aProbe);
        return lafProbe;
    }

    /**
     * Write the attributes and subobjects.
     *
     * @param probe The {@link LProbeInfo} object.
     * @return LAF conform string.
     */
    @SuppressWarnings("unchecked")
    private String writeAttributes(Probe probe) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd HHmm");
        QueryBuilder<KommentarP> kommBuilder =
            new QueryBuilder<KommentarP>(
                repository.entityManager("land"), KommentarP.class);
        kommBuilder.and("probeId", probe.getId());
        Response kommentar = repository.filter(kommBuilder.getQuery(), "land");
        List<KommentarP> kommentare = (List<KommentarP>)kommentar.getData();

        QueryBuilder<Probenart> builder =
            new QueryBuilder<Probenart>(
                repository.entityManager("stamm"),
                Probenart.class);
        builder.and("id", probe.getProbenartId());
        List<Probenart> probenarten =
            (List<Probenart>)repository.filter(
                builder.getQuery(),
                "stamm").getData();
        String probenart = probenarten.get(0).getProbenart();

        MessStelle messstelle =
            repository.getByIdPlain(MessStelle.class, probe.getMstId(), "stamm");

        QueryBuilder<ZusatzWert> zusatzBuilder =
            new QueryBuilder<ZusatzWert>(
                repository.entityManager("land"), ZusatzWert.class);
        zusatzBuilder.and("probeId", probe.getId());
        Response zusatz = repository.filter(zusatzBuilder.getQuery(), "land");
        List<ZusatzWert> zusatzwerte = (List<ZusatzWert>)zusatz.getData();

        String laf = "";
        laf += probe.getDatenbasisId() == null ?
            "": lafLine("DATENBASIS_S",
                String.format("%02d", probe.getDatenbasisId()));
        laf += messstelle == null ?
            "" : lafLine("NETZKENNUNG", messstelle.getNetzbetreiberId());
        laf += probe.getMstId() == null ?
            "" : lafLine("MESSSTELLE", probe.getMstId());
        laf += probe.getLaborMstId() == null ?
            "" : lafLine("MESSLABOR", probe.getLaborMstId());
        laf += lafLine("PROBE_ID", probe.getIdAlt());
        laf += probe.getHauptprobenNr() == null ?
            "" : lafLine("HAUPTPROBENNUMMER", probe.getHauptprobenNr());
        laf += probe.getBaId() == null ?
            "" : lafLine("MESSPROGRAMM_S", "\"" + probe.getBaId() + "\"");
        laf += probe.getProbenartId() == null ?
            "" : lafLine("PROBENART",
                "\"" + probenart + "\"");
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
        laf += probe.getMediaDesk() == null ?
            "" : lafLine("DESKRIPTOREN", "\"" +
                probe.getMediaDesk().replaceAll(" ", "").substring(2) + "\"");
        laf += probe.getTest() == Boolean.TRUE ?
            lafLine("TESTDATEN", "1") : lafLine("TESTDATEN", "0");
        laf += writeOrt(probe);
        for (ZusatzWert zw : zusatzwerte) {
            laf += writeZusatzwert(zw);
        }
        for (KommentarP kp : kommentare) {
            laf += writeKommentar(kp);
        }
        laf += writeMessung(probe);
        return laf;
    }

    /**
     * Write {@link LZusatzWert} attributes.
     *
     * @param zw    The {@link LZusatzWert}.
     * @return Single LAF line.
     */
    @SuppressWarnings("unchecked")
    private String writeZusatzwert(ZusatzWert zw) {
        QueryBuilder<ProbenZusatz> builder =
            new QueryBuilder<ProbenZusatz>(
                repository.entityManager("stamm"),
                ProbenZusatz.class);
        builder.and("id", zw.getPzsId());
        List<ProbenZusatz> zusatz =
            (List<ProbenZusatz>)repository.filter(
                builder.getQuery(),
                "stamm").getData();

        String value = "\"" + zusatz.get(0).getBeschreibung() + "\"";
        value += " " + zw.getMesswertPzs();
        value += " " + zusatz.get(0).getMessEinheitId();
        value += " " + zw.getMessfehler();
        return lafLine("PZB_S", value);
    }

    /**
     * Write {@link LOrt} attributes.
     *
     * @param probe The {@link LProbeInfo} object.
     * @return LAF conform string
     */
    @SuppressWarnings("unchecked")
    private String writeOrt(Probe probe) {
        QueryBuilder<Ortszuordnung> builder =
            new QueryBuilder<Ortszuordnung>(
                repository.entityManager("land"),
                Ortszuordnung.class);
        builder.and("probeId", probe.getId());
        Response objects = repository.filter(builder.getQuery(), "land");
        List<Ortszuordnung> orte =
            (List<Ortszuordnung>)objects.getData();

        String laf = "";
        for(Ortszuordnung o : orte) {
            String type = "";
            if ("E".equals(o.getOrtszuordnungTyp())) {
                type = "P_";
            }
            else if ("U".equals(o.getOrtszuordnungTyp())) {
                type = "U_";
                laf += "%URSPRUNGSORT%\n";
            }
            else {
                continue;
            }
            if (o.getOrtszusatztext() != null &&
                o.getOrtszusatztext().length() > 0) {
                laf += lafLine(type + "ORTS_ZUSATZTEXT", o.getOrtszusatztext());
            }
            QueryBuilder<Ort> oBuilder =
                new QueryBuilder<Ort>(
                    repository.entityManager("stamm"),
                    Ort.class);
            oBuilder.and("id", o.getOrtId());
            List<Ort> sOrte=
                (List<Ort>)repository.filter(
                    oBuilder.getQuery(),
                    "stamm").getData();

            if (sOrte.get(0).getStaatId() != null) {
                laf += lafLine(type + "HERKUNFTSLAND_S",
                    String.format("%08d", sOrte.get(0).getStaatId()));
            }

            if (sOrte.get(0).getGemId() != null &&
                sOrte.get(0).getGemId().length() > 0) {
                laf += lafLine(type + "GEMEINDESCHLUESSEL",
                    sOrte.get(0).getGemId());
            }

            String koord = String.format("%02d", sOrte.get(0).getKdaId());
            koord += " ";
            koord += sOrte.get(0).getKoordXExtern() + " ";
            koord += sOrte.get(0).getKoordYExtern();
            laf += lafLine(type + "KOORDINATEN_S", koord);

            if (sOrte.get(0).getOzId() != null &&
                sOrte.get(0).getOzId().length() > 0) {
                laf += lafLine(type + "ORTS_ZUSATZCODE", sOrte.get(0).getOzId());
            }
            if (sOrte.get(0).getHoeheUeberNn() != null) {
                laf += lafLine(type + "HOEHE_NN",
                    String.format("%f", sOrte.get(0).getHoeheUeberNn()));
            }
        }
        return laf;
    }

    /**
     * Write {@link LKommentarP} attributes.
     *
     * @param kp    The {@link LKommentarP} object.
     * @return Single LAF line.
     */
    private String writeKommentar(KommentarP kp) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd HHmm");
        String value = "\"" + kp.getMstId() + "\" " +
            format.format(kp.getDatum()) + " " +
            "\"" + kp.getText() + "\"";
        return lafLine("PROBENKOMMENTAR", value);
    }

    /**
     * Write {@link LMessung} attributes.
     *
     * @param probe The {@link LProbeInfo} object.
     * @return LAF conform string.
     */
    @SuppressWarnings("unchecked")
    private String writeMessung(Probe probe) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd HHmm");
        // Get all messungen
        QueryBuilder<Messung> builder =
            new QueryBuilder<Messung>(
                repository.entityManager("land"),
                Messung.class);
        builder.and("probeId", probe.getId());
        Response objects = repository.filter(builder.getQuery(), "land");
        List<Messung> mess = (List<Messung>)objects.getData();

        String laf = "";
        for(Messung m : mess) {
            laf += "%MESSUNG%\n";
            QueryBuilder<Messwert> wertBuilder =
                new QueryBuilder<Messwert>(
                    repository.entityManager("land"), Messwert.class);
            wertBuilder.and("messungsId", m.getId());
            Response messw = repository.filter(wertBuilder.getQuery(), "land");
            List<Messwert> werte = (List<Messwert>)messw.getData();
            QueryBuilder<KommentarM> kommBuilder =
                new QueryBuilder<KommentarM>(
                    repository.entityManager("land"), KommentarM.class);
            kommBuilder.and("messungsId", m.getId());
            Response kommentar = repository.filter(kommBuilder.getQuery(), "land");
            List<KommentarM> kommentare = (List<KommentarM>)kommentar.getData();
            laf += lafLine("MESSUNGS_ID", m.getIdAlt().toString());
            laf += lafLine("NEBENPROBENNUMMER", m.getNebenprobenNr());
            laf += m.getMesszeitpunkt() == null ?
                "" : lafLine(
                    "MESS_DATUM_UHRZEIT",
                    format.format(m.getMesszeitpunkt()));
            laf += m.getMessdauer() == null ?
                "" : lafLine("MESSZEIT_SEKUNDEN", m.getMessdauer().toString());
            laf += m.getMmtId() == null ?
                "" : lafLine("MESSMETHODE_S", m.getMmtId());
            laf += lafLine("ERFASSUNG_ABGESCHLOSSEN", (m.getFertig() ? "1" : "0"));
            for (Messwert mw : werte) {
                laf += writeMesswert(mw);
            }
            for (KommentarM mk: kommentare) {
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
    private String writeKommentar(KommentarM mk) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd HHmm");
        String value = "\"" + mk.getMstId() + "\" " +
            format.format(mk.getDatum()) + " " +
            "\"" + mk.getText() + "\"";
        return lafLine("KOMMENTAR", value);
    }

    /**
     * Write {@link LMesswert} attributes.
     * @param mw    The {@link LMesswert} object.
     * @return Single LAF line.
     */
    @SuppressWarnings("unchecked")
    private String writeMesswert(Messwert mw) {
        QueryBuilder<Messgroesse> builder =
            new QueryBuilder<Messgroesse>(
                repository.entityManager("stamm"),
                Messgroesse.class);
        builder.and("id", mw.getMessgroesseId());
        List<Messgroesse> groessen =
            (List<Messgroesse>)repository.filter(
                builder.getQuery(),
                "stamm").getData();

        QueryBuilder<MessEinheit> eBuilder =
            new QueryBuilder<MessEinheit>(
                repository.entityManager("stamm"),
                MessEinheit.class);
        eBuilder.and("id", mw.getMehId());
        List<MessEinheit> einheiten =
            (List<MessEinheit>)repository.filter(
                eBuilder.getQuery(),
                "stamm").getData();

        String tag = "MESSWERT";
        String value = "\"" + groessen.get(0).getMessgroesse() + "\"";
        value += " ";
        value += mw.getMesswertNwg() == null ? " " : mw.getMesswertNwg();
        value += mw.getMesswert();
        value += " \"" + einheiten.get(0).getEinheit() + "\"";
        value += mw.getMessfehler() == null ? " 0.0" : " " + mw.getMessfehler();
        if (mw.getGrenzwertueberschreitung() == null ||
            !mw.getGrenzwertueberschreitung()) {
            if (mw.getNwgZuMesswert() != null) {
                tag += "_NWG";
                value += " " + mw.getNwgZuMesswert();
            }
        }
        else {
            tag += "_NWG_G";
            value += " " + mw.getNwgZuMesswert() == null ? "0.0": mw.getNwgZuMesswert();
            value += " " + mw.getGrenzwertueberschreitung() == null ? " N" :
                mw.getGrenzwertueberschreitung() ? " J" : " N";
        }
        return lafLine(tag, value);
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
