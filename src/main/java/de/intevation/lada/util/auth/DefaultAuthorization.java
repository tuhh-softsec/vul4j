package de.intevation.lada.util.auth;

import javax.ejb.Stateless;

import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.rest.RequestMethod;
import de.intevation.lada.util.rest.Response;

@Stateless
@AuthorizationConfig(type=AuthorizationType.NONE)
public class DefaultAuthorization implements Authorization {

    @Override
    public UserInfo getInfo(Object source) {
        return new UserInfo();
    }

    @Override
    public <T> Response filter(Object source, Response data, Class<T> clazz) {
        return data;
    }

    @Override
    public <T> boolean isAuthorized(
        Object source, Object data, RequestMethod method, Class<T> clazz) {
        return true;
    }

    @Override
    public boolean isReadOnly(Integer probeId) {
        return false;
    }

    @Override
    public boolean isAuthorized(UserInfo userInfo, Object data) {
        return true;
    }
}
