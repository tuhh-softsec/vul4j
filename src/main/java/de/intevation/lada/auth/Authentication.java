/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3) 
 * and comes with ABSOLUTELY NO WARRANTY! Check out 
 * the documentation coming with IMIS-Labordaten-Application for details. 
 */
package de.intevation.lada.auth;

import javax.ws.rs.core.HttpHeaders;

/**
 * Defines the interface authentication modules that provide information about
 * the user.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public interface Authentication
{
    public boolean isAuthorizedUser(HttpHeaders headers)
    throws AuthenticationException;

    public AuthenticationResponse authorizedGroups(HttpHeaders headers)
    throws AuthenticationException;

    public boolean hasAccess(HttpHeaders headers, String probeId)
    throws AuthenticationException;

    public String getUserName(HttpHeaders headers)
    throws AuthenticationException;
}
