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

import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.rest.RequestMethod;
import de.intevation.lada.util.rest.Response;

@AuthorizationConfig(type=AuthorizationType.OPEN_ID)
public class TestAuthorization implements Authorization {

    @Override
    public UserInfo getInfo(Object source) {
        UserInfo info = new UserInfo();
        info.setName("testeins");
        List<String> roles = new ArrayList<String>();
        roles.add("mst_06010");
        roles.add("mst_11010");
        roles.add("ImisWorld");
        info.setRoles(roles);
        List<String> netz = new ArrayList<String>();
        netz.add("06");
        netz.add("11");
        info.setNetzbetreiber(netz);
        List<String> mess = new ArrayList<String>();
        mess.add("06010");
        mess.add("11010");
        info.setMessstellen(mess);
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
    public boolean isAuthorized(UserInfo userInfo, Object data) {
        return true;
    }

    @Override
    public boolean isReadOnly(Integer probeId) {
        return false;
    }

}
