package de.intevation.lada.util.auth;

import de.intevation.lada.util.rest.RequestMethod;
import de.intevation.lada.util.rest.Response;

public interface Authorizer {

    public <T> boolean isAuthorized(
        Object data,
        RequestMethod method,
        UserInfo userInfo,
        Class<T> clazz);

    public <T> Response filter(
        Response data,
        UserInfo userInfo,
        Class<T> clazz);
}
