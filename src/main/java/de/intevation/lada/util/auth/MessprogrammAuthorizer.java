/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.util.auth;

import javax.inject.Inject;

import de.intevation.lada.model.land.Messprogramm;
import de.intevation.lada.model.land.MessprogrammMmt;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.RequestMethod;
import de.intevation.lada.util.rest.Response;

public class MessprogrammAuthorizer implements Authorizer {

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    @Override
    public <T> boolean isAuthorized(
        Object data,
        RequestMethod method,
        UserInfo userInfo,
        Class<T> clazz
    ) {
        if (method == RequestMethod.GET) {
            // Allow read access to everybody
            return true;
        }
        Messprogramm messprogramm = null;
        if (data instanceof Messprogramm) {
            messprogramm = (Messprogramm)data;
        }
        else if (data instanceof MessprogrammMmt) {
            messprogramm = repository.getByIdPlain(
                Messprogramm.class,
                ((MessprogrammMmt)data).getMessprogrammId(),
                "land");
        }
        if (userInfo.getMessstellen().contains(messprogramm.getMstId())) {
            return true;
        }
        return false;
    }

    @Override
    public <T> Response filter(
        Response data,
        UserInfo userInfo,
        Class<T> clazz
    ) {
        // Allow read access to everybody
        return data;
    }
}
