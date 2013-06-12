package de.intevation.lada.validation;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.intevation.lada.data.LOrtRepository;
import de.intevation.lada.data.LProbeRepository;
import de.intevation.lada.model.LOrt;
import de.intevation.lada.model.LProbe;

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
    public Map<String, Integer> validate(Object probe)
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
        CriteriaBuilder cb = probeRepository.getCriteriaBuilder();
        CriteriaQuery<LProbe> criteria = cb.createQuery(LProbe.class);
        Root<LProbe> member = criteria.from(LProbe.class);
        Predicate mstId = cb.equal(member.get("mstId"), p.getMstId());
        Predicate hpNr = cb.equal(member.get("hauptprobenNr"), hpn);
        criteria.where(cb.and(mstId, hpNr));

        List<LProbe> proben = probeRepository.filter(criteria);
        if (!proben.isEmpty()) {
            Map<String, Integer> errors = new HashMap<String, Integer>();
            errors.put("hauptprobenNr", 611);
            throw new ValidationException(errors, warnings);
        }
    }

    private void validateEntnahmeOrt(LProbe probe, Map<String, Integer> warnings) {
        String pid = probe.getProbeId();

        CriteriaBuilder cb = ortRepository.getCriteriaBuilder();
        CriteriaQuery<LOrt> criteria = cb.createQuery(LOrt.class);
        Root<LOrt> member = criteria.from(LOrt.class);
        Predicate probeId = cb.equal(member.get("probeId"), pid);
        criteria.where(probeId);

        List<LOrt> orte = ortRepository.filter(criteria);
        if(orte.isEmpty()) {
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
