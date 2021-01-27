/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.util.auth;

import de.intevation.lada.util.rest.RequestMethod;
import de.intevation.lada.util.rest.Response;

public interface Authorizer {

    <T> boolean isAuthorized(
        Object data,
        RequestMethod method,
        UserInfo userInfo,
        Class<T> clazz);

    <T> boolean isAuthorizedById(
        Object id,
        RequestMethod method,
        UserInfo userInfo,
        Class<T> clazz);

    <T> Response filter(
        Response data,
        UserInfo userInfo,
        Class<T> clazz);

    boolean isProbeReadOnly(Integer probeId);

    boolean isMessungReadOnly(Integer messungsId);

}
