package de.intevation.lada.data;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.inject.Named;

import de.intevation.lada.rest.Response;

/**
 * This generic Container is an interface to request and select Data
 * obejcts from the connected database.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Named
@ApplicationScoped
public abstract class Repository
{
    /**
     * The entitymanager managing the data.
     */
    @Inject
    private EntityManager em;

    public abstract Response create(Object object);

    public abstract Response update(Object object);

    public abstract Response filter(Map<String, String> keys);

    /**
     * Get all objects of type <link>clazz</link>from database.
     *
     * @param clazz The class type.
     * @return List of objects.
     */
    public <T> Response findAll(Class<T> clazz) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(clazz);
        Root<T> member = criteria.from(clazz);
        criteria.select(member);
        List<T> result = em.createQuery(criteria).getResultList();
        return new Response(true, 200, result);
    }

    /**
     * Find a single object identified by its id.
     * 
     * @param clazz The class type.
     * @param id The object id.
     * @return The requested object of type clazz
     */
    public <T> Response findById(Class<T> clazz, String id) {
        T item = em.find(clazz, id);
        if (item == null) {
            return new Response(false, 600, null);
        }
        return new Response(true, 200, item);
    }

    /**
     * Filter object list by the given criteria.
     *
     * @param criteria
     * @return List of LProbe objects.
     */
    public <T> List<T> filter(CriteriaQuery<T> criteria) {
        return em.createQuery(criteria).getResultList();
    }

    public CriteriaBuilder getCriteriaBuilder() {
        return em.getCriteriaBuilder();
    }
}
