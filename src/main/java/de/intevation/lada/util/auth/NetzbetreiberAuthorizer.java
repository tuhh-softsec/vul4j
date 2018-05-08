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
        Class<?> dataType = data.getClass();
        String id;
        //If data is not an id
        if (dataType != Integer.class && dataType != String.class){
            Method m;
            try {
                m = clazz.getMethod("getNetzbetreiberId");
            } catch (NoSuchMethodException | SecurityException e1) {
                return false;
            }
            try {
                id = (String) m.invoke(data);
            } catch (IllegalAccessException |
                IllegalArgumentException |
                InvocationTargetException e
            ) {
                return false;
            }
        } else {
            //Use data directly as model id
            id = dataType == String.class ? (String) data : data.toString();
        }

        return (method == RequestMethod.POST
            || method == RequestMethod.PUT
            || method == RequestMethod.DELETE
        ) && (
            userInfo.getFunktionenForNetzbetreiber(id).contains(4)
            // XXX: this currently allows any user, regardless of function,
            // to manipulate and delete any ort of his own netzbetreiber!
            || clazz.isAssignableFrom(de.intevation.lada.model.stammdaten.Ort.class)
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
