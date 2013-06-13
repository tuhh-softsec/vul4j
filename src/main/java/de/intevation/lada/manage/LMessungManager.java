package de.intevation.lada.manage;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;

import de.intevation.lada.model.LMessung;

/**
 * This Manager provides database operations for LMessung objects.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Stateless
public class LMessungManager
{
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
        LMessung probe = em.find(LMessung.class, id);
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
    public void create(LMessung probe)
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
    public void update(LMessung probe)
    throws EntityExistsException,
        IllegalArgumentException,
        EJBTransactionRolledbackException,
        TransactionRequiredException {
        em.merge(probe);
    }
}
