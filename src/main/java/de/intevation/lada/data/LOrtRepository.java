package de.intevation.lada.data;

import java.util.ArrayList;
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
import javax.persistence.criteria.Root;

import de.intevation.lada.manage.LOrtManager;
import de.intevation.lada.model.LOrt;
import de.intevation.lada.rest.Response;
import de.intevation.lada.validation.ValidationException;
import de.intevation.lada.validation.Validator;

@Named("lortrepository")
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
    public Response filter(Map<String, String> filter) {
        if (filter.isEmpty()) {
            return findAll(LOrt.class);
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LOrt> criteria = cb.createQuery(LOrt.class);
        Root<LOrt> member = criteria.from(LOrt.class);
        if (filter.containsKey("probe")) {
            criteria.where(
                cb.equal(member.get("probeId"), filter.get("probe")));
        }
        else {
            return new Response(false, 600, new ArrayList<LOrt>());
        }
        List<LOrt> result = filter(criteria);

        return new Response(true, 200, result);
    }

    /**
     * Validate and persist a new LProbe object.
     *
     * @param probe The new LProbe object
     * @return Response.
     */
    public Response create(Object object) {
        if (!(object instanceof LOrt)) {
            return new Response(false, 600, object);
        }
        LOrt ort = (LOrt)object;
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

    public Response update(Object object) {
        if (!(object instanceof LOrt)) {
            return new Response(false, 600, object);
        }
        LOrt ort = (LOrt)object;
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
