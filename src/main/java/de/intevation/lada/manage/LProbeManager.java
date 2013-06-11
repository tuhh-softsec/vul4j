package de.intevation.lada.manage;

import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;

import de.intevation.lada.model.LProbe;
import de.intevation.lada.validation.ValidationException;
import de.intevation.lada.validation.Validator;

/**
 * This Manager provides databse operations for LProbe objects.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Stateless
public class LProbeManager {

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    /**
     * Delete a LProbe object by id.
     *
     * @param id
     * @throws Exception
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(String id) throws Exception {
        LProbe probe = em.find(LProbe.class, id);
        log.info("Deleting " + probe.getProbeId());
        em.remove(probe);
    }

    /**
     * Persist a new LProbe object in the database using the LProbeValidator.
     *
     * @param probe The new LProbe object.
     *
     * @throws EntityExistsException
     * @throws IllegalArgumentException
     * @throws TransactionRequiredException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(LProbe probe)
    throws EntityExistsException,
        IllegalArgumentException,
        EJBTransactionRolledbackException,
        TransactionRequiredException {
        em.persist(probe);
    }

    /**
     * Updates a LProbe object in the database.
     *
     * @param probe The new LProbe object.
     *
     * @throws EntityExistsException
     * @throws IllegalArgumentException
     * @throws TransactionRequiredException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(LProbe probe)
    throws EntityExistsException,
        IllegalArgumentException,
        EJBTransactionRolledbackException,
        TransactionRequiredException {
        em.merge(probe);
    }
}
