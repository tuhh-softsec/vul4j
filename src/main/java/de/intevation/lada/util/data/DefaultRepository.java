/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.util.data;

import java.util.List;

import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;
import javax.persistence.criteria.CriteriaQuery;

import org.apache.log4j.Logger;

import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.rest.Response;


/**
 * Repository providing read and write access.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@RepositoryConfig(type=RepositoryType.RW)
@ApplicationScoped
public class DefaultRepository extends ReadOnlyRepository {

    @Inject
    private Logger logger;

    @Inject
    private DataTransaction transaction;

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
            transaction.persistInDatabase(object, dataSource);
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
            transaction.updateInDatabase(object, dataSource);
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
        Response response = new Response(true, 200, "");
        try {
            transaction.removeFromDatabase(object, dataSource);
        }
        catch (IllegalArgumentException iae) {
            return new Response(false, 602, object);
        }
        catch (TransactionRequiredException tre) {
            return new Response(false, 603, object);
        }
        catch (EJBTransactionRolledbackException ete) {
            return new Response(false, 696, object);
        }
        return response;
    }

    /**
     * Get objects from database using the given filter.
     *
     * @param filter Filter used to request objects.
     * @param datasource The datasource.
     *
     * @return Response object containing the filtered list of objects.
     */
    @Override
    public <T> Response filter(CriteriaQuery<T> filter, String dataSource) {
        List<T> result =
            transaction.entityManager(dataSource).createQuery(filter).getResultList();
        return new Response(true, 200, result);
    }


    /**
     * Get objects from database using the given filter.
     *
     * @param filter Filter used to request objects.
     * @param size The maximum amount of objects.
     * @param start The start index.
     * @param datasource The datasource.
     *
     * @return Response object containing the filtered list of objects.
     */
    @Override
    public <T> Response filter(
        CriteriaQuery<T> filter,
        int size,
        int start,
        String dataSource
    ) {
        List<T> result =
            transaction.entityManager(dataSource).createQuery(filter).getResultList();
        if (size > 0 && start > -1) {
            List<T> newList = result.subList(start, size + start);
            return new Response(true, 200, newList, result.size());
        }
        return new Response(true, 200, result);
    }

    /**
     * Get all objects.
     *
     * @param clazz The type of the objects.
     * @param dataSource The datasource.
     *
     * @return Response object containg all requested objects.
     */
    public <T> Response getAll(Class<T> clazz, String dataSource) {
        EntityManager manager = transaction.entityManager(dataSource);
        QueryBuilder<T> builder =
            new QueryBuilder<T>(manager, clazz);
        List<T> result =
            manager.createQuery(builder.getQuery()).getResultList();
        return new Response(true, 200, result);
    }

    /**
     * Get an object by its id.
     *
     * @param clazz The type of the object.
     * @param id The id of the object.
     * @param dataSource The datasource.
     *
     * @return Response object containg the requested object.
     */
    @Override
    public <T> Response getById(Class<T> clazz, Object id, String dataSource) {
        T item = transaction.entityManager(dataSource).find(clazz, id);
        if (item == null) {
            return new Response(false, 600, null);
        }
        return new Response(true, 200, item);
    }
}
