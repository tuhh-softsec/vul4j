package de.intevation.lada.manage;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityExistsException;
import javax.persistence.TransactionRequiredException;

@Stateless
public interface Manager
{
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(Object object)
    throws EntityExistsException,
        IllegalArgumentException,
        EJBTransactionRolledbackException,
        TransactionRequiredException;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(Object object)
    throws EntityExistsException,
        IllegalArgumentException,
        EJBTransactionRolledbackException,
        TransactionRequiredException;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public <T> void delete(Object object)
    throws IllegalArgumentException,
        TransactionRequiredException;
}
