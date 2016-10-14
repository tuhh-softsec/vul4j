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

import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.Probe;
import de.intevation.lada.model.land.StatusProtokoll;
import de.intevation.lada.model.stammdaten.MessStelle;
import de.intevation.lada.model.stammdaten.StatusKombi;
import de.intevation.lada.util.rest.RequestMethod;
import de.intevation.lada.util.rest.Response;

public class MessungIdAuthorizer extends BaseAuthorizer {

    @Override
    public <T> boolean isAuthorized(
        Object data,
        RequestMethod method,
        UserInfo userInfo,
        Class<T> clazz
    ) {
        Method m;
        try {
            m = clazz.getMethod("getMessungsId");
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
        Messung messung = repository.getByIdPlain(Messung.class, id, "land");
        Probe probe = repository.getByIdPlain(
            Probe.class,
            messung.getProbeId(),
            "land");
        if (messung.getStatus() == null) {
            return false;
        }
        StatusProtokoll status = repository.getByIdPlain(
            StatusProtokoll.class,
            messung.getStatus(),
            "land");
        StatusKombi kombi = repository.getByIdPlain(
            StatusKombi.class,
            status.getStatusKombi(),
            "stamm");
        return (method == RequestMethod.POST ||
                method == RequestMethod.PUT ||
                method == RequestMethod.DELETE ||
                kombi.getStatusWert().getId() != 0) &&
            getAuthorization(userInfo, probe);
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
     * Authorize a single data object that has a messungsId Attribute.
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
            Method getMessungsId = clazz.getMethod("getMessungsId");
            Integer id = (Integer)getMessungsId.invoke(data);
            Messung messung = repository.getByIdPlain(
                Messung.class,
                id,
                "land");
            Probe probe = repository.getByIdPlain(
                Probe.class,
                messung.getProbeId(),
                "land");

            boolean readOnly = true;
            boolean owner = false;
            MessStelle mst = repository.getByIdPlain(MessStelle.class, probe.getMstId(), "stamm");
            if (!userInfo.getNetzbetreiber().contains(
                    mst.getNetzbetreiberId())) {
                owner = false;
                readOnly = true;
            }
            else {
                if (userInfo.belongsTo(probe.getMstId(), probe.getLaborMstId())) {
                    owner = true;
                }
                else {
                    owner = false;
                }
                readOnly = this.isMessungReadOnly(messung.getId());
            }

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
