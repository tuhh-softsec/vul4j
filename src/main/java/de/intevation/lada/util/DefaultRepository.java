/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.util;

import javax.ejb.EJBTransactionRolledbackException;
import javax.persistence.EntityExistsException;
import javax.persistence.TransactionRequiredException;

import de.intevation.lada.util.rest.Response;


/**
 * @author rrenkert
 */
public class DefaultRepository extends ReadOnlyRepository {

    public DefaultRepository() {
    }

    @Override
    public Response create(Object object) {
        Response response = new Response(true, 200, object);
        try {
            this.persistInDatabase(object);
        }
        catch (EntityExistsException eee) {
            return new Response(false, 601, object);
        }
        catch (IllegalArgumentException iae) {
            return new Response(false, 602, object);
        }
        catch (TransactionRequiredException tre) {
            return new Response(false, 603, object);
        }
        catch (EJBTransactionRolledbackException ete) {
            return new Response(false, 604, object);
        }
        return response;
    }

    @Override
    public Response update(Object object) {
        Response response = new Response(true, 200, object);
        try {
            this.persistInDatabase(object);
        }
        catch (EntityExistsException eee) {
            return new Response(false, 601, object);
        }
        catch (IllegalArgumentException iae) {
            return new Response(false, 602, object);
        }
        catch (TransactionRequiredException tre) {
            return new Response(false, 603, object);
        }
        catch (EJBTransactionRolledbackException ete) {
            return new Response(false, 604, object);
        }
        return response;
    }

    @Override
    public Response delete(Object object) {
        Response response = new Response(true, 200, null);
        try {
            this.removeFromDatabase(object);
        }
        catch (IllegalArgumentException iae) {
            return new Response(false, 602, object);
        }
        catch (TransactionRequiredException tre) {
            return new Response(false, 603, object);
        }
        return response;
    }
}
