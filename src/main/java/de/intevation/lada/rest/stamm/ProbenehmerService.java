/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest.stamm;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.model.stamm.Probenehmer;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.RequestMethod;
import de.intevation.lada.util.rest.Response;

/**
 * REST service for Probenehmer objects.
 * <p>
 * The services produce data in the application/json media type.
 * A typical response holds information about the action performed and the data.
 * <pre>
 * <code>
 * {
 *  "success": [boolean];
 *  "message": [string],
 *  "data":[{
 *      "id": [number],
 *      "mstId": [string],
 *      "netzbetreiberId": [string]
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
@Path("rest/probenehmer")
@RequestScoped
public class ProbenehmerService {

    /**
     * The data repository granting read access.
     */
    @Inject
    @RepositoryConfig(type=RepositoryType.RW)
    private Repository repository;

    @Inject
    @AuthorizationConfig(type=AuthorizationType.HEADER)
    private Authorization authorization;

    /**
     * Get all Probenehmer objects.
     * <p>
     * Example: http://example.com/probenehmer
     *
     * @return Response object containing all objects.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
        @Context HttpServletRequest request,
        @Context UriInfo info
    ) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        List<Probenehmer> nehmer =
            repository.getAllPlain(Probenehmer.class, "stamm");
        int size = nehmer.size();
        if (params.containsKey("start") && params.containsKey("limit")) {
            int start = Integer.valueOf(params.getFirst("start"));
            int limit = Integer.valueOf(params.getFirst("limit"));
            int end = limit + start;
            if (start + limit > nehmer.size()) {
                end = nehmer.size();
            }
            nehmer = nehmer.subList(start, end);
        }

        for (Probenehmer p : nehmer) {
            p.setReadonly(
                !authorization.isAuthorized(
                    request,
                    p,
                    RequestMethod.POST,
                    Probenehmer.class));
        }
        return new Response(true, 200, nehmer, size);
    }

    /**
     * Get a single Datenbasis object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/probenehmer/{id}
     *
     * @return Response object containing a single object.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(
        @Context HttpHeaders headers,
        @PathParam("id") String id
    ) {
        return repository.getById(
            Probenehmer.class,
            Integer.valueOf(id),
            "stamm");
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
        @Context HttpServletRequest request,
        Probenehmer probenehmer
    ) {
        if (!authorization.isAuthorized(
            request,
            probenehmer,
            RequestMethod.POST,
            Probenehmer.class)
        ) {
            return new Response(false, 699, probenehmer);
        }

        return repository.create(probenehmer, "stamm");
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
        @Context HttpServletRequest request,
        Probenehmer probenehmer
    ) {
        if (!authorization.isAuthorized(
            request,
            probenehmer,
            RequestMethod.PUT,
            Probenehmer.class)
        ) {
            return new Response(false, 699, probenehmer);
        }

        return repository.update(probenehmer, "stamm");
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(
        @Context HttpServletRequest request,
        @PathParam("id") String id
    ) {
        Probenehmer probenehmer = repository.getByIdPlain(
            Probenehmer.class, Integer.valueOf(id), "stamm");
        if (probenehmer == null ||
            !authorization.isAuthorized(
                request,
                probenehmer,
                RequestMethod.DELETE,
                Probenehmer.class
            )
        ) {
            return new Response(false, 699, null);
        }
        return repository.delete(probenehmer, "stamm");
    }
}
