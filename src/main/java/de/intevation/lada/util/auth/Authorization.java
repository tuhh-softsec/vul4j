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

/**
 * Interface for authorization in the lada application.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public interface Authorization {
    public UserInfo getInfo(Object source);
    public <T> Response filter(Object source, Response data, Class<T> clazz);
    public <T> boolean isAuthorized(
        Object source, Object data, RequestMethod method, Class<T> clazz);
    public <T> boolean isAuthorizedById(
        Object source, Object id, RequestMethod method, Class<T> clazz);
    public <T> boolean isAuthorized(UserInfo userInfo, Object data, Class<T> clazz);
    public <T> boolean isAuthorizedOnNew(UserInfo userInfo, Object data, Class<T> clazz);
    boolean isReadOnly(Integer probeId);
    boolean isMessungReadOnly(Integer messungId);
}
