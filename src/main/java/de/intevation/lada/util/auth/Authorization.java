package de.intevation.lada.util.auth;

import java.util.Map;

import javax.ws.rs.core.HttpHeaders;

public interface Authorization {
    public Map<String, Object> getInfo(HttpHeaders headers);
}
