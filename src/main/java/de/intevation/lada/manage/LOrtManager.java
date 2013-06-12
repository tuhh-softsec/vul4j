package de.intevation.lada.manage;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;

import de.intevation.lada.model.LOrt;

/**
 * This Manager provides databse operations for LOrt objects.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Stateless
public class LOrtManager
{
    @Inject
    private EntityManager em;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(LOrt ort)
    throws EntityExistsException,
        IllegalArgumentException,
        EJBTransactionRolledbackException,
        TransactionRequiredException{
        em.persist(ort);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(LOrt ort)
    throws EntityExistsException,
        IllegalArgumentException,
        EJBTransactionRolledbackException,
        TransactionRequiredException {
        em.merge(ort);
    }
}
