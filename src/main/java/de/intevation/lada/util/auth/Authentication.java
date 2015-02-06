package de.intevation.lada.util.auth;

import javax.ws.rs.core.HttpHeaders;

public interface Authentication {

    public boolean isAuthenticated(HttpHeaders headers);
}
