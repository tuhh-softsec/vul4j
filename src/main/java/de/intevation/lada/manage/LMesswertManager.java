package de.intevation.lada.manage;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;

import de.intevation.lada.model.LMesswert;

/**
 * This Manager provides databse operations for LMesswert objects.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Stateless
public class LMesswertManager
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
        LMesswert messwert = em.find(LMesswert.class, id);
        em.remove(messwert);
    }

    /**
     * Persist a new LMesswert object in the database.
     *
     * @param messwert The new LMesswert object.
     *
     * @throws EntityExistsException
     * @throws IllegalArgumentException
     * @throws TransactionRequiredException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(LMesswert messwert)
    throws EntityExistsException,
        IllegalArgumentException,
        EJBTransactionRolledbackException,
        TransactionRequiredException {
        em.persist(messwert);
    }

    /**
     * Updates a LMesswert object in the database.
     *
     * @param messwert The new LMesswert object.
     *
     * @throws EntityExistsException
     * @throws IllegalArgumentException
     * @throws TransactionRequiredException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(LMesswert messwert)
    throws EntityExistsException,
        IllegalArgumentException,
        EJBTransactionRolledbackException,
        TransactionRequiredException {
        em.merge(messwert);
    }
}
