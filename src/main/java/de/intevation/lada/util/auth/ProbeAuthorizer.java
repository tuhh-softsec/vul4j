package de.intevation.lada.util.auth;

import java.util.ArrayList;
import java.util.List;

import de.intevation.lada.model.land.LProbe;
import de.intevation.lada.util.rest.RequestMethod;
import de.intevation.lada.util.rest.Response;

public class ProbeAuthorizer extends BaseAuthorizer {

    @Override
    public <T> boolean isAuthorized(
        Object data,
        RequestMethod method,
        UserInfo userInfo,
        Class<T> clazz
    ) {
        LProbe probe = (LProbe)data;
        if (method == RequestMethod.POST) {
            return getAuthorization(userInfo, probe);
        }
        else if (method == RequestMethod.PUT ||
                 method == RequestMethod.DELETE) {
            return !isProbeReadOnly(probe.getId());
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
            List<LProbe> proben = new ArrayList<LProbe>();
            for (LProbe probe :(List<LProbe>)data.getData()) {
                proben.add(setAuthData(userInfo, probe));
            }
            data.setData(proben);
        }
        else if (data.getData() instanceof LProbe) {
            LProbe probe = (LProbe)data.getData();
            data.setData(setAuthData(userInfo, probe));
        }
        return data;
    }

    /**
     * Set authorization data for the current probe object.
     *
     * @param userInfo  The user information.
     * @param probe     The probe object.
     * @return The probe.
     */
    private LProbe setAuthData(UserInfo userInfo, LProbe probe) {
        if (!userInfo.getNetzbetreiber().contains(probe.getNetzbetreiberId())) {
            probe.setOwner(false);
            probe.setReadonly(true);
            return probe;
        }
        if (userInfo.getMessstellen().contains(probe.getMstId())) {
            probe.setOwner(true);
        }
        else {
            probe.setOwner(false);
        }
        probe.setReadonly(this.isProbeReadOnly(probe.getId()));
        return probe;
    }
}
