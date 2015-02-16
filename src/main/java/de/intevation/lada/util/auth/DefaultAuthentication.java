package de.intevation.lada.util.auth;

import javax.ejb.Stateless;
import javax.ws.rs.core.HttpHeaders;

import de.intevation.lada.util.annotation.AuthenticationConfig;

@Stateless
@AuthenticationConfig(type=AuthenticationType.NONE)
public class DefaultAuthentication implements Authentication {

    @Override
    public boolean isAuthenticated(HttpHeaders headers) {
        return true;
    }

}
