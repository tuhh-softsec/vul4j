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
 * @author rrenkert
 */
@Stateless
public class ReadOnlyRepository extends AbstractRepository {

    @Inject
    private Logger logger;

    public ReadOnlyRepository() {
    }

    @Override
    public Response create(Object object, String dataSource) {
        return null;
    }

    @Override
    public Response update(Object object, String dataSource) {
        return null;
    }

    @Override
    public Response delete(Object object, String dataSource) {
        return null;
    }

    @Override
    public <T> Response filter(CriteriaQuery<T> filter, String dataSource) {
        List<T> result =
            emp.entityManager(dataSource).createQuery(filter).getResultList();
        return new Response(true, 200, result);
    }

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

    public <T> Response getAll(Class<T> clazz, String dataSource) {
        EntityManager manager = emp.entityManager(dataSource);
        QueryBuilder<T> builder =
            new QueryBuilder<T>(manager, clazz);
        List<T> result =
            manager.createQuery(builder.getQuery()).getResultList();
        return new Response(true, 200, result);
    }

    @Override
    public <T> Response getById(Class<T> clazz, Object id, String dataSource) {
        T item = emp.entityManager(dataSource).find(clazz, id);
        if (item == null) {
            return new Response(false, 600, null);
        }
        return new Response(true, 200, item);
    }
}
