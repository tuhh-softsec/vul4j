package de.intevation.lada.data;

import java.util.List;
import java.util.Map;

import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.intevation.lada.manage.LOrtManager;
import de.intevation.lada.model.LOrt;
import de.intevation.lada.rest.Response;
import de.intevation.lada.validation.ValidationException;
import de.intevation.lada.validation.Validator;


public class LOrtRepository
extends Repository
{
    /**
     * The entitymanager managing the data.
     */
    @Inject
    private EntityManager em;

    @Inject
    @Named("lortvalidator")
    private Validator validator;

    @Inject
    private LOrtManager manager;
    /**
     * Filter for LOrt objects used for calls from a service.
     *
     * @param probeId The id of the LProbe object.
     *
     * @return Response object containing LOrt objects.
     */
    public Response filter(String probeId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LOrt> criteria = cb.createQuery(LOrt.class);
        Root<LOrt> member = criteria.from(LOrt.class);
        Predicate pid = cb.equal(member.get("probeId"), probeId);
        criteria.where(pid);
        List<LOrt> result = filter(criteria);

        return new Response(true, 200, result);
    }
    /**
     * Filter LProbe object list by the given criteria.
     *
     * @param criteria
     * @return List of LProbe objects.
     */
    public List<LOrt> filter(CriteriaQuery<LOrt> criteria) {
        return em.createQuery(criteria).getResultList();
    }

    /**
     * Validate and persist a new LProbe object.
     *
     * @param probe The new LProbe object
     * @return Response.
     */
    public Response create(LOrt ort) {
        Response response = new Response(true, 200, ort);
        // Try to save the new LOrt.
        try {
            Map<String, Integer> warnings = validator.validate(ort);
            manager.create(ort);
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

    public Response update(LOrt ort) {
        Response response = new Response(true, 200, ort);
        // Try to update a LOrt object.
        try {
            Map<String, Integer> warnings = validator.validate(ort);
            manager.update(ort);
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
}
