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

import de.intevation.lada.manage.Manager;
import de.intevation.lada.model.LMessung;
import de.intevation.lada.rest.Response;
import de.intevation.lada.validation.ValidationException;
import de.intevation.lada.validation.Validator;

/**
 * This Container is an interface to request, filter and select LMessung
 * obejcts from the connected database.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Named("lmessungrepository")
public class LMessungRepository
extends Repository
{
    /**
     * The entitymanager managing the data.
     */
    @Inject
    private EntityManager em;

    /**
     * Manager class for LPRobe. Used to manipulate data objects.
     */
    @Inject
    @Named("datamanager")
    private Manager manager;

    @Inject
    @Named("lmessungvalidator")
    private Validator validator;

    /**
     * Filter for LProbe objects used for calls from a service.
     *
     * @param mstId mst_id
     * @param uwbId umw_id
     * @param begin probeentnahmebegin
     * @return
     */
    public Response filter(Map<String, String> filter) {
        if (filter.isEmpty()) {
            return findAll(LMessung.class);
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LMessung> criteria = cb.createQuery(LMessung.class);
        Root<LMessung> member = criteria.from(LMessung.class);
        if (filter.containsKey("probe")) {
            criteria.where(
                cb.equal(member.get("LProbeId"), filter.get("probe")));
        }
        else {
            return new Response(false, 600, new ArrayList<LMessung>());
        }
        List<LMessung> result = filter(criteria);
        return new Response(true, 200, result);
    }

    /**
     * Validate and persist a new LProbe object.
     *
     * @param probe The new LProbe object
     * @return Response.
     */
    public Response create(Object object) {
        if (!(object instanceof LMessung)) {
            return new Response(false, 602, object);
        }
        LMessung messung = (LMessung)object;
        Response response = new Response(true, 200, messung);
        // Try to save the new LMessung.
        try {
            Map<String, Integer> warnings = validator.validate(messung);
            manager.create(messung);
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
        if (!(object instanceof LMessung)) {
            return new Response(false, 602, object);
        }
        LMessung messung = (LMessung)object;
        Response response = new Response(true, 200, messung);
        // Try to save the new LProbe.
        try {
            Map<String, Integer> warnings = validator.validate(messung);
            manager.update(messung);
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
