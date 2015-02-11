/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.util.data;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.TransactionRequiredException;

import org.apache.log4j.Logger;

import de.intevation.lada.util.rest.Response;


/**
 * Repository providing read and write access.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Stateless
public class DefaultRepository extends ReadOnlyRepository {

    @Inject
    private Logger logger;

    /**
     * Create and persist a new object in the database.
     *
     * @param object The new object.
     * @param dataSource The datasource.
     *
     * @return Response object containing the new object.
     */
    @Override
    public Response create(Object object, String dataSource) {
        try {
            this.persistInDatabase(object, dataSource);
        }
        catch (EntityExistsException eee) {
            logger.error("Could not persist " + object.getClass().getName() +
                ". Reason: " + eee.getClass().getName() + " - " +
                eee.getMessage());
            return new Response(false, 601, object);
        }
        catch (IllegalArgumentException iae) {
            logger.error("Could not persist " + object.getClass().getName() +
                ". Reason: " + iae.getClass().getName() + " - " +
                iae.getMessage());
            return new Response(false, 602, object);
        }
        catch (TransactionRequiredException tre) {
            logger.error("Could not persist " + object.getClass().getName() +
                ". Reason: " + tre.getClass().getName() + " - " +
                tre.getMessage());
            return new Response(false, 603, object);
        }
        catch (EJBTransactionRolledbackException ete) {
            logger.error("Could not persist " + object.getClass().getName() +
                ". Reason: " + ete.getClass().getName() + " - " +
                ete.getMessage());
            return new Response(false, 604, object);
        }
        Response response = new Response(true, 200, object);
        return response;
    }

    /**
     * Update an existing object in the database.
     *
     * @param object The object.
     * @param dataSource The datasource.
     *
     * @return Response object containing the upadted object.
     */
    @Override
    public Response update(Object object, String dataSource) {
        Response response = new Response(true, 200, object);
        try {
            this.updateInDatabase(object, dataSource);
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

    /**
     * Delete an object from the database.
     *
     * @param object The object.
     * @param dataSource The datasource.
     *
     * @return Response object.
     */
    @Override
    public Response delete(Object object, String dataSource) {
        Response response = new Response(true, 200, null);
        try {
            this.removeFromDatabase(object, dataSource);
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
