package de.intevation.lada.data.importer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import de.intevation.lada.auth.AuthenticationResponse;
import de.intevation.lada.data.Repository;
import de.intevation.lada.model.LKommentarM;
import de.intevation.lada.model.LKommentarP;
import de.intevation.lada.model.LMessung;
import de.intevation.lada.model.LMesswert;
import de.intevation.lada.model.LOrt;
import de.intevation.lada.model.LProbe;
import de.intevation.lada.model.Ort;
import de.intevation.lada.validation.ValidationException;
import de.intevation.lada.validation.Validator;

/**
 * Writer to persist new entities in the database.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Named("lafwriter")
public class LAFWriter
implements Writer
{
    @Inject
    private EntityManager em;

    @Inject
    @Named("lprobevalidator")
    private Validator probeValidator;
    @Inject
    @Named("lmessungvalidator")
    private Validator messungValidator;
    @Inject
    @Named("lortvalidator")
    private Validator ortValidator;
    @Inject
    @Named("lmesswertvalidator")
    private Validator messwertValidator;

    @Inject
    @Named("lproberepository")
    private Repository probeRepository;
    @Inject
    @Named("lmessungrepository")
    private Repository messungRepository;
    @Inject
    @Named("lortrepository")
    private Repository lortRepository;
    @Inject
    @Named("ortrepository")
    private Repository ortRepository;
    @Inject
    @Named("lkommentarRepository")
    private Repository pKommentarRepository;
    @Inject
    @Named("lkommentarmrepository")
    private Repository mKommentarRepository;
    @Inject
    @Named("lmesswertrepository")
    private Repository messwertRepository;

    private List<ReportData> errors;
    private List<ReportData> warnings;

    /**
     * Default constructor.
     */
    public LAFWriter() {
        errors = new ArrayList<ReportData>();
        warnings = new ArrayList<ReportData>();
    }

    /**
     * Write a new {@link LProbe} object to the database using
     * authorization and validation.
     *
     * @param auth      The authentication information.
     * @param probe     The new {@link LProbe} object.
     * @return success
     */
    @Override
    public boolean writeProbe(AuthenticationResponse auth, LProbe probe) {
        if (!authorized(probe, auth)) {
            errors.add(new ReportData("auth", "not authorized", 699));
            return false;
        }
        try {
            Map<String, Integer> warn =
                probeValidator.validate(probe, false);
            if (warn != null) {
                for (String key: warn.keySet()) {
                    warnings.add(new ReportData(key, "validation", warn.get(key)));
                }
            }
        }
        catch (ValidationException e) {
            Map<String, Integer> err = e.getErrors();
            for(String key: err.keySet()) {
                errors.add(new ReportData(key, "validation", err.get(key)));
            }
            Map<String, Integer> warn = e.getWarnings();
            if (warn != null) {
                for (String key: warn.keySet()) {
                    warnings.add(new ReportData(key, "validation", warn.get(key)));
                }
            }
            if (probe.getProbeId() == null) {
                errors.add(new ReportData("probeId", "missing", 673));
            }
            return false;
        }
        if (probe.getProbeId() == null) {
            errors.add(new ReportData("probeId", "missing", 673));
            return false;
        }
        persist(probe);
        return true;
    }

    /**
     * Write new {@link LMessung} objects to the database using
     * authorization and validation.
     *
     * @param auth      The authentication information.
     * @param messungen The new {@link LMessung} objects.
     * @return success
     */
    @Override
    public boolean writeMessungen(
        AuthenticationResponse auth,
        List<LMessung> messungen
    ) {
        for(LMessung messung: messungen) {
            try {
                Map<String, Integer> warn =
                    messungValidator.validate(messung, false);
                messungRepository.create(messung);
                if (warn != null) {
                    for (String key : warn.keySet()) {
                        warnings.add(
                            new ReportData(key, "validation", warn.get(key)));
                    }
                }
            }
            catch (ValidationException e) {
                Map<String, Integer> err = e.getErrors();
                for(String key: err.keySet()) {
                    errors.add(
                        new ReportData(key, "validation", err.get(key)));
                }
                Map<String, Integer> warn = e.getWarnings();
                if (warn != null) {
                    for (String key: warn.keySet()) {
                        warnings.add(
                            new ReportData(key, "validation", warn.get(key)));
                    }
                }
                return false;
            }
        }

        return true;
    }

    /**
     * Write new {@link Ort} objects to the database.
     *
     * @param auth      The authentication information.
     * @param orte      List of {@link Ort} objects.
     * @return success
     */
    @Override
    public boolean writeOrte(AuthenticationResponse auth, List<Ort> orte) {
        for (Ort ort :orte) {
            ortRepository.create(ort);
        }
        return true;
    }

    /**
     * Write new {@link LOrt} objects to the database using validation.
     *
     * @param auth  The authentication information.
     * @param orte  List of {@link LOrt} objects.
     * @return success
     */
    @Override
    public boolean writeLOrte(AuthenticationResponse auth, List<LOrt> orte) {
        for(LOrt ort: orte) {
            try {
                Map<String, Integer> warn =
                    ortValidator.validate(ort, false);
                lortRepository.create(ort);
                if (warn != null) {
                    for (String key : warn.keySet()) {
                        warnings.add(
                            new ReportData(key, "validation", warn.get(key)));
                    }
                }
            }
            catch (ValidationException e) {
                Map<String, Integer> err = e.getErrors();
                for(String key: err.keySet()) {
                    errors.add(
                        new ReportData(key, "validation", err.get(key)));
                }
                Map<String, Integer> warn = e.getWarnings();
                if (warn != null) {
                    for (String key: warn.keySet()) {
                        warnings.add(
                            new ReportData(key, "validation", warn.get(key)));
                    }
                }
                return false;
            }
        }
        return true;
    }

    /**
     * Write new {@link LKommentarP} objects to the database.
     *
     * @param auth          The authentication information.
     * @param kommentare    List of {@link LKommentarP} objects.
     * @return success
     */
    @Override
    public boolean writeProbenKommentare(
        AuthenticationResponse auth,
        List<LKommentarP> kommentare
    ) {
        for(LKommentarP kommentar: kommentare) {
            pKommentarRepository.create(kommentar);
        }
        return true;
    }

    /**
     * Write new {@link LKommentarM} objects to the database.
     *
     * @param auth          The authentication information.
     * @param kommentare    List of {@link LKommentarM} objects.
     * @return success
     */
    @Override
    public boolean writeMessungKommentare(
        AuthenticationResponse auth,
        List<LKommentarM> kommentare
    ) {
        for(LKommentarM kommentar: kommentare) {
            Query q =
            em.createNativeQuery(
                "select nextval('kommentar_m_id_seq')");
            BigInteger seqId = (BigInteger)q.getSingleResult();
            kommentar.getId().setKId(seqId.intValue());
            mKommentarRepository.create(kommentar);
        }
        return true;
    }

    /**
     * Write new {@link LMesswert} objects to the database using validation.
     *
     * @param auth      The authentication information.
     * @param werte     List of {@link LMesswert} objects.
     * @return success
     */
    @Override
    public boolean writeMesswerte(
        AuthenticationResponse auth,
        List<LMesswert> werte
    ) {
        for(LMesswert messwert: werte) {
            try {
                Map<String, Integer> warn =
                    messwertValidator.validate(messwert, false);
                messwertRepository.create(messwert);
                if (warn != null) {
                    for (String key : warn.keySet()) {
                        warnings.add(
                            new ReportData(key, "validation", warn.get(key)));
                    }
                }
            }
            catch (ValidationException e) {
                Map<String, Integer> err = e.getErrors();
                for(String key: err.keySet()) {
                    errors.add(
                        new ReportData(key, "validation", err.get(key)));
                }
                Map<String, Integer> warn = e.getWarnings();
                if (warn != null) {
                    for (String key: warn.keySet()) {
                        warnings.add(
                            new ReportData(key, "validation", warn.get(key)));
                    }
                }
                return false;
            }
        }
        return false;
    }

    /**
     * Persist a {@link LProbe} object.
     *
     * @param probe The {@link LProbe} object.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private void persist(LProbe probe) {
        String queryColumns = "insert into l_probe (probe_id, ba_id, test," +
            " datenbasis_id, netzbetreiber_id, mst_id, probenart_id, umw_id";
        String queryParameter = " values (:probe_id, :ba_id, :test," +
            " :datenbasis_id, :netzbetreiber_id, :mst_id, :probenart_id," +
            " :umw_id";
        if (probe.getErzeugerId() != null) {
            queryColumns += ", erzeuger_id";
            queryParameter += ", :erzeuger_id";
        }
        if (probe.getHauptprobenNr() != null) {
            queryColumns += ", hauptproben_nr";
            queryParameter += ", :hauptproben_nr";
        }
        if (probe.getLetzteAenderung() != null) {
            queryColumns += ", letzte_aenderung";
            queryParameter += ", :letzte_aenderung";
        }
        if (probe.getMedia() != null) {
            queryColumns += ", media";
            queryParameter += ", :media";
        }
        if (probe.getMediaDesk() != null) {
            queryColumns += ", media_desk";
            queryParameter += ", :media_desk";
        }
        if (probe.getMittelungsdauer() != null) {
            queryColumns += ", mittelungsdauer";
            queryParameter += ", :mittelungsdauer";
        }
        if (probe.getMpKat() != null) {
            queryColumns += ", mp_kat";
            queryParameter += ", mp_kat";
        }
        if (probe.getMplId() != null) {
            queryColumns += ", mpl_id";
            queryParameter += ", :mpl_id";
        }
        if (probe.getMprId() != null) {
            queryColumns += ", mpr_id";
            queryParameter += ", :mpr_id";
        }
        if (probe.getProbeNehmerId() != null) {
            queryColumns += ", probe_nehmer_id";
            queryParameter += ", :probe_nehmer_id";
        }
        if (probe.getProbeentnahmeBeginn() != null) {
            queryColumns += ", probeentnahme_beginn";
            queryParameter += ", :probeentnahme_beginn";
        }
        if (probe.getProbeentnahmeEnde() != null) {
            queryColumns += ", probeentnahme_ende";
            queryParameter += ", :probeentnahme_ende";
        }
        if (probe.getSolldatumBeginn() != null) {
            queryColumns += ", solldatum_beginn";
            queryParameter += ", :solldatum_beginn";
        }
        if (probe.getSolldatumEnde() != null) {
            queryColumns += ", solldatum_ende";
            queryParameter += ", :solldatum_ende";
        }
        queryColumns += ") " + queryParameter + ")";

        Query insert = em.createNativeQuery(queryColumns);
        insert.setParameter("probe_id", probe.getProbeId());
        insert.setParameter("ba_id", probe.getBaId());
        insert.setParameter("datenbasis_id", probe.getDatenbasisId());
        insert.setParameter("mst_id", probe.getMstId());
        insert.setParameter("netzbetreiber_id", probe.getNetzbetreiberId());
        insert.setParameter("probenart_id", probe.getProbenartId());
        insert.setParameter("test", probe.isTest());
        insert.setParameter("umw_id", probe.getUmwId());
        if (probe.getErzeugerId() != null) {
            insert.setParameter("erzeuger_id", probe.getErzeugerId());
        }
        if (probe.getHauptprobenNr() != null) {
            insert.setParameter("hauptproben_nr", probe.getHauptprobenNr());
        }
        if (probe.getLetzteAenderung() != null) {
            insert.setParameter("letzte_aenderung", probe.getLetzteAenderung());
        }
        if (probe.getMedia() != null) {
            insert.setParameter("media", probe.getMedia());
        }
        if (probe.getMediaDesk() != null) {
            insert.setParameter("media_desk", probe.getMediaDesk());
        }
        if (probe.getMittelungsdauer() != null) {
            insert.setParameter("mittelungsdauer", probe.getMittelungsdauer());
        }
        if (probe.getMpKat() != null) {
            insert.setParameter("mp_kat", probe.getMpKat());
        }
        if (probe.getMplId() != null) {
            insert.setParameter("mpl_id", probe.getMplId());
        }
        if (probe.getMprId() != null) {
            insert.setParameter("mpr_id", probe.getMprId());
        }
        if (probe.getProbeNehmerId() != null) {
            insert.setParameter("probe_nehmer_id", probe.getProbeNehmerId());
        }
        if (probe.getProbeentnahmeBeginn() != null) {
            insert.setParameter("probeentnahme_beginn", probe.getProbeentnahmeBeginn());
        }
        if (probe.getProbeentnahmeEnde() != null) {
            insert.setParameter("probeentnahme_ende", probe.getProbeentnahmeEnde());
        }
        if (probe.getSolldatumBeginn() != null) {
            insert.setParameter("solldatum_beginn", probe.getSolldatumBeginn());
        }
        if (probe.getSolldatumEnde() != null) {
            insert.setParameter("solldatum_ende", probe.getSolldatumEnde());
        }
        insert.executeUpdate();
    }

    /**
     * Check if the user given in the authentication information is authorized
     * to access the {@link LProbe} object.
     *
     * @param probe The {@link LProbe} object.
     * @param auth  The authentication information.
     * @return access
     */
    private boolean authorized(LProbe probe, AuthenticationResponse auth) {
        if (auth.getNetzbetreiber().contains(probe.getNetzbetreiberId()) &&
            auth.getMst().contains(probe.getMstId())) {
            return true;
        }
        return false;
    }

    /**
     * @return the errors
     */
    public List<ReportData> getErrors() {
        return errors;
    }

    /**
     * @return the warnings
     */
    public List<ReportData> getWarnings() {
        return warnings;
    }

    /**
     * Reset the errors and warnings.
     */
    public void reset() {
        this.warnings = new ArrayList<ReportData>();
        this.errors = new ArrayList<ReportData>();
    }
}
