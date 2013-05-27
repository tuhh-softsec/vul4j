package de.intevation.lada.data;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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

    public List<LKommentarP> filter(String probeId) {
        if (probeId.isEmpty()) {
            return new ArrayList<LKommentarP>(0);
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LKommentarP> criteria = cb.createQuery(LKommentarP.class);
        Root<LKommentarP> member = criteria.from(LKommentarP.class);
        criteria.where(cb.equal(member.get("probeId"), probeId));

        return em.createQuery(criteria).getResultList();
    }

    public String create(LKommentarP kommentar) {
        try {
            manager.create(kommentar);
            return "";
        }
        catch(EntityExistsException eee) {
            return "Entity already exists.";
        }
        catch(IllegalArgumentException iae) {
            return "Object is not an entity.";
        }
        catch(TransactionRequiredException tre) {
            logger.log(Level.INFO, "exception: " + tre);
            return "Transaction failed.";
        }
    }
}
