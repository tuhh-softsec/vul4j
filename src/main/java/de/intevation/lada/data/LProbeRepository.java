package de.intevation.lada.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;
import javax.persistence.criteria.CriteriaQuery;
import javax.ws.rs.core.MultivaluedMap;

import de.intevation.lada.auth.Authorization;
import de.intevation.lada.manage.Manager;
import de.intevation.lada.model.LProbe;
import de.intevation.lada.model.LProbeInfo;
import de.intevation.lada.rest.Response;
import de.intevation.lada.validation.ValidationException;
import de.intevation.lada.validation.Validator;

/**
 * This Container is an interface to read, write and update LProbe objects.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@ApplicationScoped
@Named("lproberepository")
public class LProbeRepository implements Repository{
    /**
     * The entitymanager managing the data.
     */
    @Inject
    private EntityManager em;

    /**
     * The data manager providing database operations.
     */
    @Inject
    @Named("datamanager")
    private Manager manager;

    /**
     * The validator used for LProbe objects.
     */
    @Inject
    @Named("lprobevalidator")
    private Validator validator;

    @Inject
    @Named("dataauthorization")
    private Authorization authorization;

    public EntityManager getEntityManager() {
        return this.em;
    }

    /**
     * Filter object list by the given criteria.
     *
     * @param filter  The query filter
     * @return Response object.
     */
    public <T> Response filter(CriteriaQuery<T> filter) {
        List<T> result = em.createQuery(filter).getResultList();
        return new Response(true, 200, result);
    }

    public Response filterFree(
        String sql,
        List<String> filters,
        List<String> results,
        MultivaluedMap<String, String> params) {
        Query query = em.createNativeQuery(sql);
        for (String filter: filters) {
            List<String> param = params.get(filter);
            List<String> clean = new ArrayList<String>();
            for(String p : param) {
                clean.add(p.replace(",", "|"));
            }
            query.setParameter(filter, clean);
        }
        List<Object[]> result = query.getResultList();
        List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
        for (Object[] row: result) {
            Map<String, Object> set = new HashMap<String, Object>();
            for (int i = 0; i < row.length; i++) {
                set.put(results.get(i), row[i]);
                if (results.get(i).equals("probeId")) {
                    if (authorization.isReadOnly((String)row[i])) {
                        set.put("readonly", Boolean.TRUE);
                    }
                    else {
                        set.put("readonly", Boolean.FALSE);
                    }
                }
            }
            res.add(set);
        }
        return new Response(true, 200, res);
    }

    /**
     * Get all objects.
     *
     * @param clazz The object type. (unused)
     * @return Response object.
     */
    public <T> Response findAll(Class<T> clazz) {
        QueryBuilder<LProbeInfo> builder =
            new QueryBuilder<LProbeInfo>(this.getEntityManager(), LProbeInfo.class);
        builder.distinct();
        return filter(builder.getQuery());
    }

    /**
     * Find object identified by its id.
     *
     * @param clazz The object type.(unused)
     * @param id    The object id.
     * @return Response object.
     */
    public <T> Response findById(Class<T> clazz, String id) {
        QueryBuilder<LProbeInfo> builder =
            new QueryBuilder<LProbeInfo>(this.getEntityManager(), LProbeInfo.class);
        builder.and("probeId", id);
        builder.distinct();
        return filter(builder.getQuery());
    }

    /**
     * Validate and persist a new LProbe object.
     *
     * @param object    The new LProbe object
     * @return Response.
     */
    public Response create(Object object) {
        if (!(object instanceof LProbe)) {
            return new Response(false, 602, object);
        }
        LProbe probe = (LProbe)object;
        Response response = new Response(true, 200, probe);
        // Try to save the new LProbe.
        try {
            Map<String, Integer> warnings = validator.validate(probe, false);
            manager.create(probe);
            response.setWarnings(warnings);
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
        catch (ValidationException ve) {
            response.setSuccess(false);
            response.setMessage(604);
            response.setErrors(ve.getErrors());
            response.setWarnings(ve.getWarnings());
        }
        catch (EJBTransactionRolledbackException te) {
            response.setSuccess(false);
            response.setMessage(604);
        }
        return response;
    }

    /**
     * Validate and update a LProbe object.
     *
     * @param object    The object to update.
     * @return Response object.
     */
    public Response update(Object object) {
        if (!(object instanceof LProbe)) {
            return new Response(false, 602, object);
        }
        LProbe probe = (LProbe)object;
        Response response = new Response(true, 200, probe);
        try {
            Map<String, Integer> warnings = validator.validate(probe, true);
            manager.update(probe);
            response.setWarnings(warnings);
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
        catch (ValidationException ve) {
            response.setSuccess(false);
            response.setMessage(604);
            response.setErrors(ve.getErrors());
            response.setWarnings(ve.getWarnings());
        }
        catch (EJBTransactionRolledbackException te) {
            response.setSuccess(false);
            response.setMessage(604);
        }
        return response;
    }

    /**
     * This class does not support this operation.
     *
     * @param object
     */
    public Response delete(Object object) {
        return null;
    }

    @Override
    public <T> Response filter(CriteriaQuery<T> filter, int size, int start) {
        return null;
    }
}
