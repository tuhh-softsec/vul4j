package de.intevation.lada.data;

import java.util.List;

import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.intevation.lada.manage.Manager;
import de.intevation.lada.model.Ort;
import de.intevation.lada.rest.Response;

/**
 * Repository that allows read, write and update operations on 'Ort' objects.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@ApplicationScoped
@Named("ortrepository")
public class OrtRepository implements Repository
{
    /**
     * The entitymanager managing the data.
     */
    @Inject
    private EntityManager em;

    /**
     * THe data manager providing database operations.
     */
    @Inject
    @Named("datamanager")
    private Manager manager;

    public EntityManager getEntityManager() {
        return this.em;
    }

    /**
     * Filter object list by the given criteria.
     *
     * @param criteria  The query filter.
     * @return Response object.
     */
    public <T> Response filter(CriteriaQuery<T> filter) {
        List<T> result = em.createQuery(filter).getResultList();
        return new Response(true, 200, result);
    }


    /**
     * Get all objects of type <link>clazz</link>from database.
     *
     * @param clazz The object type.
     * @return Response object
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
     * Create a new 'Ort' object.
     *
     * @param object    The new object.
     * @return Response object.
     */
    public Response create(Object object) {
        if (!(object instanceof Ort)) {
            return new Response(false, 602, object);
        }
        Ort ort = (Ort)object;
        try {
            manager.create(ort);
            return new Response(true, 200, ort);
        }
        catch(EntityExistsException eee) {
            return new Response(false, 601, ort);
        }
        catch(IllegalArgumentException iae) {
            return new Response(false, 602, ort);
        }
        catch(TransactionRequiredException tre) {
            return new Response(false, 603, ort);
        }
    }

    /**
     * Update a 'Ort' object.
     *
     * @param object    The object to update.
     * @return Response object.
     */
    public Response update(Object object) {
        if (!(object instanceof Ort)) {
            return new Response(false, 602, object);
        }
        Ort ort = (Ort)object;
        Response response = new Response(true, 200, ort);
        try {
            manager.update(ort);
            return response;
        }
        catch (EntityExistsException eee) {
            response.setSuccess(false);
            response.setMessage(601);
        }
        catch (IllegalArgumentException iae) {
            response.setSuccess(false);
            response.setMessage(602);
        }
        catch (TransactionRequiredException tre) {
            response.setSuccess(false);
            response.setMessage(603);
        }
        catch (EJBTransactionRolledbackException te) {
            response.setSuccess(false);
            response.setMessage(604);
        }
        return response;
    }

    /**
     * Delete a 'Ort' object.
     *
     * @param object    The object to delete.
     * @return Response object.
     */
    public Response delete(Object object) {
        if (!(object instanceof Ort)) {
            return new Response(false, 602, null);
        }
        Ort ort = (Ort)object;
        Response response = new Response(true, 200, null);
        try {
            manager.delete(ort);
        }
        catch (IllegalArgumentException iae) {
            response.setSuccess(false);
            response.setMessage(602);
        }
        catch (TransactionRequiredException tre) {
            response.setSuccess(false);
            response.setMessage(603);
        }
        return response;
    }
}