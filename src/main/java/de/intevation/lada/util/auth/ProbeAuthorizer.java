/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.util.auth;

import java.util.ArrayList;
import java.util.List;

import de.intevation.lada.model.land.Probe;
import de.intevation.lada.model.stammdaten.MessStelle;
import de.intevation.lada.util.data.Strings;
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
        Probe probe = (Probe) data;
        if (method == RequestMethod.PUT
            || method == RequestMethod.DELETE) {
            return !isProbeReadOnly(probe.getId())
                && getAuthorization(userInfo, probe);
        }
        return getAuthorization(userInfo, probe);
    }

    @Override
    public <T> boolean isAuthorizedById(
        Object id,
        RequestMethod method,
        UserInfo userInfo,
        Class<T> clazz
    ) {
        Probe probe = repository.getByIdPlain(Probe.class, id, Strings.LAND);
        return isAuthorized(probe, method, userInfo, clazz);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Response filter(
        Response data,
        UserInfo userInfo,
        Class<T> clazz
    ) {
        if (data.getData() instanceof List<?>) {
            List<Probe> proben = new ArrayList<Probe>();
            for (Probe probe :(List<Probe>) data.getData()) {
                proben.add(setAuthData(userInfo, probe));
            }
            data.setData(proben);
        } else if (data.getData() instanceof Probe) {
            Probe probe = (Probe) data.getData();
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
    private Probe setAuthData(UserInfo userInfo, Probe probe) {
        MessStelle mst =
            repository.getByIdPlain(
                MessStelle.class, probe.getMstId(), Strings.STAMM);
        if (!userInfo.getNetzbetreiber().contains(mst.getNetzbetreiberId())) {
            probe.setOwner(false);
            probe.setReadonly(true);
            return probe;
        }
        if (userInfo.belongsTo(probe.getMstId(), probe.getLaborMstId())) {
            probe.setOwner(true);
        } else {
            probe.setOwner(false);
        }
        probe.setReadonly(this.isProbeReadOnly(probe.getId()));
        return probe;
    }
}
