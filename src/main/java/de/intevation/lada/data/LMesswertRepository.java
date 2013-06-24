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

import de.intevation.lada.manage.Manager;
import de.intevation.lada.model.LMesswert;
import de.intevation.lada.rest.Response;

/**
 * This Container is an interface to request, filter and select LMesswert
 * obejcts from the connected database.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Named("lmesswertrepository")
public class LMesswertRepository
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

    @Override
    public Response create(Object object) {
        if (!(object instanceof LMesswert)) {
            return new Response(false, 602, object);
        }
        LMesswert messwert = (LMesswert)object;
        Response response = new Response(true, 200, messwert);
        // Try to save the new LProbe.
        try {
            manager.create(messwert);
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

    @Override
    public Response update(Object object) {
        if (!(object instanceof LMesswert)) {
            return new Response(false, 602, object);
        }
        LMesswert messwert = (LMesswert)object;
        Response response = new Response(true, 200, messwert);
        // Try to save the new LProbe.
        try {
            manager.update(messwert);
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

    @Override
    public Response filter(Map<String, String> filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LMesswert> criteria = cb.createQuery(LMesswert.class);
        Root<LMesswert> member = criteria.from(LMesswert.class);
        List<Predicate> andFilter = new ArrayList<Predicate>();
        if (filter.containsKey("probe")) {
            andFilter.add(cb.equal(member.get("probeId"), filter.get("probe")));
        }
        if (filter.containsKey("messung")) {
            andFilter.add(cb.equal(member.get("messungsId"), filter.get("messung")));
        }
        criteria.distinct(true);
        Predicate ap = cb.and(andFilter.toArray(new Predicate[andFilter.size()]));
        criteria.where(ap);
        List<LMesswert> result = filter(criteria);
        return new Response(true, 200, result);
    }

}
