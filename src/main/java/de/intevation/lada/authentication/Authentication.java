package de.intevation.lada.authentication;

import javax.ws.rs.core.HttpHeaders;

public interface Authentication
{
    public AuthenticationResponse authorizedGroups(HttpHeaders headers)
    throws AuthenticationException;

}
