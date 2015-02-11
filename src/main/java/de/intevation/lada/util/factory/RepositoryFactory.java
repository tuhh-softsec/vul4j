/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.util.factory;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.New;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;

import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.DefaultRepository;
import de.intevation.lada.util.data.ReadOnlyRepository;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;


@RequestScoped
public class RepositoryFactory {

    @Produces
    Repository createRepository(InjectionPoint injectionPoint,
        @New ReadOnlyRepository readOnlyRepo,
        @New DefaultRepository defaultRepo) {
        Annotated annotated = injectionPoint.getAnnotated();
        RepositoryConfig config =
            annotated.getAnnotation(RepositoryConfig.class);
        if (config == null) {
            return readOnlyRepo;
        }
        Repository repository;
        if (config.type() == RepositoryType.RW) {
            repository = defaultRepo;
        }
        else {
            repository = readOnlyRepo;
        }
        return repository;
    }
}
