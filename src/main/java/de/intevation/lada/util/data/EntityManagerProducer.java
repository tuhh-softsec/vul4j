/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.util.data;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.validation.UnexpectedTypeException;

/**
 * Factory class used to get entitymanager for a specific persistence unit.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Stateless
@LocalBean
public class EntityManagerProducer {

    @Resource
    private SessionContext ctx;

    private String jndiPath = "java:app/entitymanager/";

    /**
     * Constructor for multi-tenancy entity manager delegate.
     * Default jndi search path is 'java:app/entitymanager'.
     */
    public EntityManagerProducer() {
    }

    /**
     * Constructor for multi-tenancy entity manager delegate.
     *
     * @param jndiEnv The jndi path to search for datasources.
     *     Defaults to 'java:app/entitymanager'.
     */
    public EntityManagerProducer(String jndiPath) {
        this.jndiPath = jndiPath;
    }

    /**
     * Create an entity manager for a datasource.
     *
     * @throws UnexpectedTypeException
     * @param dataSourceName The jndi name of the datasource.
     * @return The entity manager for the datasource.
     */
    public EntityManager entityManager(String dataSourceName) {

        EntityManager entityManager = 
            (EntityManager) this.ctx.lookup(this.jndiPath + dataSourceName);

        if (entityManager == null) {
            throw new UnexpectedTypeException("Unknown data source name '" +
                dataSourceName + "'.");
        }

        return entityManager;

    }
}