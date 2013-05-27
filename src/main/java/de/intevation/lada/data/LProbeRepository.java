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
import de.intevation.lada.model.LKommentarP;
import de.intevation.lada.model.LOrt;
import de.intevation.lada.model.LProbe;
import de.intevation.lada.model.LProbeDetails;

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

    public LProbeDetails details(String probeId) {
        LProbeDetails details = new LProbeDetails();
        LProbe probe = em.find(LProbe.class, probeId);
        if (probe == null) {
            return new LProbeDetails();
        }
        details.setLprobe(probe);

        CriteriaBuilder cbLorts = em.getCriteriaBuilder();
        CriteriaQuery<LOrt> criteriaLorts = cbLorts.createQuery(LOrt.class);
        Root<LOrt> lo = criteriaLorts.from(LOrt.class);
        criteriaLorts.where(cbLorts.equal(lo.get("probeId"), probe.getProbeId()));
        List<LOrt> lorts = em.createQuery(criteriaLorts).getResultList();
        details.setLorts(lorts);

        CriteriaBuilder cbLKomm = em.getCriteriaBuilder();
        CriteriaQuery<LKommentarP> criteriaLKomm =
            cbLKomm.createQuery(LKommentarP.class);
        Root<LKommentarP> lk = criteriaLKomm.from(LKommentarP.class);
        criteriaLorts.where(cbLorts.equal(lk.get("probeId"), probe.getProbeId()));
        List<LKommentarP> lkomm = em.createQuery(criteriaLKomm).getResultList();
        details.setLkommentar(lkomm);

        return details;
    }
}
