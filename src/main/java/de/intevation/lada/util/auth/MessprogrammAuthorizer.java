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

import javax.inject.Inject;

import de.intevation.lada.model.land.Messprogramm;
import de.intevation.lada.model.land.MessprogrammMmt;
import de.intevation.lada.model.stammdaten.MessStelle;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.util.rest.RequestMethod;
import de.intevation.lada.util.rest.Response;

public class MessprogrammAuthorizer implements Authorizer {

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    @Override
    public <T> boolean isAuthorized(
        Object data,
        RequestMethod method,
        UserInfo userInfo,
        Class<T> clazz
    ) {
        if (method == RequestMethod.GET) {
            // Allow read access to everybody
            return true;
        }
        Messprogramm messprogramm;
        if (data instanceof Messprogramm) {
            messprogramm = (Messprogramm)data;
        }
        else if (data instanceof MessprogrammMmt) {
            messprogramm = repository.getByIdPlain(
                Messprogramm.class,
                ((MessprogrammMmt)data).getMessprogrammId(),
                Strings.LAND);
        }
        else {
            return false;
        }
        String mstId = messprogramm.getMstId();
        if (mstId != null) {
            MessStelle mst = repository.getByIdPlain(
                MessStelle.class, mstId, Strings.STAMM);
            if (userInfo.getFunktionenForNetzbetreiber(
                    mst.getNetzbetreiberId()).contains(4)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public <T> boolean isAuthorizedById(Object id, RequestMethod method, UserInfo userInfo, Class<T> clazz) {
        Messprogramm mp = repository.getByIdPlain(Messprogramm.class, id, Strings.LAND);
        return isAuthorized(mp, method, userInfo, Messprogramm.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Response filter(
        Response data,
        UserInfo userInfo,
        Class<T> clazz
    ) {
        if (data.getData() instanceof List<?> &&
            !clazz.isAssignableFrom(MessprogrammMmt.class)) {
            List<Messprogramm> messprogramme = new ArrayList<Messprogramm>();
            for (Messprogramm messprogramm :(List<Messprogramm>)data.getData()) {
                messprogramme.add(setAuthData(userInfo, messprogramm));
            }
            data.setData(messprogramme);
        }
        else if (data.getData() instanceof Messprogramm) {
            Messprogramm messprogramm = (Messprogramm)data.getData();
            data.setData(setAuthData(userInfo, messprogramm));
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
    private Messprogramm setAuthData(UserInfo userInfo, Messprogramm messprogramm) {
        MessStelle mst = repository.getByIdPlain(MessStelle.class, messprogramm.getMstId(), Strings.STAMM);
        if (userInfo.getFunktionenForNetzbetreiber(
                mst.getNetzbetreiberId()).contains(4)) {
            messprogramm.setReadonly(false);
            return messprogramm;
        }
        else {
            messprogramm.setReadonly(true);
        }
        return messprogramm;
    }
}
