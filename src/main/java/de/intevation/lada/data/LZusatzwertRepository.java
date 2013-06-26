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
import javax.persistence.criteria.Root;

import de.intevation.lada.manage.Manager;
import de.intevation.lada.model.LZusatzWert;
import de.intevation.lada.model.LZusatzWertId;
import de.intevation.lada.rest.Response;

/**
 * This Container is an interface to request, filter and select LZusatzWert
 * obejcts from the connected database.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Named("lzusatzwertrepository")
public class LZusatzwertRepository
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
        if (!(object instanceof LZusatzWert)) {
            return new Response(false, 602, object);
        }
        LZusatzWert zusatzwert = (LZusatzWert)object;
        LZusatzWertId id =
            new LZusatzWertId(
                zusatzwert.getProbeId(),
                zusatzwert.getPzsId());
        zusatzwert.setId(id);
        Response response = new Response(true, 200, zusatzwert);
        // Try to save the new LProbe.
        try {
            manager.create(zusatzwert);
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
        if (!(object instanceof LZusatzWert)) {
            return new Response(false, 602, object);
        }
        LZusatzWert zusatzwert = (LZusatzWert)object;
        LZusatzWertId id = new LZusatzWertId(
            zusatzwert.getProbeId(),
            zusatzwert.getPzsId());
        zusatzwert.setId(id);
        Response response = new Response(true, 200, zusatzwert);
        try {
            manager.update(zusatzwert);
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
        CriteriaQuery<LZusatzWert> criteria = cb.createQuery(LZusatzWert.class);
        Root<LZusatzWert> member = criteria.from(LZusatzWert.class);
        if (filter.containsKey("probe")) {
            criteria.where(cb.equal(member.get("probeId"), filter.get("probe")));
        }
        List<LZusatzWert> result = filter(criteria);
        return new Response(true, 200, result);
    }

}
