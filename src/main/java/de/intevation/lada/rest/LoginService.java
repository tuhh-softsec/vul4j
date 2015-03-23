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
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.util.rest.Response;

/**
 * This class serves as a login check service
 */
@Path("login")
@RequestScoped
public class LoginService {

    /**
     * Get all probe objects.
     *
     * @return Response object containing all probe objects.
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
        /* This should probably contain the users name and roles. */
        return new Response(true, 200, response);
    }
}
