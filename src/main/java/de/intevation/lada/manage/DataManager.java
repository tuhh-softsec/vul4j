package de.intevation.lada.manage;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;


/**
 * This data manager provides the interface to persist, remove and update
 * database objects.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Stateless
@Named("datamanager")
public class DataManager
implements Manager
{
    @Inject
    private EntityManager em;

    /**
     * Create a new database object.
     *
     * @param object    The new object.
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(Object object)
    throws EntityExistsException,
        IllegalArgumentException,
        EJBTransactionRolledbackException,
        TransactionRequiredException {
        em.persist(object);
    }

    /**
     * Update a database object.
     *
     * @param object    The object to update.
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(Object object)
    throws EntityExistsException,
        IllegalArgumentException,
        EJBTransactionRolledbackException,
        TransactionRequiredException {
        em.merge(object);
    }

    /**
     * Delete a database object.
     *
     * @param object    The object to delete.
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(Object object)
    throws IllegalArgumentException,
        TransactionRequiredException {
        em.remove(em.contains(object) ? object : em.merge(object));
    }
}
