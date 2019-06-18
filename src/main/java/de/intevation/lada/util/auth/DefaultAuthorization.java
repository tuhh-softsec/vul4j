/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.util.auth;

import javax.ejb.Stateless;

import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.rest.RequestMethod;
import de.intevation.lada.util.rest.Response;

/**
 * Default implementation of the authorization.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Stateless
@AuthorizationConfig(type=AuthorizationType.NONE)
public class DefaultAuthorization implements Authorization {

    /**
     * Get the user infomation.
     *
     * @return An empty UserInfo object.
     */
    @Override
    public UserInfo getInfo(Object source) {
        return new UserInfo();
    }

    /**
     * Filter the data embedded in 'data'.
     *
     * @param   source  The HTTP request.
     * @param   data    The response data object.
     * @param   clazz   The type of the embedded data.
     *
     * @return Unfiltered response data object.
     */
    @Override
    public <T> Response filter(Object source, Response data, Class<T> clazz) {
        return data;
    }

    /**
     * Get the authorization status.
     *
     * @param   source  The HTTP request
     * @param   data    The requested data
     * @param   method  The HTTP request method
     * @param   clazz   The type of the embedded data.
     *
     * @return true
     */
    @Override
    public <T> boolean isAuthorized(
        Object source, Object data, RequestMethod method, Class<T> clazz) {
        return true;
    }

    /**
     * Get the R/W status of a probe object by id.
     *
     * @param   probeId The probe id.
     *
     * @return false
     */
    @Override
    public boolean isReadOnly(Integer probeId) {
        return false;
    }

    /**
     * Get the authorization status.
     *
     * @param   userInfo    The user information
     * @param   data        The response data object.
     *
     * @return true
     */
    @Override
    public <T> boolean isAuthorized(
        UserInfo userInfo,
        Object data,
        Class<T> clazz) {
        return true;
    }

    @Override
    public <T> boolean isAuthorizedById(Object source, Object id, RequestMethod method, Class<T> clazz) {
        return true;
    }

    /**
     * Get the authorization status.
     *
     * @param   userInfo    The user information
     * @param   data        The response data object.
     *
     * @return true
     */
    @Override
    public <T> boolean isAuthorizedOnNew(
        UserInfo userInfo,
        Object data,
        Class<T> clazz) {
        return true;
    }

    @Override
    public boolean isMessungReadOnly(Integer messungId) {
        return true;
    }
}
