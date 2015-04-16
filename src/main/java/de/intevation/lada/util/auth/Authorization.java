package de.intevation.lada.util.auth;

import de.intevation.lada.util.rest.RequestMethod;
import de.intevation.lada.util.rest.Response;

public interface Authorization {
    public UserInfo getInfo(Object source);
    public <T> Response filter(Object source, Response data, Class<T> clazz);
    public <T> boolean isAuthorized(
        Object source, Object data, RequestMethod method, Class<T> clazz);
    public boolean isAuthorized(UserInfo userInfo, Object data);
    boolean isReadOnly(Integer probeId);
}
