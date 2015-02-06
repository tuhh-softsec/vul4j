package de.intevation.lada.util.auth;

import java.util.Map;

import javax.ws.rs.core.HttpHeaders;

public class DefaultAuthorization implements Authorization {

    @Override
    public Map<String, Object> getInfo(HttpHeaders headers) {
        return null;
    }

}
