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

import de.intevation.lada.model.stammdaten.NetzBetreiber;
import de.intevation.lada.model.stammdaten.Ort;
import de.intevation.lada.util.data.Strings;
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
        String id;
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
        return isAuthorizedById(id, method, userInfo, clazz);
    }

    @Override
    public <T> boolean isAuthorizedById(
        Object id,
        RequestMethod method,
        UserInfo userInfo,
        Class<T> clazz
    ) {
        String netId = (String) id;
        /* If model is an ort instance check:
           - If user tries to edit: Has user function 4 and the same mstId as the ort? OR
           - If user tries to create: Can user edit probe objects?
        */
        if (clazz.isAssignableFrom(de.intevation.lada.model.stammdaten.Ort.class)) {
            return
            ((method == RequestMethod.PUT
              || method == RequestMethod.DELETE
            ) && userInfo.getFunktionenForNetzbetreiber(netId).contains(4)
              && userInfo.getNetzbetreiber().contains(netId))
            || method == RequestMethod.POST;
        } else {
            return
            ((method == RequestMethod.PUT
            || method == RequestMethod.POST
            || method == RequestMethod.DELETE
            ) && userInfo.getFunktionenForNetzbetreiber(netId).contains(4));
        }
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
