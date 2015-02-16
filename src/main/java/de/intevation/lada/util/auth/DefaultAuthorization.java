package de.intevation.lada.util.auth;

import java.util.Map;

import javax.ejb.Stateless;
import javax.ws.rs.core.HttpHeaders;

import de.intevation.lada.util.annotation.AuthorizationConfig;

@Stateless
@AuthorizationConfig(type=AuthorizationType.NONE)
public class DefaultAuthorization implements Authorization {

    @Override
    public Map<String, Object> getInfo(HttpHeaders headers) {
        return null;
    }

}
