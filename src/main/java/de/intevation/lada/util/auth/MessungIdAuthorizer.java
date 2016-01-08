package de.intevation.lada.util.auth;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.intevation.lada.model.land.LMessung;
import de.intevation.lada.model.land.LProbe;
import de.intevation.lada.model.land.LStatusProtokoll;
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
        LMessung messung = repository.getByIdPlain(LMessung.class, id, "land");
        LProbe probe = repository.getByIdPlain(
            LProbe.class,
            messung.getProbeId(),
            "land");
        if (messung.getStatus() == null) {
            return false;
        }
        LStatusProtokoll status = repository.getByIdPlain(
            LStatusProtokoll.class,
            messung.getStatus(),
            "land");
        return (method == RequestMethod.POST ||
                method == RequestMethod.PUT ||
                method == RequestMethod.DELETE ||
                status.getStatusWert() != 0) &&
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
            LMessung messung = repository.getByIdPlain(
                LMessung.class,
                id,
                "land");
            LProbe probe = repository.getByIdPlain(
                LProbe.class,
                messung.getProbeId(),
                "land");

            boolean readOnly = true;
            boolean owner = false;
            if (!userInfo.getNetzbetreiber().contains(
                    probe.getNetzbetreiberId())) {
                owner = false;
                readOnly = true;
            }
            else {
                if (userInfo.getMessstellen().contains(probe.getMstId())) {
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
