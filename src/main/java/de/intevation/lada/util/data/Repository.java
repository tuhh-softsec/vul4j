/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.util.data;

import java.util.List;

import javax.json.JsonArray;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

import de.intevation.lada.util.rest.Response;

/**
 * This generic Container is an interface to request and select Data
 * objects from the connected database.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public interface Repository {

     Response create(Object object, String dataSource);

     Response update(Object object, String dataSource);

     Response delete(Object object, String dataSource);

     <T> Response filter(CriteriaQuery<T> filter, String dataSource);

     <T> List<T> filterPlain(CriteriaQuery<T> filter, String dataSource);

     <T> List<T> filterPlain(
         QueryBuilder<T> query,
         JsonArray filter,
         String dataSource);

     <T> Response filter(
        CriteriaQuery<T> filter,
        int size,
        int start,
        String dataSource);

    <T> List<T> filterPlain(
        CriteriaQuery<T> filter,
        int size,
        int start,
        String dataSource);

    <T> Response getAll(Class<T> clazz, String dataSource);

    <T> List<T> getAllPlain(Class<T> clazz, String dataSource);

    <T> Response getById(Class<T> clazz, Object id, String dataSource);

    <T> T getByIdPlain(Class<T> clazz, Object id, String dataSource);

    Query queryFromString(String sql, String dataSource);

    EntityManager entityManager(String dataSource);
}
