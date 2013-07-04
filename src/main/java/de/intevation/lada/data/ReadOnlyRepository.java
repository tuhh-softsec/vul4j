package de.intevation.lada.data;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.intevation.lada.rest.Response;

/**
 * Repository that allows read only access to model objects.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@ApplicationScoped
@Named("readonlyrepository")
public class ReadOnlyRepository implements Repository
{
    /**
     * The entitymanager managing the data.
     */
    @Inject
    private EntityManager em;

    public EntityManager getEntityManager() {
        return this.em;
    }

    /**
     * Filter object list by the given criteria.
     *
     * @param criteria  The filter query.
     * @return Response object.
     */
    public <T> Response filter(CriteriaQuery<T> filter) {
        List<T> result = em.createQuery(filter).getResultList();
        return new Response(true, 200, result);
    }

    /**
     * Get all objects of type <link>clazz</link>from database.
     *
     * @param clazz     The object type.
     * @return Response object.
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
     * @param clazz     The object type.
     * @param id        The object id.
     * @return Response object.
     */
    public <T> Response findById(Class<T> clazz, String id) {
        T item = em.find(clazz, id);
        if (item == null) {
            return new Response(false, 600, null);
        }
        return new Response(true, 200, item);
    }

    /**
     * This class does not support this operation.
     *
     * @param object
     */
    public Response create(Object object) {
        return null;
    }

    /**
     * This class does not support this operation.
     *
     * @param object
     */
    public Response update(Object object) {
        return null;
    }

    /**
     * This class does not support this operation.
     *
     * @param object
     */
    public Response delete(Object object) {
        return null;
    }
}