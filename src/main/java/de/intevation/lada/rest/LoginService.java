/* Copyright (C) 2015 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.auth.UserInfo;
import de.intevation.lada.util.rest.Response;

/**
 * REST service to get login data for the Lada application.
 * <p>
 * This service produces data in the application/json media type.
 * A typical response holds information about the action performed and the data.
 * <pre>
 * <code>
 * {
 *  "success": [boolean],
 *  "message": [string],
 *  "data":{
 *      "username": [string],
 *      "servertime": [timestamp],
 *      "roles": [string]
 *  },
 *  "errors": [object],
 *  "warnings": [object],
 *  "readonly": [boolean],
 *  "totalCount": [number]
 * }
 * </code>
 * </pre>
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("login")
@RequestScoped
public class LoginService {

    @Inject
    @AuthorizationConfig(type=AuthorizationType.OPEN_ID)
    private Authorization authorization;

    /**
     * Get login data.
     * <pre>
     * <code>
     * {
     *  "success": [boolean],
     *  "message": [string],
     *  "data": {
     *      "username": [string],
     *      "servertime": [timestamp],
     *      "roles": [string]
     *  },
     *  "errors": [object],
     *  "warnings": [object],
     *  "readonly": [boolean],
     *  "totalCount": [number]
     * }
     * </code>
     * </pre>
     *
     * @return Response object containing login data.
     */
    @GET
    @Path("/")
    @Produces("application/json")
    public Response get(
        @Context HttpHeaders headers,
        @Context UriInfo info,
        @Context HttpServletRequest request
    ) {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("username", request.getAttribute("lada.user.name"));
        response.put("roles", request.getAttribute("lada.user.roles"));
        response.put("servertime", new Date().getTime());
        UserInfo userInfo = authorization.getInfo(request);
        response.put("mst", userInfo.getMessstellen());
        response.put("netzbetreiber", userInfo.getNetzbetreiber());
        return new Response(true, 200, response);
    }
}
