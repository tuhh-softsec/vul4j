package de.intevation.lada.data.importer;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
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
import de.intevation.lada.rest.Response;
import de.intevation.lada.validation.ValidationException;
import de.intevation.lada.validation.Validator;

@Named("lafimporter")
@Stateless
public class LAFImporter
implements Importer
{

    @Inject
    private EntityManager em;

    @Inject
    private LAFParser parser;

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

    private Map<String, Map<String, Integer>> warnings;
    private Map<String, Map<String, Integer>> errors;

    public LAFImporter() {
        warnings = new HashMap<String, Map<String, Integer>>();
        errors = new HashMap<String, Map<String, Integer>>();
    }

    /**
     * @return the warnings
     */
    public Map<String, Map<String, Integer>> getWarnings() {
        return warnings;
    }

    /**
     * @return the errors
     */
    public Map<String, Map<String, Integer>> getErrors() {
        return errors;
    }

    @Override
    public boolean importData(String content, AuthenticationResponse auth) {
        try {
            boolean success = parser.parse(content);
            if (success) {
                List<LProbe> proben = parser.getProben();
                List<LMessung> messungen = parser.getMessungen();
                List<LOrt> orte = parser.getOrte();
                List<LKommentarP> pKommentare = parser.getProbeKommentare();
                List<LKommentarM> mKommentare = parser.getMessungKommentare();
                List<LMesswert> messwerte = parser.getMesswerte();
                writeProben(auth, proben);
                writeMessungen(auth, messungen);
                //writeOrte(auth, orte);
                writePKommentare(auth, pKommentare);
                writeMKommentare(auth, mKommentare);
                writeMesswerte(auth, messwerte);
            }
            else {
                Map<String, Integer> err = new HashMap<String, Integer>();
                err.put("no success", 660);
                errors.put("parser", err);
                return false;
            }
        }
        catch (LAFParserException e) {
            Map<String, Integer> err = new HashMap<String, Integer>();
            err.put(e.getMessage(), 660);
            errors.put("parser", err);
            return false;
        }
        Map<String, Map<String, Map<String, Integer>>> data =
            new HashMap<String, Map<String,Map<String, Integer>>>();
        data.put("warnings", warnings);
        data.put("errors", errors);
        return true;
    }

    private void writeMessungen(
        AuthenticationResponse auth,
        List<LMessung> messungen
    ) {
        for(LMessung messung: messungen) {
            try {

                Map<String, Integer> warn =
                    messungValidator.validate(messung, false);
                messungRepository.create(messung);
                if (warn != null) {
                    warnings.put(
                        messung.getMessungsId().toString(),
                        warn);
                }
            }
            catch (ValidationException e) {
                errors.put(messung.getProbeId(), e.getErrors());
                warnings.put(
                    messung.getProbeId(),
                    e.getWarnings());
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private void writeMesswerte(
        AuthenticationResponse auth,
        List<LMesswert> messwerte
    ) {
        for(LMesswert messwert: messwerte) {
            try {
                Map<String, Integer> warn =
                    messwertValidator.validate(messwert, false);
                Response r = messwertRepository.create(messwert);
                if (warn != null) {
                    warnings.put(
                        messwert.getProbeId(),
                        warn);
                }
            }
            catch (ValidationException e) {
                errors.put(messwert.getProbeId(), e.getErrors());
                warnings.put(
                    messwert.getProbeId(),
                    e.getWarnings());
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private void writePKommentare(
        AuthenticationResponse auth,
        List<LKommentarP> kommentare
    ) {
        for(LKommentarP kommentar: kommentare) {
            try {
                pKommentarRepository.create(kommentar);
            }
            catch(Exception e) {
                Map<String, Integer> err = new HashMap<String, Integer>();
                err.put(
                    kommentar.getProbeId() + " - " +
                    kommentar.getkId(), 661);
                errors.put("lkommentarp", err);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private void writeMKommentare(
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
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private void writeOrte(
        AuthenticationResponse auth,
        List<LOrt> orte
    ) {
        for(LOrt ort: orte) {
            try {
                Map<String, Integer> warn =
                    ortValidator.validate(ort, false);
                ortRepository.create(ort);
                if (warn != null) {
                    warnings.put(String.valueOf(ort.getOrtId()), warn);
                }
            }
            catch (ValidationException e) {
                errors.put(String.valueOf(ort.getOrtId()), e.getErrors());
                warnings.put(
                    String.valueOf(ort.getOrtId()),
                    e.getWarnings());
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private void writeProben(AuthenticationResponse auth, List<LProbe> proben) {
        for (LProbe probe: proben) {
            if (!authorized(probe, auth)) {
                Map<String, Integer> err = new HashMap<String, Integer>();
                err.put("not authorized", 699);
                errors.put(probe.getProbeId(), err);
                continue;
            }
            try {
                Map<String, Integer> warn =
                    probeValidator.validate(probe, false);
                if (warn != null) {
                    warnings.put(probe.getProbeId(), warn);
                }
            }
            catch (ValidationException e) {
                errors.put(probe.getProbeId(), e.getErrors());
                warnings.put(probe.getProbeId(), e.getWarnings());
                continue;
            }
            persist(probe);
        }
    }

    private boolean authorized(LProbe probe, AuthenticationResponse auth) {
        if (auth.getNetzbetreiber().contains(probe.getNetzbetreiberId()) &&
            auth.getMst().contains(probe.getMstId())) {
            return true;
        }
        return false;
    }

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
        int res = insert.executeUpdate();
        int i = res;
    }
}
