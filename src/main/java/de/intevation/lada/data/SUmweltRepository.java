package de.intevation.lada.data;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.intevation.lada.model.SUmwelt;

/**
 * This Container is an interface to request, filter and select LProbe
 * obejcts from the connected database.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@ApplicationScoped
public class SUmweltRepository {

    /**
     * The entitymanager managing the data.
     */
    @Inject
    EntityManager em;

    /**
     * Get all SUmwelt objects from database.
     *
     * @return List of LProbe objects.
     */
    public List<SUmwelt> findAll() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<SUmwelt> criteria = builder.createQuery(SUmwelt.class);
        Root<SUmwelt> member = criteria.from(SUmwelt.class);
        criteria.select(member);
        return em.createQuery(criteria).getResultList();
    }

    /**
     * Find a single SUmwelt object identified by its id.
     *
     * @param id The mst_id
     * @return The SMessStelle object.
     */
    public SUmwelt findById(String id) {
        return em.find(SUmwelt.class, id);
    }
}
