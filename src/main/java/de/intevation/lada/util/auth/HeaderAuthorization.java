/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.util.auth;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import de.intevation.lada.model.land.LKommentarM;
import de.intevation.lada.model.land.LKommentarP;
import de.intevation.lada.model.land.LMessung;
import de.intevation.lada.model.land.LMesswert;
import de.intevation.lada.model.land.LOrtszuordnung;
import de.intevation.lada.model.land.LProbe;
import de.intevation.lada.model.land.LStatusProtokoll;
import de.intevation.lada.model.land.LZusatzWert;
import de.intevation.lada.model.stamm.Auth;
import de.intevation.lada.model.stamm.DatensatzErzeuger;
import de.intevation.lada.model.stamm.LadaUser;
import de.intevation.lada.model.stamm.MessprogrammKategorie;
import de.intevation.lada.model.stamm.Ort;
import de.intevation.lada.model.stamm.Probenehmer;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.RequestMethod;
import de.intevation.lada.util.rest.Response;

/**
 * Authorize a user via HttpServletRequest attributes.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@AuthorizationConfig(type=AuthorizationType.HEADER)
public class HeaderAuthorization implements Authorization {

    @Inject
    private Logger logger;

    /**
     * The Repository used to read from Database.
     */
    @Inject
    @RepositoryConfig(type=RepositoryType.RW)
    private Repository repository;

    @SuppressWarnings("rawtypes")
    private Map<Class, Authorizer> authorizers;
    @Inject ProbeAuthorizer probeAuthorizer;
    @Inject MessungAuthorizer messungAuthorizer;
    @Inject ProbeIdAuthorizer pIdAuthorizer;
    @Inject MessungIdAuthorizer mIdAuthorizer;
    @Inject NetzbetreiberAuthorizer netzAuthorizer;

    @SuppressWarnings("rawtypes")
    @PostConstruct
    public void init() {
        authorizers = new HashMap<Class, Authorizer>();
        authorizers.put(LProbe.class, probeAuthorizer);
        authorizers.put(LMessung.class, messungAuthorizer);
        authorizers.put(LOrtszuordnung.class, pIdAuthorizer);
        authorizers.put(LKommentarP.class, pIdAuthorizer);
        authorizers.put(LZusatzWert.class, pIdAuthorizer);
        authorizers.put(LKommentarM.class, mIdAuthorizer);
        authorizers.put(LMesswert.class, mIdAuthorizer);
        authorizers.put(LStatusProtokoll.class, mIdAuthorizer);
        authorizers.put(Probenehmer.class, netzAuthorizer);
        authorizers.put(DatensatzErzeuger.class, netzAuthorizer);
        authorizers.put(MessprogrammKategorie.class, netzAuthorizer);
        authorizers.put(Ort.class, netzAuthorizer);
    }

    /**
     * Request user informations using the HttpServletRequest.
     *
     * @param source    The HttpServletRequest
     * @return The UserInfo object containing username and groups.
     */
    @Override
    public UserInfo getInfo(Object source) {
        if (source instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest)source;
            String roleString =
                request.getAttribute("lada.user.roles").toString();
            UserInfo info = getGroupsFromDB(roleString);
            info.setName(request.getAttribute("lada.user.name").toString());
            QueryBuilder<LadaUser> builder = new QueryBuilder<LadaUser>(
                repository.entityManager("stamm"),
                LadaUser.class
            );
            builder.and("name", info.getName());
            List<LadaUser> user = repository.filterPlain(builder.getQuery(), "stamm");
            if (user == null || user.isEmpty()) {
                LadaUser newUser = new LadaUser();
                newUser.setName(info.getName());
                Response r = repository.create(newUser, "stamm");
                user = repository.filterPlain(builder.getQuery(), "stamm");
            }
            info.setUserId(user.get(0).getId());
            return info;
        }
        return null;
    }

    /**
     * Filter a list of data objects using the user informations contained in
     * the HttpServletRequest.
     *
     * @param source    The HttpServletRequest
     * @param data      The Response object containing the data.
     * @param clazz     The data object class.
     * @return The Response object containing the filtered data.
     */
    @Override
    public <T> Response filter(Object source, Response data, Class<T> clazz) {
        UserInfo userInfo = this.getInfo(source);
        if (userInfo == null) {
            return data;
        }
        Authorizer authorizer = authorizers.get(clazz);
        //This is a hack... Allows wildcard for unknown classes.
        if (authorizer == null) {
            return data;
        }
        return authorizer.filter(data, userInfo, clazz);
    }

    /**
     * Check whether a user is authorized to operate on the given data.
     *
     * @param source    The HttpServletRequest containing user information.
     * @param data      The data to test.
     * @param method    The Http request type.
     * @param clazz     The data object class.
     * @return True if the user is authorized else returns false.
     */
    @Override
    public <T> boolean isAuthorized(
        Object source,
        Object data,
        RequestMethod method,
        Class<T> clazz
    ) {
        UserInfo userInfo = this.getInfo(source);
        if (userInfo == null) {
            return false;
        }
        Authorizer authorizer = authorizers.get(clazz);
        //This is a hack... Allows wildcard for unknown classes.
        if (authorizer == null) {
            return true;
        }
        return authorizer.isAuthorized(data, method, userInfo, clazz);
    }

    /**
     * Request the lada specific groups.
     *
     * @param roles     The roles defined in the OpenId server.
     * @return The UserInfo contianing roles and user name.
     */
    private UserInfo getGroupsFromDB(String roles) {
        QueryBuilder<Auth> builder = new QueryBuilder<Auth>(
            repository.entityManager("stamm"),
            Auth.class);
        roles = roles.replace("[","");
        roles = roles.replace("]","");
        roles = roles.replace(" ","");
        String[] mst = roles.split(",");
        builder.andIn("ldapGroup", Arrays.asList(mst));
        Response response = repository.filter(builder.getQuery(), "stamm");
        @SuppressWarnings("unchecked")
        List<Auth> auth = (List<Auth>)response.getData();
        UserInfo userInfo = new UserInfo();
        userInfo.setAuth(auth);
        return userInfo;
    }

    /**
     * Test whether a probe is readonly.
     *
     * @param probeId   The probe Id.
     * @return True if the probe is readonly.
     */
    @Override
    public boolean isReadOnly(Integer probeId) {
        EntityManager manager = repository.entityManager("land");
        QueryBuilder<LMessung> builder =
            new QueryBuilder<LMessung>(
                manager,
                LMessung.class);
        builder.and("probeId", probeId);
        Response response = repository.filter(builder.getQuery(), "land");
        @SuppressWarnings("unchecked")
        List<LMessung> messungen = (List<LMessung>) response.getData();
        for (int i = 0; i < messungen.size(); i++) {
            if (messungen.get(i).getStatus() == null) {
                continue;
            }
            LStatusProtokoll status = repository.getByIdPlain(
                LStatusProtokoll.class, messungen.get(i).getStatus(), "land");
            if (status.getStatusWert() != 0 && status.getStatusWert() != 4) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether a user is authorized to operate on the given probe.
     *
     * @param userInfo  The user information.
     * @param data      The probe data to test.
     * @return True if the user is authorized else returns false.
     */
    @Override
    public <T> boolean isAuthorized(
        UserInfo userInfo,
        Object data,
        Class<T> clazz
    ) {
        Authorizer authorizer = authorizers.get(clazz);
        //This is a hack... Allows wildcard for unknown classes.
        if (authorizer == null) {
            return true;
        }
        return authorizer.isAuthorized(data, RequestMethod.GET, userInfo, clazz);
    }
}
