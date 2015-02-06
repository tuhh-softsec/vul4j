package de.intevation.lada.util.auth;

import javax.ws.rs.core.HttpHeaders;

public class DefaultAuthentication implements Authentication {

    @Override
    public boolean isAuthenticated(HttpHeaders headers) {
        return true;
    }

}
