package de.intevation.lada.data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.intevation.lada.manage.LProbeManager;
import de.intevation.lada.model.LProbe;
import de.intevation.lada.validation.ValidationException;

/**
 * This Container is an interface to request, filter and select LProbe
 * obejcts from the connected database.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@ApplicationScoped
public class LProbeRepository extends Repository{

    /**
     * The entitymanager managing the data.
     */
    @Inject
    private EntityManager em;

    /**
     * Manager class for LPRobe. Used to manipulate data objects.
     */
    @Inject
    private LProbeManager manager;

    /**
     * Errors/Warnings occured in repository operations.
     */
    private int generalError;
    private Map<String, Integer> errors;
    private Map<String, Integer> warnings;

    /**
     * Filter for LProbe objects.
     *
     * @param mstId mst_id
     * @param uwbId umw_id
     * @param begin probeentnahmebegin
     * @return
     */
    public List<LProbe> filter(String mstId, String uwbId, Long begin) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LProbe> criteria = cb.createQuery(LProbe.class);
        Root<LProbe> member = criteria.from(LProbe.class);
        Predicate mst = cb.equal(member.get("mstId"), mstId);
        Predicate uwb = cb.equal(member.get("umwId"), uwbId);

        if (!mstId.isEmpty() && !uwbId.isEmpty() && begin != null) {
            Predicate beg = cb.equal(member.get("probeentnahmeBeginn"), new Date(begin));
            criteria.where(cb.and(mst, uwb, beg));
        }
        else if (!mstId.isEmpty() && !uwbId.isEmpty() && begin == null) {
            criteria.where(cb.and(mst, uwb));
        }
        else if (!mstId.isEmpty() && uwbId.isEmpty() && begin != null) {
            Predicate beg = cb.equal(member.get("probeentnahmeBeginn"), new Date(begin));
            criteria.where(cb.and(mst, beg));
        }
        else if (mstId.isEmpty() && !uwbId.isEmpty() && begin != null) {
            Predicate beg = cb.equal(member.get("probeentnahmeBeginn"), new Date(begin));
            criteria.where(cb.and(uwb, beg));
        }
        else if (!mstId.isEmpty() && uwbId.isEmpty() && begin == null) {
            criteria.where(mst);
        }
        else if (mstId.isEmpty() && !uwbId.isEmpty() && begin == null) {
            criteria.where(uwb);
        }
        else if (mstId.isEmpty() && uwbId.isEmpty() && begin != null) {
            Predicate beg = cb.equal(member.get("probeentnahmeBeginn"), new Date(begin));
            criteria.where(beg);
        }
        return em.createQuery(criteria).getResultList();
    }

    public LProbe details(String probeId) {
        return em.find(LProbe.class, probeId);
    }

    public boolean create(LProbe probe) {
        setGeneralError(200);
        setErrors(new HashMap<String, Integer>());
        setWarnings(new HashMap<String, Integer>());
        try {
            manager.create(probe);
            setWarnings(manager.getWarnings());
            return true;
        }
        catch (EntityExistsException eee) {
            setGeneralError(601);
        }
        catch (IllegalArgumentException iae) {
            setGeneralError(602);
        }
        catch (TransactionRequiredException tre) {
            setGeneralError(603);
        }
        catch (ValidationException ve) {
            setGeneralError(604);
            setErrors(ve.getErrors());
            setWarnings(manager.getWarnings());
        }
        return false;
    }

    public int getGeneralError() {
        return generalError;
    }

    private void setGeneralError(int generalError) {
        this.generalError = generalError;
    }

    public Map<String, Integer> getErrors() {
        return errors;
    }

    private void setErrors(Map<String, Integer> errors) {
        this.errors = errors;
    }

    public Map<String, Integer> getWarnings() {
        return warnings;
    }

    private void setWarnings(Map<String, Integer> warnings) {
        this.warnings = warnings;
    }
}
