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

import de.intevation.lada.model.stammdaten.Auth;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.rest.RequestMethod;
import de.intevation.lada.util.rest.Response;

@AuthorizationConfig(type=AuthorizationType.NONE)
public class TestAuthorization implements Authorization {

    @Override
    public UserInfo getInfo(Object source) {
        UserInfo info = new UserInfo();
        info.setName("testeins");
        List<Auth> auth = new ArrayList<Auth>();
        Auth a1 = new Auth();
        a1.setFunktionId(0);
        a1.setLdapGroup("mst_06010");
        a1.setMstId("06010");
        a1.setNetzbetreiberId("06");
        auth.add(a1);
        Auth a2 = new Auth();
        a2.setFunktionId(0);
        a2.setLdapGroup("mst_11010");
        a2.setNetzbetreiberId("11");
        a2.setMstId("11010");
        auth.add(a2);
        Auth a3 = new Auth();
        a3.setLdapGroup("Imis_world");
        a3.setFunktionId(0);
        auth.add(a3);
        List<String> roles = new ArrayList<String>();
        roles.add("mst_06010");
        roles.add("mst_11010");
        roles.add("ImisWorld");
        info.setAuth(auth);
        return info;
    }

    @Override
    public <T> Response filter(Object source, Response data, Class<T> clazz) {
        return data;
    }

    @Override
    public <T> boolean isAuthorized(Object source, Object data,
            RequestMethod method, Class<T> clazz) {
        return true;
    }

    @Override
    public <T> boolean isAuthorizedById(Object source, Object id, RequestMethod method, Class<T> clazz) {
        return true;
    }

    @Override
    public <T> boolean isAuthorized(UserInfo userInfo, Object data, Class<T> clazz) {
        return true;
    }

    @Override
    public <T> boolean isAuthorizedOnNew(UserInfo userInfo, Object data, Class<T> clazz) {
        return true;
    }

    @Override
    public boolean isReadOnly(Integer probeId) {
        return false;
    }

    @Override
    public boolean isMessungReadOnly(Integer messungId) {
        return false;
    }
}
