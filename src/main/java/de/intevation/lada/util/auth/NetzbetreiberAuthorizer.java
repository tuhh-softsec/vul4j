package de.intevation.lada.util.auth;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.intevation.lada.util.rest.RequestMethod;
import de.intevation.lada.util.rest.Response;

public class NetzbetreiberAuthorizer extends BaseAuthorizer {

    @Override
    public <T> boolean isAuthorized(
        Object data,
        RequestMethod method,
        UserInfo userInfo,
        Class<T> clazz
    ) {
        Method m;
        try {
            m = clazz.getMethod("getNetzbetreiberId");
        } catch (NoSuchMethodException | SecurityException e1) {
            return false;
        }
        String id;
        try {
            id = (String) m.invoke(data);
        } catch (IllegalAccessException |
            IllegalArgumentException |
            InvocationTargetException e
        ) {
            return false;
        }
        return (method == RequestMethod.POST ||
            method == RequestMethod.PUT ||
            method == RequestMethod.DELETE) &&
            userInfo.getNetzbetreiber().contains(id) &&
            userInfo.getFunktionen().contains(4);
    }

    @Override
    public <T> Response filter(
        Response data,
        UserInfo userInfo,
        Class<T> clazz
    ) {
        return data;
    }

}
