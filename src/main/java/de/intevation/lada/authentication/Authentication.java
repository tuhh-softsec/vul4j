package de.intevation.lada.authentication;

import javax.ws.rs.core.HttpHeaders;

/**
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

    public boolean isReadOnly(HttpHeaders headers, String probeId)
    throws AuthenticationException;
}
