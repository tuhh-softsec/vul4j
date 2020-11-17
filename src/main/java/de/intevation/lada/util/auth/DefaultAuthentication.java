/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.util.auth;

import javax.ejb.Stateless;
import javax.ws.rs.core.HttpHeaders;

import de.intevation.lada.util.annotation.AuthenticationConfig;

/**
 * Default implementation of the authentication mechanism.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Stateless
@AuthenticationConfig(type = AuthenticationType.NONE)
public class DefaultAuthentication implements Authentication {

    /**
     * For this type of authentication allways true.
     */
    @Override
    public boolean isAuthenticated(HttpHeaders headers) {
        return true;
    }

}
