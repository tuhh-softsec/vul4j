/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.util.data;

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

    public Response create(Object object, String dataSource);

    public Response update(Object object, String dataSource);

    public Response delete(Object object, String dataSource);

    public <T> Response filter(CriteriaQuery<T> filter, String dataSource);

    public <T> Response filter(
        CriteriaQuery<T> filter,
        int size,
        int start,
        String dataSource);

    public <T> Response getAll(Class<T> clazz, String dataSource);

    public <T> Response getById(Class<T> clazz, Object id, String dataSource);

    public Query queryFromString(String sql, String dataSource);

    public EntityManager entityManager(String dataSource);
}
