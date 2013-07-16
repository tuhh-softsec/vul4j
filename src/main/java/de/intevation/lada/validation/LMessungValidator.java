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
import de.intevation.lada.model.LProbe;
import de.intevation.lada.model.LProbeInfo;
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
        if (!update) {
            validateUniqueNebenprobenNr(messung, warnings);
        }
        return warnings;
    }

    private void validateHasNebenprobenNr(
        LMessung messung,
        Map<String, Integer> warnings)
    throws ValidationException {
        if (messung.getNebenprobenNr() == null ||
            messung.getNebenprobenNr().equals("")) {
            warnings.put("nebenprobenNr", 631);
        }
    }

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
        if (probe.getProbeentnahmeEnde().after(messung.getMesszeitpunkt())) {
            warnings.put("messzeitpunkt", 661);
        }
    }
}
