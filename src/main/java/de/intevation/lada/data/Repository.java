/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3) 
 * and comes with ABSOLUTELY NO WARRANTY! Check out 
 * the documentation coming with IMIS-Labordaten-Application for details. 
 */
package de.intevation.lada.data;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

import de.intevation.lada.rest.Response;

/**
 * This generic Container is an interface to request and select Data
 * obejcts from the connected database.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public interface Repository
{
    public Response create(Object object);

    public Response update(Object object);

    public Response delete(Object object);

    public <T> Response filter(CriteriaQuery<T> filter);

    public <T> Response filter(CriteriaQuery<T> filter, int size, int start);

    public <T> Response findAll(Class<T> clazz);

    public <T> Response findById(Class<T> clazz, String id);

    public EntityManager getEntityManager();
}