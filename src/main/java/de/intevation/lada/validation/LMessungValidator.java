package de.intevation.lada.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.intevation.lada.data.QueryBuilder;
import de.intevation.lada.data.Repository;
import de.intevation.lada.model.LMessung;
import de.intevation.lada.model.LMesswert;
import de.intevation.lada.model.LProbe;
import de.intevation.lada.model.LProbeInfo;
import de.intevation.lada.model.SPflichtMessgroesse;
import de.intevation.lada.rest.Response;

/**
 * Validator for LMessung objects.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Named("lmessungvalidator")
@ApplicationScoped
public class LMessungValidator
implements Validator
{
    @Inject
    @Named("lproberepository")
    private Repository probeRepository;

    @Inject
    @Named("lmessungrepository")
    private Repository messungRepository;

    @Inject
    @Named("lmesswertrepository")
    private Repository messwertRepository;

    @Inject
    @Named("readonlyrepository")
    private Repository readonlyRepository;

    /**
     * Validate a LMessung object.
     *
     * @param object    The object to validate.
     * @param update    The database operation.
     *                  TRUE indicates that the object should be updated, FALSE
     *                  if the object is a new Object.
     * @return Map containing warnings.
     * @throws ValidationException
     */
    @Override
    public Map<String, Integer> validate(Object object, boolean update)
    throws ValidationException {
        Map<String, Integer> warnings = new HashMap<String, Integer>();
        if (!(object instanceof LMessung)) {
            Map<String, Integer> errors = new HashMap<String, Integer>();
            errors.put("lmessung", 610);
            throw new ValidationException(errors);
        }
        LMessung messung = (LMessung)object;

        validateHasNebenprobenNr(messung, warnings);
        validateDatum(messung, warnings);
        validateHasMesswert(messung, warnings);
        validateMessgroesse(messung, warnings);
        validatePflichtmessgroessen(messung, warnings);
        if (!update) {
            validateUniqueNebenprobenNr(messung, warnings);
        }
        return warnings;
    }

    private void validateHasMesswert(
        LMessung messung,
        Map<String, Integer> warnings) {
        QueryBuilder<LMesswert> wertBuilder =
            new QueryBuilder<LMesswert>(
                messwertRepository.getEntityManager(), LMesswert.class);
        wertBuilder.and("messungsId", messung.getMessungsId())
            .and("probeId", messung.getProbeId());
        Response wertResponse = messwertRepository.filter(wertBuilder.getQuery());
        List<LMesswert> messwerte = (List<LMesswert>)wertResponse.getData();
        if (messwerte == null || messwerte.isEmpty()) {
            warnings.put("messwert", 631);
        }
    }

    private void validatePflichtmessgroessen(
        LMessung messung,
        Map<String, Integer> warnings) {
        QueryBuilder<SPflichtMessgroesse> builder =
            new QueryBuilder<SPflichtMessgroesse>(
                readonlyRepository.getEntityManager(),
                SPflichtMessgroesse.class);
        builder.and("mmtId", messung.getMmtId());
        Response response = readonlyRepository.filter(builder.getQuery());
        List<SPflichtMessgroesse> pflicht =
            (List<SPflichtMessgroesse>)response.getData();

        QueryBuilder<LMesswert> wertBuilder =
            new QueryBuilder<LMesswert>(
                messwertRepository.getEntityManager(), LMesswert.class);
        wertBuilder.and("messungsId", messung.getMessungsId())
            .and("probeId", messung.getProbeId());
        Response wertResponse =
            messwertRepository.filter(wertBuilder.getQuery());
        List<LMesswert> messwerte = (List<LMesswert>)wertResponse.getData();
        for (SPflichtMessgroesse p : pflicht) {
            boolean hit = false;
            for (LMesswert wert : messwerte) {
                if (p.getMessgroesseId().equals(wert.getMessgroesseId())) {
                    hit = true;
                }
            }
            if (!hit) {
                warnings.put("pflichtmessgroesse", 631);
            }
        }
    }

    private void validateMessgroesse(
        LMessung messung,
        Map<String, Integer> warnings
    ) {
        String mmt = messung.getMmtId();
        QueryBuilder<LMesswert> builder =
            new QueryBuilder<LMesswert>(
                messwertRepository.getEntityManager(), LMesswert.class);
        builder.and("messungsId", messung.getMessungsId())
            .and("probeId", messung.getProbeId());
        Response response = messwertRepository.filter(builder.getQuery());
        List<LMesswert> messwerte = (List<LMesswert>)response.getData();
        String query = "select messgroesse_id from S_mmt_messgroesse where mmt_id = " + mmt;
        List<Object[]> results = readonlyRepository.getEntityManager().createNativeQuery(query).getResultList();
        for(LMesswert messwert: messwerte) {
            boolean hit = false;
            for (Object[] row: results) {
                if (messwert.getMessgroesseId().equals(row[0].toString())) {
                    hit = true;
                }
            }
            if (!hit) {
                warnings.put("messgroesse", 632);
            }
        }
    }

    /**
     * Check if the object has a 'Nebenproben Nr.'.
     *
     * @param messung   The LMessung object.
     * @param warnings  The map containing warnings.
     * @throws ValidationException
     */
    private void validateHasNebenprobenNr(
        LMessung messung,
        Map<String, Integer> warnings)
    throws ValidationException {
        if (messung.getNebenprobenNr() == null ||
            messung.getNebenprobenNr().equals("")) {
            warnings.put("nebenprobenNr", 631);
        }
    }

    /**
     * Check if the 'Nebenproben Nr' is unique.
     *
     * @param messung   The LMessung object.
     * @param warnings  The map containing warnings.
     * @throws ValidationException
     */
    private void validateUniqueNebenprobenNr(
        LMessung messung,
        Map<String, Integer> warnings)
    throws ValidationException {
        QueryBuilder<LMessung> builder =
            new QueryBuilder<LMessung>(
                messungRepository.getEntityManager(),
                LMessung.class);
        builder.and("probeId", messung.getProbeId());
        Response response = messungRepository.filter(builder.getQuery());
        List<LMessung> list = (List<LMessung>) response.getData();
        if (list.isEmpty()) {
            return;
        }
        for (LMessung m: list) {
            if (m.getNebenprobenNr().equals(messung.getNebenprobenNr())) {
                Map<String, Integer> errors = new HashMap<String, Integer>();
                errors.put("nebenprobenNr", 611);
                throw new ValidationException(errors);
            }
        }
    }

    /**
     * Check if the 'Messdatum' is after the 'Probennahmedatum'.
     *
     * @param messung   The LMessung object.
     * @param warnings  The map containing warinings. 
     * @throws ValidationException
     */
    private void validateDatum(
        LMessung messung,
        Map<String, Integer> warnings)
    throws ValidationException{
        String probeId = messung.getProbeId();
        Response response = probeRepository.findById(LProbe.class, probeId);
        @SuppressWarnings("unchecked")
        List<LProbeInfo> list = (List<LProbeInfo>) response.getData();
        if (list.isEmpty()) {
            Map<String, Integer> errors = new HashMap<String, Integer>();
            errors.put("lprobe", 604);
            throw new ValidationException(errors);
        }
        LProbeInfo probe = list.get(0);
        if (probe.getProbeentnahmeEnde() == null ||
            probe.getProbeentnahmeEnde().after(messung.getMesszeitpunkt())) {
            warnings.put("messzeitpunkt", 661);
        }
    }
}
