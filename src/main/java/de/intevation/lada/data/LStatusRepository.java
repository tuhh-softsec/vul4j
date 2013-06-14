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

import de.intevation.lada.manage.LStatusManager;
import de.intevation.lada.manage.LZusatzwertManager;
import de.intevation.lada.model.LStatus;
import de.intevation.lada.rest.Response;

/**
 * This Container is an interface to request, filter and select LMesswert
 * obejcts from the connected database.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Named("lstatusrepository")
public class LStatusRepository
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
    private LStatusManager manager;

    @Override
    public Response create(Object object) {
        if (!(object instanceof LStatus)) {
            return new Response(false, 602, object);
        }
        LStatus status = (LStatus)object;
        Response response = new Response(true, 200, status);
        // Try to save the new LProbe.
        try {
            manager.create(status);
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
        if (!(object instanceof LStatus)) {
            return new Response(false, 602, object);
        }
        LStatus status = (LStatus)object;
        Response response = new Response(true, 200, status);
        try {
            manager.update(status);
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
        CriteriaQuery<LStatus> criteria = cb.createQuery(LStatus.class);
        Root<LStatus> member = criteria.from(LStatus.class);
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
        List<LStatus> result = filter(criteria);
        return new Response(true, 200, result);
    }

}
