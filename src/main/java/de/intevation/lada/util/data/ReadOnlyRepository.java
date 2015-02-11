/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.util.data;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

import org.apache.log4j.Logger;

import de.intevation.lada.util.rest.Response;


/**
 * Repository providing read access.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Stateless
public class ReadOnlyRepository extends AbstractRepository {

    @Inject
    private Logger logger;

    public ReadOnlyRepository() {
    }

    /**
     * NOT SUPPORTED.
     *
     * @return null
     */
    @Override
    public Response create(Object object, String dataSource) {
        return null;
    }

    /**
     * NOT SUPPORTED.
     *
     * @return null
     */
    @Override
    public Response update(Object object, String dataSource) {
        return null;
    }

    /**
     * NOT SUPPORTED.
     *
     * @return null
     */
    @Override
    public Response delete(Object object, String dataSource) {
        return null;
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
            emp.entityManager(dataSource).createQuery(filter).getResultList();
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
            emp.entityManager(dataSource).createQuery(filter).getResultList();
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
        logger.warn("ich bin ein logger");
        EntityManager manager = emp.entityManager(dataSource);
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
        T item = emp.entityManager(dataSource).find(clazz, id);
        if (item == null) {
            return new Response(false, 600, null);
        }
        return new Response(true, 200, item);
    }
}
