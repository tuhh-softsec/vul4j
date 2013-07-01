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


@Stateless
@Named("datamanager")
public class DataManager
implements Manager
{
    @Inject
    private EntityManager em;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(Object object)
    throws EntityExistsException,
        IllegalArgumentException,
        EJBTransactionRolledbackException,
        TransactionRequiredException {
        em.persist(object);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(Object object)
    throws EntityExistsException,
        IllegalArgumentException,
        EJBTransactionRolledbackException,
        TransactionRequiredException {
        em.merge(object);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public <T> void delete(Object object)
    throws IllegalArgumentException,
        TransactionRequiredException {
        em.remove(em.contains(object) ? object : em.merge(object));
    }
}
