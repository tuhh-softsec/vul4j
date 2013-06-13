package de.intevation.lada.data;

import java.util.List;
import java.util.Map;

import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.intevation.lada.manage.LMessungManager;
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
@ApplicationScoped
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
    private LMessungManager manager;

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
    public Response filter(String probeId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LMessung> criteria = cb.createQuery(LMessung.class);
        Root<LMessung> member = criteria.from(LMessung.class);
        Predicate probe = cb.equal(member.get("LProbeId"), probeId);
        criteria.where(probe);
        List<LMessung> result = filter(criteria);
        return new Response(true, 200, result);
    }

    /**
     * Filter LProbe object list by the given criteria.
     *
     * @param criteria
     * @return List of LProbe objects.
     */
    public List<LMessung> filter(CriteriaQuery<LMessung> criteria) {
        return em.createQuery(criteria).getResultList();
    }

    /**
     * Validate and persist a new LProbe object.
     *
     * @param probe The new LProbe object
     * @return Response.
     */
    public Response create(LMessung messung) {
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

    public Response update(LMessung messung) {
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
