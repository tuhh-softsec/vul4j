package de.intevation.lada.data;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.intevation.lada.model.SMessStelle;

/**
 * This Container is an interface to request, filter and select SMessStelle
 * obejcts from the connected database.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@ApplicationScoped
public class SMessstelleRepository
{

    /**
     * The entitymanager managing the data.
     */
    @Inject
    EntityManager em;

    /**
     * Get all SMessStelle object from database.
     *
     * @return List of SMessStelle objects.
     */
    public List<SMessStelle> findAll() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<SMessStelle> criteria = builder.createQuery(SMessStelle.class);
        Root<SMessStelle> member = criteria.from(SMessStelle.class);
        criteria.select(member);
        return em.createQuery(criteria).getResultList();
    }

    /**
     * Find a single SMessStelle object identified by its id.
     *
     * @param id The mst_id
     * @return The SMessStelle object.
     */
    public SMessStelle findById(String id) {
        return em.find(SMessStelle.class, id);
    }
}
