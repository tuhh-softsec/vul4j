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
import javax.persistence.Query;

import de.intevation.lada.exporter.Creator;
import de.intevation.lada.model.land.LKommentarM;
import de.intevation.lada.model.land.LKommentarP;
import de.intevation.lada.model.land.LMessung;
import de.intevation.lada.model.land.LMesswert;
import de.intevation.lada.model.land.LOrt;
import de.intevation.lada.model.land.LProbe;
import de.intevation.lada.model.land.LZusatzWert;
import de.intevation.lada.model.land.ProbeTranslation;
import de.intevation.lada.model.stamm.MessEinheit;
import de.intevation.lada.model.stamm.Messgroesse;
import de.intevation.lada.model.stamm.ProbenZusatz;
import de.intevation.lada.model.stamm.Probenart;
import de.intevation.lada.model.stamm.SOrt;
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
        lafProbe += probeToLAF(probeId);
        return lafProbe;
    }

    /**
     * Find the {@link LProbe} object and produce the LAF conform string.
     * @param probeId The {@link LProbe} id.
     * @return LAF conform string.
     */
    private String probeToLAF(String probeId) {
        Response found = repository.getById(LProbe.class, Integer.valueOf(probeId), "land");
        if (found.getData() == null) {
            return null;
        }
        LProbe aProbe = (LProbe)found.getData();
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
    private String writeAttributes(LProbe probe) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd HHmm");
        QueryBuilder<LKommentarP> kommBuilder =
            new QueryBuilder<LKommentarP>(
                repository.entityManager("land"), LKommentarP.class);
        kommBuilder.and("probeId", probe.getId());
        Response kommentar = repository.filter(kommBuilder.getQuery(), "land");
        List<LKommentarP> kommentare = (List<LKommentarP>)kommentar.getData();

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

        QueryBuilder<LZusatzWert> zusatzBuilder =
            new QueryBuilder<LZusatzWert>(
                repository.entityManager("land"), LZusatzWert.class);
        zusatzBuilder.and("probeId", probe.getId());
        Response zusatz = repository.filter(zusatzBuilder.getQuery(), "land");
        List<LZusatzWert> zusatzwerte = (List<LZusatzWert>)zusatz.getData();

        QueryBuilder<ProbeTranslation> transBuilder =
            new QueryBuilder<ProbeTranslation>(
                repository.entityManager("land"), ProbeTranslation.class);
        transBuilder.and("probe", probe.getId());
        Response trans = repository.filter(transBuilder.getQuery(), "land");
        List<ProbeTranslation> translation = (List<ProbeTranslation>)trans.getData();
        String laf = "";
        laf += probe.getDatenbasisId() == null ?
            "": lafLine("DATENBASIS_S", probe.getDatenbasisId().toString());
        laf += probe.getNetzbetreiberId() == null ?
            "" : lafLine("NETZKENNUNG", probe.getNetzbetreiberId());
        laf += probe.getMstId() == null ?
            "" : lafLine("MESSSTELLE", probe.getMstId());
        laf += lafLine("PROBE_ID", translation.get(0).getProbeIdAlt());
        laf += lafLine("HAUPTPROBENNUMMER", probe.getHauptprobenNr());
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
    @SuppressWarnings("unchecked")
    private String writeZusatzwert(LZusatzWert zw) {
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
        value += " " + zusatz.get(0).getMehId();
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
    private String writeOrt(LProbe probe) {
        QueryBuilder<LOrt> builder =
            new QueryBuilder<LOrt>(
                repository.entityManager("land"),
                LOrt.class);
        builder.and("probeId", probe.getId());
        Response objects = repository.filter(builder.getQuery(), "land");
        List<LOrt> orte = (List<LOrt>)objects.getData();

        String laf = "";
        for(LOrt o : orte) {
            laf += "%ORT%\n";
            QueryBuilder<SOrt> oBuilder =
                new QueryBuilder<SOrt>(
                    repository.entityManager("stamm"),
                    SOrt.class);
            oBuilder.and("id", o.getOrt());
            List<SOrt> sOrte=
                (List<SOrt>)repository.filter(
                    oBuilder.getQuery(),
                    "stamm").getData();

            laf += lafLine("ORT_CODE",
                "\"" + sOrte.get(0).getBezeichnung() + "\"");
            laf += lafLine("ORT_TYP", "\"" + o.getOrtsTyp() + "\"");
            laf += o.getOrtszusatztext() == null ? "":
                lafLine("ORT_ZUSATZTEXT", "\"" + o.getOrtszusatztext() + "\"");
            laf += lafLine("ORT_LAND_S", String.valueOf(sOrte.get(0).getStaatId()));
            String koord = "";
            koord += "05 ";
            koord += sOrte.get(0).getLongitude() + " ";
            koord += sOrte.get(0).getLatitude();
            //TODO: use table koordinatenart and koord*extern!
            laf += lafLine("ORT_KOORDINATEN_S", koord);
            laf += lafLine("ORT_GEMEINDESCHLUESSEL", sOrte.get(0).getVerwaltungseinheitId());
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
    private String writeMessung(LProbe probe) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd HHmm");
        // Get all messungen
        QueryBuilder<LMessung> builder =
            new QueryBuilder<LMessung>(
                repository.entityManager("land"),
                LMessung.class);
        builder.and("probeId", probe.getId());
        Response objects = repository.filter(builder.getQuery(), "land");
        List<LMessung> mess = (List<LMessung>)objects.getData();

        String laf = "";
        for(LMessung m : mess) {
            laf += "%MESSUNG%\n";
            QueryBuilder<LMesswert> wertBuilder =
                new QueryBuilder<LMesswert>(
                    repository.entityManager("land"), LMesswert.class);
            wertBuilder.and("messungsId", m.getId());
            Response messw = repository.filter(wertBuilder.getQuery(), "land");
            List<LMesswert> werte = (List<LMesswert>)messw.getData();
            QueryBuilder<LKommentarM> kommBuilder =
                new QueryBuilder<LKommentarM>(
                    repository.entityManager("land"), LKommentarM.class);
            kommBuilder.and("messungsId", m.getId());
            Response kommentar = repository.filter(kommBuilder.getQuery(), "land");
            List<LKommentarM> kommentare = (List<LKommentarM>)kommentar.getData();
            laf += lafLine("MESSUNGS_ID", m.getId().toString());
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
    private String writeMesswert(LMesswert mw) {
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

        String value = "\"" + groessen.get(0).getMessgroesse() + "\"";
        if (mw.getGrenzwertueberschreitung() != null &&
            !mw.getGrenzwertueberschreitung()) {
            value += " <";
        }
        else {
            value += " ";
        }
        value += mw.getMesswert();
        value += " \"" + einheiten.get(0).getEinheit() + "\"";
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
