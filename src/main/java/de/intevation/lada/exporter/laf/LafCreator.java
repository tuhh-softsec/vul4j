/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.exporter.laf;

import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import de.intevation.lada.exporter.Creator;
import de.intevation.lada.model.land.KommentarM;
import de.intevation.lada.model.land.KommentarP;
import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.Messwert;
import de.intevation.lada.model.land.Ortszuordnung;
import de.intevation.lada.model.land.Probe;
import de.intevation.lada.model.land.StatusProtokoll;
import de.intevation.lada.model.land.ZusatzWert;
import de.intevation.lada.model.stammdaten.MessEinheit;
import de.intevation.lada.model.stammdaten.MessStelle;
import de.intevation.lada.model.stammdaten.Messgroesse;
import de.intevation.lada.model.stammdaten.Ort;
import de.intevation.lada.model.stammdaten.GemeindeUntergliederung;
import de.intevation.lada.model.stammdaten.ProbenZusatz;
import de.intevation.lada.model.stammdaten.Probenart;
import de.intevation.lada.model.stammdaten.DatensatzErzeuger;
import de.intevation.lada.model.stammdaten.MessprogrammKategorie;
import de.intevation.lada.model.stammdaten.Probenehmer;
import de.intevation.lada.model.stammdaten.ReiProgpunktGruppe;
import de.intevation.lada.model.stammdaten.StatusKombi;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.auth.UserInfo;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.util.rest.Response;

// import org.apache.log4j.Logger;

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
    // @Inject private Logger logger;

    // Some format strings corresponding to LAF notation
    private static final String KEY_FORMAT = "%-30s";
    private static final String DEFAULT_FORMAT = "%s";
    private static final String CN = "\"%s\""; // cn, mcn, scn

    @Inject
    @AuthorizationConfig(type=AuthorizationType.HEADER)
    private Authorization authorization;

    /**
     * The repository used to read data.
     */
    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    private UserInfo userInfo;

    /**
     * Create the LAF conform String.
     *
     * @param probeId   The {@link LProbe} id.
     */
    @Override
    public String createProbe(String probeId) {
        String lafProbe = "%PROBE%\n";
        lafProbe += lafLine("UEBERTRAGUNGSFORMAT", "7", CN);
        lafProbe += lafLine("VERSION", "0084", CN);
        lafProbe += probeToLAF(probeId, new ArrayList<Integer>());
        return lafProbe;
    }

    /**
     * Create the LAF conform String.
     *
     * @param probeId   The {@link LProbe} id.
     */
    @Override
    public String createMessung(String probeId, List<Integer> messungen) {
        String lafProbe = "%PROBE%\n";
        lafProbe += lafLine("UEBERTRAGUNGSFORMAT", "7", CN);
        lafProbe += lafLine("VERSION", "0084", CN);
        lafProbe += probeToLAF(probeId, messungen);
        return lafProbe;
    }

    /**
     * Find the {@link LProbe} object and produce the LAF conform string.
     * @param probeId The {@link LProbe} id.
     * @return LAF conform string.
     */
    private String probeToLAF(String probeId, List<Integer> messungen) {
        Response found = repository.getById(Probe.class, Integer.valueOf(probeId), Strings.LAND);
        if (found.getData() == null) {
            return null;
        }
        Probe aProbe = (Probe)found.getData();
        String lafProbe = writeAttributes(aProbe, messungen);
        return lafProbe;
    }

    /**
     * Write the attributes and subobjects.
     *
     * @param probe The {@link LProbeInfo} object.
     * @return LAF conform string.
     */
    @SuppressWarnings("unchecked")
    private String writeAttributes(Probe probe, List<Integer> messungen) {
        QueryBuilder<KommentarP> kommBuilder =
            new QueryBuilder<KommentarP>(
                repository.entityManager(Strings.LAND), KommentarP.class);
        kommBuilder.and("probeId", probe.getId());
        Response kommentar = repository.filter(kommBuilder.getQuery(), Strings.LAND);
        List<KommentarP> kommentare = (List<KommentarP>)kommentar.getData();

        QueryBuilder<Probenart> builder =
            new QueryBuilder<Probenart>(
                repository.entityManager(Strings.STAMM),
                Probenart.class);
        builder.and("id", probe.getProbenartId());
        List<Probenart> probenarten =
            (List<Probenart>)repository.filter(
                builder.getQuery(),
                Strings.STAMM).getData();
        String probenart = probenarten.get(0).getProbenart();

        MessStelle messstelle =
            repository.getByIdPlain(MessStelle.class, probe.getMstId(), Strings.STAMM);

        QueryBuilder<ZusatzWert> zusatzBuilder =
            new QueryBuilder<ZusatzWert>(
                repository.entityManager(Strings.LAND), ZusatzWert.class);
        zusatzBuilder.and("probeId", probe.getId());
        Response zusatz = repository.filter(zusatzBuilder.getQuery(), Strings.LAND);
        List<ZusatzWert> zusatzwerte = (List<ZusatzWert>)zusatz.getData();

        String laf = "";
        laf += probe.getDatenbasisId() == null ?
            "": lafLine("DATENBASIS_S",
                String.format("%02d", probe.getDatenbasisId()));
        laf += messstelle == null ?
            "" : lafLine("NETZKENNUNG", messstelle.getNetzbetreiberId(), CN);
        laf += probe.getMstId() == null ?
            "" : lafLine("MESSSTELLE", probe.getMstId(), CN);
        laf += probe.getLaborMstId() == null ?
            "" : lafLine("MESSLABOR", probe.getLaborMstId(), CN);
        laf += lafLine("PROBE_ID", probe.getExterneProbeId(), CN);
        laf += probe.getHauptprobenNr() == null ?
            "" : lafLine("HAUPTPROBENNUMMER", probe.getHauptprobenNr(), CN);
        if (probe.getBaId() != null && probe.getDatenbasisId() != null) {
            if (probe.getDatenbasisId() == 4) {
                if (probe.getBaId() == 1 ) {
                    laf += lafLine("MESSPROGRAMM_S", 4, CN);
                } else if (probe.getBaId() == 2 ) {
                    laf += lafLine("MESSPROGRAMM_S", 5, CN);
                } else if (probe.getBaId() == 3 ) {
                    laf += lafLine("MESSPROGRAMM_S", 6, CN);
                } else {
                    laf += lafLine("MESSPROGRAMM_S", "\""+(char)probe.getBaId().intValue()+"\"");
                }
            } else {
                if (probe.getBaId() > 3 ) {
                    laf += lafLine("MESSPROGRAMM_S", "\""+(char)probe.getBaId().intValue()+"\"");
                } else if (probe.getBaId() == 3 ) {
                    laf += lafLine("MESSPROGRAMM_S", 2, CN);
                } else {
                    laf += lafLine("MESSPROGRAMM_S", probe.getBaId(), CN);
                }
            }
        }
        laf += probe.getProbenartId() == null ?
            "" : lafLine("PROBENART", probenart, CN);
        laf += lafLine("ZEITBASIS_S", "2");
        laf += probe.getSolldatumBeginn() == null ?
            "" : lafLine("SOLL_DATUM_UHRZEIT_A",
                toUTCString(probe.getSolldatumBeginn()));
        laf += probe.getSolldatumEnde() == null ?
            "" : lafLine("SOLL_DATUM_UHRZEIT_E",
                toUTCString(probe.getSolldatumEnde()));
        laf += probe.getProbeentnahmeBeginn() == null ?
            "" : lafLine("PROBENAHME_DATUM_UHRZEIT_A",
                toUTCString(probe.getProbeentnahmeBeginn()));
        laf += probe.getProbeentnahmeEnde() == null ?
            "" : lafLine("PROBENAHME_DATUM_UHRZEIT_E",
                toUTCString(probe.getProbeentnahmeEnde()));
        laf += probe.getUmwId() == null ?
            "" : lafLine("UMWELTBEREICH_S", probe.getUmwId(), CN);
        laf += probe.getMediaDesk() == null ?
            "" : lafLine("DESKRIPTOREN",
                probe.getMediaDesk().replaceAll(" ", "").substring(2), CN);
        laf += probe.getTest() == Boolean.TRUE ?
            lafLine("TESTDATEN", "1") : lafLine("TESTDATEN", "0");
        if (probe.getErzeugerId() != null) {
            DatensatzErzeuger erz = repository.getByIdPlain(
                DatensatzErzeuger.class, probe.getErzeugerId(), "stamm");
            laf += lafLine("ERZEUGER", erz.getDatensatzErzeugerId(), CN);
        }
        if (probe.getMplId() != null) {
            MessprogrammKategorie mpkat = repository.getByIdPlain(
                MessprogrammKategorie.class, probe.getMplId(), "stamm");
            laf += lafLine("MESSPROGRAMM_LAND", mpkat.getCode(), CN);
        }
        if (probe.getProbeNehmerId() != null) {
            Probenehmer prn = repository.getByIdPlain(
                Probenehmer.class, probe.getProbeNehmerId(), "stamm");
            laf += lafLine("PROBENAHMEINSTITUTION", prn.getPrnId(), CN);
        }
        if (probe.getReiProgpunktGrpId() != null) {
            ReiProgpunktGruppe rpg = repository.getByIdPlain(
                ReiProgpunktGruppe.class, probe.getReiProgpunktGrpId(), "stamm");
            laf += lafLine("REI_PROGRAMMPUNKTGRUPPE", rpg.getReiProgPunktGruppe(), CN);
        }
        for (ZusatzWert zw : zusatzwerte) {
            laf += writeZusatzwert(zw);
        }
        for (KommentarP kp : kommentare) {
            laf += writeKommentar(kp);
        }
        laf += writeOrt(probe);
        laf += writeMessung(probe, messungen);
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
                repository.entityManager(Strings.STAMM),
                ProbenZusatz.class);
        builder.and("id", zw.getPzsId());
        List<ProbenZusatz> zusatz =
            (List<ProbenZusatz>)repository.filter(
                builder.getQuery(),
                Strings.STAMM).getData();

        String value = "\"" + zusatz.get(0).getId() + "\"";
        value += ((zw.getKleinerAls() == null) ? " " : " " + zw.getKleinerAls());
        value += zw.getMesswertPzs();
        value += " " + zusatz.get(0).getMessEinheitId();
        value += " " + ((zw.getMessfehler() == null) ? "" : zw.getMessfehler());
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
                repository.entityManager(Strings.LAND),
                Ortszuordnung.class);
        builder.and("probeId", probe.getId());
        Response objects = repository.filter(builder.getQuery(), Strings.LAND);
        List<Ortszuordnung> orte =
            (List<Ortszuordnung>)objects.getData();

        String laf = "";
        for(Ortszuordnung o : orte) {
            String type = "";
            if ("E".equals(o.getOrtszuordnungTyp()) ||
                "R".equals(o.getOrtszuordnungTyp())) {
                laf += writeOrtData(o,"P_");
            }
        }
        for(Ortszuordnung o : orte) {
            String type = "";
            if ("U".equals(o.getOrtszuordnungTyp()) ||
                "R".equals(o.getOrtszuordnungTyp())) {
                laf += "%URSPRUNGSORT%\n";
                laf += writeOrtData(o,"U_");
            }
        }
        return laf;
    }

    /**
     * Write {@link LOrt} attributes.
     *
     * @param Ortszuordnung
     * @return LAF conform string
     */
    @SuppressWarnings("unchecked")
    private String writeOrtData(Ortszuordnung o, String typePrefix) {
        String laf = "";
        if (o.getOrtszusatztext() != null &&
                o.getOrtszusatztext().length() > 0) {
                laf += lafLine(typePrefix + "ORTS_ZUSATZTEXT",
                    o.getOrtszusatztext(), CN);
        }
        QueryBuilder<Ort> oBuilder =
            new QueryBuilder<Ort>(
                repository.entityManager(Strings.STAMM),
                Ort.class);
        oBuilder.and("id", o.getOrtId());
        List<Ort> sOrte=
            (List<Ort>)repository.filter(
                oBuilder.getQuery(),
                Strings.STAMM).getData();

        if (sOrte.get(0).getStaatId() != null) {
            laf += lafLine(typePrefix + "HERKUNFTSLAND_S",
                String.format("%08d", sOrte.get(0).getStaatId()));
        }

        if (sOrte.get(0).getGemId() != null && 
            sOrte.get(0).getGemId().length() > 0) {
            laf += lafLine(typePrefix + "GEMEINDESCHLUESSEL",
                sOrte.get(0).getGemId());
        }

        if (sOrte.get(0).getNutsCode() != null && 
            sOrte.get(0).getNutsCode().length() > 0) {
            laf += lafLine(typePrefix + "NUTS_CODE",
                sOrte.get(0).getNutsCode());
        }

        if (sOrte.get(0).getGemUntId() != null) {
            GemeindeUntergliederung gu = repository.getByIdPlain(
                GemeindeUntergliederung.class, sOrte.get(0).getGemUntId(), "stamm");
            laf += lafLine(typePrefix + "ORTS_ZUSATZKENNZAHL",
                gu.getOzkId(), CN);
        }

        String koord = String.format("%02d", sOrte.get(0).getKdaId());
        koord += " \"";
        koord += sOrte.get(0).getKoordXExtern() + "\" \"";
        koord += sOrte.get(0).getKoordYExtern() + "\"";
        laf += lafLine(typePrefix + "KOORDINATEN_S", koord);

        if ("P_".equals(typePrefix) && sOrte.get(0).getOzId() != null) {
            laf += lafLine(typePrefix + "ORTS_ZUSATZCODE", sOrte.get(0).getOzId(), CN);
        }
        else if ("U_".equals(typePrefix) && "R".equals(o.getOrtszuordnungTyp())) {
            laf += lafLine(typePrefix + "ORTS_ZUSATZCODE", sOrte.get(0).getOrtId(), CN);
        }
        else if ("U_".equals(typePrefix) && sOrte.get(0).getOzId() != null) {
            laf += lafLine(typePrefix + "ORTS_ZUSATZCODE", sOrte.get(0).getOzId(), CN);
        } 
//        if (sOrte.get(0).getHoeheUeberNn() != null) {
//            laf += lafLine(typePrefix + "HOEHE_NN",
//                String.format("%f", sOrte.get(0).getHoeheUeberNn()));
//        }
        return laf;
    }

    /**
     * Write {@link LKommentarP} attributes.
     *
     * @param kp    The {@link LKommentarP} object.
     * @return Single LAF line.
     */
    private String writeKommentar(KommentarP kp) {
        String value = "\"" + kp.getMstId() + "\" " +
            toUTCString(kp.getDatum()) + " " +
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
    private String writeMessung(Probe probe, List<Integer> messungen) {
        List<Messung> mess = new ArrayList<>();
        QueryBuilder<Messung> builder =
            new QueryBuilder<Messung>(
                repository.entityManager(Strings.LAND),
                Messung.class);
        if (messungen.isEmpty()) {
            // Get all messungen
            builder.and("probeId", probe.getId());
        }
        else {
            builder.andIn("id", messungen);
        }
        mess = repository.filterPlain(builder.getQuery(), Strings.LAND);

        String laf = "";
        for(Messung m : mess) {
            laf += "%MESSUNG%\n";
            QueryBuilder<Messwert> wertBuilder =
                new QueryBuilder<Messwert>(
                    repository.entityManager(Strings.LAND), Messwert.class);
            wertBuilder.and("messungsId", m.getId());
            Response messw = repository.filter(wertBuilder.getQuery(), Strings.LAND);
            List<Messwert> werte = (List<Messwert>)messw.getData();
            QueryBuilder<KommentarM> kommBuilder =
                new QueryBuilder<KommentarM>(
                    repository.entityManager(Strings.LAND), KommentarM.class);
            kommBuilder.and("messungsId", m.getId());
            Response kommentar = repository.filter(kommBuilder.getQuery(), Strings.LAND);
            List<KommentarM> kommentare = (List<KommentarM>)kommentar.getData();
            laf += lafLine("MESSUNGS_ID", m.getExterneMessungsId().toString());
            laf += m.getNebenprobenNr() == null ?
                "" : lafLine("NEBENPROBENNUMMER", m.getNebenprobenNr(), CN);
            laf += m.getMesszeitpunkt() == null ?
                "" : lafLine("MESS_DATUM_UHRZEIT", toUTCString(m.getMesszeitpunkt()));
            laf += m.getMessdauer() == null ?
                "" : lafLine("MESSZEIT_SEKUNDEN", m.getMessdauer().toString());
            laf += m.getMmtId() == null ?
                "" : lafLine("MESSMETHODE_S", m.getMmtId(), CN);
            laf += lafLine("ERFASSUNG_ABGESCHLOSSEN", (m.getFertig() ? "1" : "0"));
//            laf += lafLine("PEP_FLAG", (m.getGeplant() ? "1" : "0"));
            laf += lafLine("BEARBEITUNGSSTATUS", writeStatus(m));
            if (this.userInfo != null &&
                authorization.isAuthorized(this.userInfo, m, Messung.class)
            ) {
                for (Messwert mw : werte) {
                    laf += writeMesswert(mw);
                }
            }
            for (KommentarM mk: kommentare) {
                laf += writeKommentar(mk);
            }
        }
        return laf;
    }

    /**
     * Write out the status protocol as string with 4 character:
     *  1. status level mst
     *  2. status level land
     *  3. status level lst
     *  4. set to 0 (unused)
     *
     * @param messung the messung containing the status
     * @return 4 character string
     */
    private String writeStatus(Messung messung) {
        Integer status[] = {0, 0, 0};
        StatusProtokoll currentStatus = repository.getByIdPlain(
            StatusProtokoll.class,
            messung.getStatus(),
            Strings.LAND);
        StatusKombi currentKombi = repository.getByIdPlain(
            StatusKombi.class, 
            currentStatus.getStatusKombi(), 
            Strings.STAMM);
        Integer currenStufe = currentKombi.getStatusStufe().getId();
        if (currenStufe == 1) {
            status[0] = currentKombi.getStatusWert().getId();
        }
        else {
            QueryBuilder<StatusProtokoll> builder =
                new QueryBuilder<StatusProtokoll>(
                    repository.entityManager(Strings.LAND), StatusProtokoll.class);
            builder.and("messungsId", messung.getId());
            builder.andIn("statusKombi", Arrays.asList(1, 2, 3, 4, 5, 14));
            builder.orderBy("datum", false);
            StatusProtokoll mst = repository.filterPlain(builder.getQuery(), Strings.LAND).get(0);
            Integer mstKombi = mst.getStatusKombi();
            StatusKombi kombi = repository.getByIdPlain(StatusKombi.class, mstKombi, Strings.STAMM);
            if (currenStufe == 2) {
                status[1] = currentKombi.getStatusWert().getId();
            }
            else {
                builder = builder.getEmptyBuilder();
                builder.and("messungsId", messung.getId());
                builder.andIn("statusKombi", Arrays.asList(6, 7, 8, 9, 15));
                builder.orderBy("datum", false);
                List<StatusProtokoll> land = repository.filterPlain(builder.getQuery(), Strings.LAND);
                if (!land.isEmpty()) {
                    Integer landKombi = land.get(0).getStatusKombi();
                    StatusKombi lKombi = repository.getByIdPlain(StatusKombi.class, landKombi, Strings.STAMM);
                    status[1] = lKombi.getStatusWert().getId();
                }
                status[2] = currentKombi.getStatusWert().getId();
            }
        }
        if (status[0] == 0 && status[1] != 0) {
            status[0] = 1;
        }
        return "" + status[0] + status[1] + status[2] + "0";
    }

    /**
     * Write {@link LKommentarM} attributes.
     * @param mk    The {@link LKommentarM} object.
     * @return Single LAF line.
     */
    private String writeKommentar(KommentarM mk) {
        String value = "\"" + mk.getMstId() + "\" " +
            toUTCString(mk.getDatum()) + " " +
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
                repository.entityManager(Strings.STAMM),
                Messgroesse.class);
        builder.and("id", mw.getMessgroesseId());
        List<Messgroesse> groessen =
            (List<Messgroesse>)repository.filter(
                builder.getQuery(),
                Strings.STAMM).getData();

        QueryBuilder<MessEinheit> eBuilder =
            new QueryBuilder<MessEinheit>(
                repository.entityManager(Strings.STAMM),
                MessEinheit.class);
        eBuilder.and("id", mw.getMehId());
        List<MessEinheit> einheiten =
            (List<MessEinheit>)repository.filter(
                eBuilder.getQuery(),
                Strings.STAMM).getData();

        String tag = "MESSWERT";
        String value = "\"" + groessen.get(0).getMessgroesse() + "\"";
        value += " ";
        value += mw.getMesswertNwg() == null ? " " : mw.getMesswertNwg();
        value += mw.getMesswertNwg() == null ? mw.getMesswert() : mw.getNwgZuMesswert();
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
            value += " " + (mw.getNwgZuMesswert() == null ? "0.0": mw.getNwgZuMesswert());
            value += " " + (mw.getGrenzwertueberschreitung() == null ? " N" :
                mw.getGrenzwertueberschreitung() ? " J" : " N");
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
        return lafLine(key, value, DEFAULT_FORMAT);
    }

    /**
     * Write a single LAF conform line from key and value.
     *
     * @param key    The key.
     * @param value  The value.
     * @param format A format string for the value
     * @return LAF conform line.
     */
    private String lafLine(String key, Object value, String format) {
        return String.format(KEY_FORMAT, key)
            + String.format(format, value)
            + "\n";
    }

    private String toUTCString(Timestamp timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmm");
        return formatter.format(timestamp.toInstant().atZone(ZoneOffset.UTC));
    }

	@Override
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
}
