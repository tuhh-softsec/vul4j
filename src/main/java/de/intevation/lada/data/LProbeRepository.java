package de.intevation.lada.data;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.intevation.lada.manage.LProbeManager;
import de.intevation.lada.model.LProbe;

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
}
