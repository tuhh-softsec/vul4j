/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3) 
 * and comes with ABSOLUTELY NO WARRANTY! Check out 
 * the documentation coming with IMIS-Labordaten-Application for details. 
 */
package de.intevation.lada.manage;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityExistsException;
import javax.persistence.TransactionRequiredException;

/**
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
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
    public void delete(Object object)
    throws IllegalArgumentException,
        TransactionRequiredException;

	void persist(Object object) throws EntityExistsException,
			IllegalArgumentException, EJBTransactionRolledbackException,
			TransactionRequiredException;
}
