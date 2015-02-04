/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.util.factory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import de.intevation.lada.util.DefaultRepository;
import de.intevation.lada.util.EntityManagerProducer;
import de.intevation.lada.util.ReadOnlyRepository;
import de.intevation.lada.util.Repository;
import de.intevation.lada.util.RepositoryType;
import de.intevation.lada.util.annotation.RepositoryConfig;


@ApplicationScoped
public class RepositoryFactory {

    @Inject
    private EntityManagerProducer emp;

    @Produces
    Repository createRepository(InjectionPoint injectionPoint) {
        Annotated annotated = injectionPoint.getAnnotated();
        RepositoryConfig config = annotated.getAnnotation(RepositoryConfig.class);
        if (config == null) {
            return new ReadOnlyRepository();
        }
        Repository repository;
        if (config.type() == RepositoryType.RW) {
            repository = new DefaultRepository();
        }
        else {
            repository = new ReadOnlyRepository();
        }
        repository.setEntityManagerProducer(emp);
        repository.setDataSource(config.dataSource());
        return repository;
    }
}
