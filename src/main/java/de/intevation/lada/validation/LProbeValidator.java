package de.intevation.lada.validation;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.intevation.lada.data.LOrtRepository;
import de.intevation.lada.data.LProbeRepository;
import de.intevation.lada.data.QueryBuilder;
import de.intevation.lada.model.LOrt;
import de.intevation.lada.model.LProbe;
import de.intevation.lada.rest.Response;

/**
 * Validator for LProbe objects.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Named("lprobevalidator")
@ApplicationScoped
public class LProbeValidator
implements Validator
{
    /**
     * The repositories used in this validator.
     */
    @Inject
    private LProbeRepository probeRepository;
    @Inject
    private LOrtRepository ortRepository;

    /**
     * Validate a LProbe object.
     *
     * @param probe The LProbe object.
     */
    @Override
    public Map<String, Integer> validate(Object probe, boolean update)
    throws ValidationException {
        Map<String, Integer>warnings = new HashMap<String, Integer>();
        if (!(probe instanceof LProbe)) {
            Map<String, Integer> errors = new HashMap<String, Integer>();
            errors.put("lprobe", 610);
            throw new ValidationException(errors);
        }
        LProbe p = (LProbe)probe;

        validateEntnahmeOrt(p, warnings);
        validateProbenahmeBegin(p, warnings);
        validateUWB(p, warnings);
        validateHauptProbenNummer(p, warnings);
        return warnings;
    }

    private void validateHauptProbenNummer(LProbe p, Map<String, Integer> warnings)
    throws ValidationException {
        String hpn = p.getHauptprobenNr();
        QueryBuilder<LProbe> builder =
            new QueryBuilder<LProbe>(
                probeRepository.getEntityManager(), LProbe.class);
        builder.and("mstId", p.getMstId()).and("hauptprobenNr", hpn);

        Response response = probeRepository.filter(builder.getQuery());
        if (!((List<LProbe>)response.getData()).isEmpty()) {
            Map<String, Integer> errors = new HashMap<String, Integer>();
            errors.put("hauptprobenNr", 611);
            throw new ValidationException(errors, warnings);
        }
    }

    private void validateEntnahmeOrt(LProbe probe, Map<String, Integer> warnings) {
        String pid = probe.getProbeId();

        QueryBuilder<LOrt> builder =
            new QueryBuilder<LOrt>(
                ortRepository.getEntityManager(), LOrt.class);
        builder.and("probeId", pid);

        Response response = ortRepository.filter(builder.getQuery());
        if (((List<LOrt>)response.getData()).isEmpty()) {
            warnings.put("entnahmeOrt", 631);
        }
    }

    private void validateProbenahmeBegin(LProbe probe, Map<String, Integer> warnings) {
        Date begin = probe.getProbeentnahmeBeginn();
        if (begin == null) {
            warnings.put("probeentnahmeBegin", 631);
        }
        else if (begin.after(new Date())){
            warnings.put("probeentnahmeBegin", 661);
        }
    }

    private void validateUWB(LProbe probe, Map<String, Integer> warnings) {
        String uwb = probe.getUmwId();
        if (uwb == null || uwb.equals("")) {
            warnings.put("uwb", 631);
        }
    }
}
