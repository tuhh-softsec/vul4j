/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest.stamm;

import java.util.ArrayList;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.model.stamm.NetzBetreiber;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.auth.UserInfo;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.Response;

/**
 * REST service for NetzBetreiber objects.
 * <p>
 * The services produce data in the application/json media type.
 * A typical response holds information about the action performed and the data.
 * <pre>
 * <code>
 * {
 *  "success": [boolean];
 *  "message": [string],
 *  "data":[{
 *      "id": [string],
 *      "aktiv": [boolean],
 *      "idfNetzbetreiber": [string],
 *      "isBmn": [boolean],
 *      "mailverteiler": [string],
 *      "netzbetreiber": [string],
 *      "zustMstId": [string]
 *  }],
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
@Path("netzbetreiber")
@RequestScoped
public class NetzbetreiberService {

    /**
     * The data repository granting read access.
     */
    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository defaultRepo;

    /**
     * The authorization module.
     */
    @Inject
    @AuthorizationConfig(type=AuthorizationType.OPEN_ID)
    private Authorization authorization;

    /**
     * Get all NetzBetreiber objects.
     * <p>
     * Example: http://example.com/netzbetreiber
     *
     * @return Response object containing all NetzBetreiber objects.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        @Context UriInfo info
    ) {
        UserInfo userInfo = authorization.getInfo(request);
        QueryBuilder<NetzBetreiber> builder =
            new QueryBuilder<NetzBetreiber>(
                defaultRepo.entityManager("stamm"), NetzBetreiber.class);
        builder.or("id", userInfo.getNetzbetreiber());
        return defaultRepo.filter(builder.getQuery(), "stamm");
    }

    /**
     * Get a single NetzBetreiber object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/netzbetreiber/{id}
     *
     * @return Response object containing a single NetzBetreiber.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        @PathParam("id") String id
    ) {
        UserInfo userInfo = authorization.getInfo(request);
        if (userInfo.getNetzbetreiber().contains(id)) {
            return defaultRepo.getById(NetzBetreiber.class, id, "stamm");
        }
        return new Response(false, 698, new ArrayList<NetzBetreiber>());
    }
}
