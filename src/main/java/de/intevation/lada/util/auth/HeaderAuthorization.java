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

import de.intevation.lada.model.land.KommentarM;
import de.intevation.lada.model.land.KommentarP;
import de.intevation.lada.model.land.Messprogramm;
import de.intevation.lada.model.land.MessprogrammMmt;
import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.Messwert;
import de.intevation.lada.model.land.Ortszuordnung;
import de.intevation.lada.model.land.OrtszuordnungMp;
import de.intevation.lada.model.land.Probe;
import de.intevation.lada.model.land.StatusProtokoll;
import de.intevation.lada.model.land.ZusatzWert;
import de.intevation.lada.model.stammdaten.Auth;
import de.intevation.lada.model.stammdaten.DatensatzErzeuger;
import de.intevation.lada.model.stammdaten.LadaUser;
import de.intevation.lada.model.stammdaten.MessprogrammKategorie;
import de.intevation.lada.model.stammdaten.Ort;
import de.intevation.lada.model.stammdaten.Probenehmer;
import de.intevation.lada.model.stammdaten.StatusKombi;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.util.rest.RequestMethod;
import de.intevation.lada.util.rest.Response;

/**
 * Authorize a user via HttpServletRequest attributes.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@AuthorizationConfig(type=AuthorizationType.HEADER)
public class HeaderAuthorization implements Authorization {

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
    @Inject MessprogrammAuthorizer messprogrammAuthorizer;
    @Inject MessprogrammIdAuthorizer mpIdAuthorizer;

    @SuppressWarnings("rawtypes")
    @PostConstruct
    public void init() {
        authorizers = new HashMap<Class, Authorizer>();
        authorizers.put(Probe.class, probeAuthorizer);
        authorizers.put(Messung.class, messungAuthorizer);
        authorizers.put(Ortszuordnung.class, pIdAuthorizer);
        authorizers.put(KommentarP.class, pIdAuthorizer);
        authorizers.put(ZusatzWert.class, pIdAuthorizer);
        authorizers.put(KommentarM.class, mIdAuthorizer);
        authorizers.put(Messwert.class, mIdAuthorizer);
        authorizers.put(StatusProtokoll.class, mIdAuthorizer);
        authorizers.put(Probenehmer.class, netzAuthorizer);
        authorizers.put(DatensatzErzeuger.class, netzAuthorizer);
        authorizers.put(MessprogrammKategorie.class, netzAuthorizer);
        authorizers.put(Ort.class, netzAuthorizer);
        authorizers.put(Messprogramm.class, messprogrammAuthorizer);
        authorizers.put(MessprogrammMmt.class, messprogrammAuthorizer);
        authorizers.put(OrtszuordnungMp.class, mpIdAuthorizer);
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
                repository.entityManager(Strings.STAMM),
                LadaUser.class
            );
            builder.and("name", info.getName());
            List<LadaUser> user = repository.filterPlain(builder.getQuery(), Strings.STAMM);
            if (user == null || user.isEmpty()) {
                LadaUser newUser = new LadaUser();
                newUser.setName(info.getName());
                Response r = repository.create(newUser, Strings.STAMM);
                user = repository.filterPlain(builder.getQuery(), Strings.STAMM);
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
        if (authorizer == null) {
            return new Response(false, 699, null);
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
        // Do not authorize anything unknown
        if (authorizer == null) {
            return false;
        }
        return authorizer.isAuthorized(data, method, userInfo, clazz);
    }

    /**
     * Check whether a user is authorized to operate on the given data by the given object id.
     *
     * @param source    The HttpServletRequest containing user information.
     * @param id        The data's id to test.
     * @param method    The Http request type.
     * @param clazz     The data object class.
     * @return True if the user is authorized else returns false.
     */
    public <T> boolean isAuthorizedById(
        Object source,
        Object id,
        RequestMethod method,
        Class<T> clazz
    ) {
        UserInfo userInfo = this.getInfo(source);
        if (userInfo == null) {
            return false;
        }
        Authorizer authorizer = authorizers.get(clazz);
        // Do not authorize anything unknown
        if (authorizer == null) {
            return false;
        }
        return authorizer.isAuthorizedById(id, method, userInfo, clazz);
    }

    /**
     * Request the lada specific groups.
     *
     * @param roles     The roles defined in the OpenId server.
     * @return The UserInfo contianing roles and user name.
     */
    private UserInfo getGroupsFromDB(String roles) {
        QueryBuilder<Auth> builder = new QueryBuilder<Auth>(
            repository.entityManager(Strings.STAMM),
            Auth.class);
        roles = roles.replace("[","");
        roles = roles.replace("]","");
        roles = roles.replace(" ","");
        String[] mst = roles.split(",");
        builder.andIn("ldapGroup", Arrays.asList(mst));
        Response response = repository.filter(builder.getQuery(), Strings.STAMM);
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
        EntityManager manager = repository.entityManager(Strings.LAND);
        QueryBuilder<Messung> builder =
            new QueryBuilder<Messung>(
                manager,
                Messung.class);
        builder.and("probeId", probeId);
        Response response = repository.filter(builder.getQuery(), Strings.LAND);
        @SuppressWarnings("unchecked")
        List<Messung> messungen = (List<Messung>) response.getData();
        for (int i = 0; i < messungen.size(); i++) {
            if (messungen.get(i).getStatus() == null) {
                continue;
            }
            StatusProtokoll status = repository.getByIdPlain(
                StatusProtokoll.class, messungen.get(i).getStatus(), Strings.LAND);
            StatusKombi kombi = repository.getByIdPlain(
                StatusKombi.class, status.getStatusKombi(), Strings.STAMM);
            if (kombi.getStatusWert().getId() != 0 &&
                kombi.getStatusWert().getId() != 4) {
                return true;
            }
        }
        return false;
    }

    public boolean isMessungReadOnly(Integer messungId) {
        Authorizer a = authorizers.get(Messung.class);
        return a.isMessungReadOnly(messungId);
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
        if (authorizer == null) {
            return false;
        }
        return authorizer.isAuthorized(data, RequestMethod.GET, userInfo, clazz);
    }

    /**
     * Check whether a user is authorized to operate on the given probe.
     *
     * @param userInfo  The user information.
     * @param data      The probe data to test.
     * @return True if the user is authorized else returns false.
     */
    @Override
    public <T> boolean isAuthorizedOnNew(
        UserInfo userInfo,
        Object data,
        Class<T> clazz
    ) {
        Authorizer authorizer = authorizers.get(clazz);
        if (authorizer == null) {
            return false;
        }
        return authorizer.isAuthorized(data, RequestMethod.POST, userInfo, clazz);
    }
}
