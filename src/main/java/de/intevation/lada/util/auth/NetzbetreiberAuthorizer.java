/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
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
        return (method == RequestMethod.POST
            || method == RequestMethod.PUT
            || method == RequestMethod.DELETE
        ) && (
            userInfo.getFunktionenForNetzbetreiber(id).contains(4)
            // XXX: this currently allows any user, regardless of function,
            // to manipulate and delete any ort of his own netzbetreiber!
            || clazz.getName().equals("de.intevation.lada.model.stammdaten.Ort")
            && userInfo.getNetzbetreiber().contains(id)
        );
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
