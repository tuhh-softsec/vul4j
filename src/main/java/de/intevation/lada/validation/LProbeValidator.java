package de.intevation.lada.validation;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class LProbeValidator
implements Validator
{

    /**
     * Warnings found while validating the LProbe
     */
    private Map<String, Integer> warnings;

    @Inject
    private EntityManager em;

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
    public void validate(Object probe)
    throws ValidationException {
        warnings = new HashMap<String, Integer>();
        if (!(probe instanceof LProbe)) {
            Map<String, Integer> errors = new HashMap<String, Integer>();
            errors.put("lprobe", 610);
            throw new ValidationException(errors);
        }
        LProbe p = (LProbe)probe;

        validateHauptProbenNummer(p);
        validateEntnahmeOrt(p);
        validateProbenahmeBegin(p);
        validateUWB(p);
    }

    private void validateHauptProbenNummer(LProbe p)
    throws ValidationException {
        String hpn = p.getHauptprobenNr();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LProbe> criteria = cb.createQuery(LProbe.class);
        Root<LProbe> member = criteria.from(LProbe.class);
        Predicate mstId = cb.equal(member.get("mstId"), p.getMstId());
        Predicate hpNr = cb.equal(member.get("hauptprobenNr"), hpn);
        criteria.where(cb.and(mstId, hpNr));

        List<LProbe> proben = probeRepository.filter(criteria);
        if (!proben.isEmpty()) {
            Map<String, Integer> errors = new HashMap<String, Integer>();
            errors.put("hauptprobenNr", 611);
            throw new ValidationException(errors);
        }
    }

    private void validateEntnahmeOrt(LProbe probe) {
        String pid = probe.getProbeId();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LOrt> criteria = cb.createQuery(LOrt.class);
        Root<LOrt> member = criteria.from(LOrt.class);
        Predicate probeId = cb.equal(member.get("probeId"), pid);
        criteria.where(probeId);

        List<LOrt> orte = ortRepository.filter(criteria);
        if(orte.isEmpty()) {
            this.warnings.put("entnahmeOrt", 631);
        }
    }

    private void validateProbenahmeBegin(LProbe probe) {
        Date begin = probe.getProbeentnahmeBeginn();
        if (begin == null) {
            this.warnings.put("probeentnahmeBegin", 631);
        }
        else if (begin.after(new Date())){
            this.warnings.put("probeentnahmeBegin", 661);
        }
    }

    private void validateUWB(LProbe probe) {
        String uwb = probe.getUmwId();
        if (uwb == null || uwb.equals("")) {
            this.warnings.put("uwb", 631);
        }
    }

    /**
     * Getter for warnings occurred while validating the LProbe.
     *
     * @return Map of field - warning code pairs.
     */
    @Override
    public Map<String, Integer> getWarnings() {
        return warnings;
    }
}
