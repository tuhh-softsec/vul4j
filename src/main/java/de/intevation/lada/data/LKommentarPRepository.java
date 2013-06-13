package de.intevation.lada.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.intevation.lada.manage.LKommentarPManager;
import de.intevation.lada.model.LKommentarP;
import de.intevation.lada.rest.Response;


@Named("lkommentarRepository")
public class LKommentarPRepository
extends Repository
{
    /**
     * The entitymanager managing the data.
     */
    @Inject
    private EntityManager em;

    @Inject
    private LKommentarPManager manager;

    @Inject
    private Logger logger;

    public Response filter(Map<String, String> filter) {
        if (filter.isEmpty()) {
            return findAll(LKommentarP.class);
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LKommentarP> criteria = cb.createQuery(LKommentarP.class);
        Root<LKommentarP> member = criteria.from(LKommentarP.class);
        if (filter.containsKey("probe")) {
            criteria.where(
                cb.equal(member.get("probeId"), filter.get("probe")));
        }
        else {
            return new Response(false, 600, new ArrayList<LKommentarP>());
        }

        List<LKommentarP> result = filter(criteria);
        return new Response(true, 200, result);
    }

    public Response create(Object object) {
        if (!(object instanceof LKommentarP)) {
            return new Response(false, 602, object);
        }
        LKommentarP kommentar = (LKommentarP)object;
        try {
            manager.create(kommentar);
            return new Response(true, 200, kommentar);
        }
        catch(EntityExistsException eee) {
            return new Response(false, 601, kommentar);
        }
        catch(IllegalArgumentException iae) {
            return new Response(false, 602, kommentar);
        }
        catch(TransactionRequiredException tre) {
            logger.log(Level.INFO, "exception: " + tre);
            return new Response(false, 603, kommentar);
        }
    }

    @Override
    public Response update(Object object) {
        return new Response(false, 698, new ArrayList<LKommentarP>());
    }
}
