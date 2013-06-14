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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.intevation.lada.manage.LProbeManager;
import de.intevation.lada.model.LProbe;
import de.intevation.lada.model.LProbeInfo;
import de.intevation.lada.rest.Response;
import de.intevation.lada.validation.ValidationException;
import de.intevation.lada.validation.Validator;

/**
 * This Container is an interface to request, filter and select LProbe
 * obejcts from the connected database.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Named("lproberepository")
public class LProbeRepository extends Repository{

    /**
     * The entitymanager managing the data.
     */
    @Inject
    private EntityManager em;

    /**
     * Manager class for LPRobe. Used to manipulate data objects.
     */
    @Inject
    private LProbeManager manager;

    @Inject
    @Named("lprobevalidator")
    private Validator validator;

    @Override
    public <T> Response findAll(Class<T> clazz) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LProbeInfo> criteria = cb.createQuery(LProbeInfo.class);
        Root<LProbeInfo> member = criteria.from(LProbeInfo.class);
        criteria.distinct(true);
        List<LProbeInfo> result = filter(criteria);
        return new Response(true, 200, result);
    }

    @Override
    public <T> Response findById(Class<T> clazz, String id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LProbeInfo> criteria = cb.createQuery(LProbeInfo.class);
        Root<LProbeInfo> member = criteria.from(LProbeInfo.class);
        Predicate pid = cb.equal(member.get("probeId"), id);
        criteria.where(pid);
        criteria.distinct(true);
        List<LProbeInfo> result = filter(criteria);
        return new Response(true, 200, result);
    }

    /**
     * Filter for LProbe objects used for calls from a service.
     *
     * @param mstId mst_id
     * @param uwbId umw_id
     * @param begin probeentnahmebegin
     * @return
     */
    public Response filter(Map<String, String> filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LProbeInfo> criteria = cb.createQuery(LProbeInfo.class);
        Root<LProbeInfo> member = criteria.from(LProbeInfo.class);
        List<Predicate> andFilter = new ArrayList<Predicate>();
        if (filter.containsKey("mst")) {
            andFilter.add(cb.equal(member.get("mstId"), filter.get("mst")));
        }
        if (filter.containsKey("uwb")) {
            andFilter.add(cb.equal(member.get("umwId"), filter.get("uwb")));
        }
        if (filter.containsKey("begin")) {
            try {
                Long date = Long.getLong(filter.get("begin"));
                andFilter.add(
                    cb.equal(member.get("probeentnahmeBeginn"), date));
            }
            catch(NumberFormatException nfe) {
                //ignore filter parameter.
            }
        }
        criteria.distinct(true);
        Predicate af = cb.and(andFilter.toArray(new Predicate[andFilter.size()]));
        criteria.where(af);
        List<LProbeInfo> result = filter(criteria);
        return new Response(true, 200, result);
    }

    /**
     * Validate and persist a new LProbe object.
     *
     * @param probe The new LProbe object
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
            Map<String, Integer> warnings = validator.validate(probe);
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

    public Response update(Object object) {
        if (!(object instanceof LProbe)) {
            return new Response(false, 602, object);
        }
        LProbe probe = (LProbe)object;
        Response response = new Response(true, 200, probe);
        // Try to save the new LProbe.
        try {
            Map<String, Integer> warnings = validator.validate(probe);
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
}
