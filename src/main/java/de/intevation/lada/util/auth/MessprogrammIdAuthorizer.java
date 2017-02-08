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
import java.util.ArrayList;
import java.util.List;

import de.intevation.lada.model.land.Messprogramm;
import de.intevation.lada.util.rest.RequestMethod;
import de.intevation.lada.util.rest.Response;

public class MessprogrammIdAuthorizer extends BaseAuthorizer {

    @Override
    public <T> boolean isAuthorized(
        Object data,
        RequestMethod method,
        UserInfo userInfo,
        Class<T> clazz
    ) {
        Method m;
        try {
            m = clazz.getMethod("getMessprogrammId");
        } catch (NoSuchMethodException | SecurityException e1) {
            return false;
        }
        Integer id;
        try {
            id = (Integer) m.invoke(data);
        } catch (IllegalAccessException |
            IllegalArgumentException |
            InvocationTargetException e
        ) {
            return false;
        }
        Messprogramm messprogramm =
            repository.getByIdPlain(Messprogramm.class, id, "land");
        if (userInfo.getMessstellen().contains(messprogramm.getMstId())) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Response filter(
        Response data,
        UserInfo userInfo,
        Class<T> clazz
    ) {
        if (data.getData() instanceof List<?>) {
            List<Object> objects = new ArrayList<Object>();
            for (Object object :(List<Object>)data.getData()) {
                objects.add(setAuthData(userInfo, object, clazz));
            }
            data.setData(objects);
        }
        else {
            Object object = data.getData();
            data.setData(setAuthData(userInfo, object, clazz));
        }
        return data;
    }
    /**
     * Authorize a single data object that has a probeId Attribute.
     *
     * @param userInfo  The user information.
     * @param data      The Response object containing the data.
     * @param clazz     The data object class.
     * @return A Response object containing the data.
     */
    private <T> Object setAuthData(
        UserInfo userInfo,
        Object data,
        Class<T> clazz
    ) {
        try {
            Method getMessprogrammId = clazz.getMethod("getMessprogrammId");
            Integer id = null;
            if (getMessprogrammId != null) {
                id = (Integer) getMessprogrammId.invoke(data);
            }
            else {
                return null;
            }
            Messprogramm messprogramm = repository.getByIdPlain(
                Messprogramm.class, id, "land");

            boolean owner = false;
            if (userInfo.belongsTo(
                    messprogramm.getMstId(),
                    messprogramm.getLaborMstId())
            ) {
                owner = true;
            }
            boolean readOnly = !owner;

            Method setOwner = clazz.getMethod("setOwner", boolean.class);
            Method setReadonly = clazz.getMethod("setReadonly", boolean.class);
            setOwner.invoke(data, owner);
            setReadonly.invoke(data, readOnly);
        } catch (NoSuchMethodException | SecurityException
            | IllegalAccessException | IllegalArgumentException
            | InvocationTargetException e) {
            return null;
        }
        return data;
    }
}
