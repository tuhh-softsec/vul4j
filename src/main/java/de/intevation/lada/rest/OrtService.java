/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3) 
 * and comes with ABSOLUTELY NO WARRANTY! Check out 
 * the documentation coming with IMIS-Labordaten-Application for details. 
 */
package de.intevation.lada.rest;

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

import org.apache.log4j.Logger;

import de.intevation.lada.model.land.LOrt;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.RequestMethod;
import de.intevation.lada.util.rest.Response;

@Path("ort")
@RequestScoped
public class OrtService {

    /* The logger used in this class.*/
    @Inject
    private Logger logger;

    /* The data repository granting read/write access.*/
    @Inject
    @RepositoryConfig(type=RepositoryType.RW)
    private Repository defaultRepo;

    /* The authorization module.*/
    @Inject
    @AuthorizationConfig(type=AuthorizationType.OPEN_ID)
    private Authorization authorization;

    /**
     * Get all objects.
     *
     * @return Response object containing all messung objects.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
        @Context HttpHeaders headers,
        @Context UriInfo info,
        @Context HttpServletRequest request
    ) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty() || !params.containsKey("probeId")) {
            logger.debug("get all");
            return defaultRepo.getAll(LOrt.class, "land");
        }
        String probeId = params.getFirst("probeId");
        QueryBuilder<LOrt> builder =
            new QueryBuilder<LOrt>(
                defaultRepo.entityManager("land"),
                LOrt.class);
        builder.and("probeId", probeId);
        return authorization.filter(
            request,
            defaultRepo.filter(builder.getQuery(), "land"),
            LOrt.class);
    }

    /**
     * Get an object by id.
     *
     * @return Response object containing a single messung.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        @PathParam("id") String id
    ) {
        return authorization.filter(
            request,
            defaultRepo.getById(LOrt.class, Integer.valueOf(id), "land"),
            LOrt.class);
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        LOrt ort
    ) {
        if (!authorization.isAuthorized(
                request,
                ort,
                RequestMethod.POST,
                LOrt.class)) {
            return new Response(false, 699, null);
        }
        /* Persist the new object*/
        return defaultRepo.create(ort, "land");
    }

    /**
     * Update an existing object.
     *
     * @return Response object containing the updated probe object.
     */
    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        LOrt ort
    ) {
        if (!authorization.isAuthorized(
                request,
                ort,
                RequestMethod.PUT,
                LOrt.class)) {
            return new Response(false, 699, null);
        }
        Response response = defaultRepo.update(ort, "land");
        Response updated = defaultRepo.getById(
            LOrt.class,
            ((LOrt)response.getData()).getId(), "land");
        return updated;
    }

    /**
     * Delete an existing object by id.
     *
     * @return Response object.
     */
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        @PathParam("id") String id
    ) {
        /* Get the messwert object by id*/
        Response object =
            defaultRepo.getById(LOrt.class, Integer.valueOf(id), "land");
        LOrt ortObj = (LOrt)object.getData();
        if (!authorization.isAuthorized(
                request,
                ortObj,
                RequestMethod.PUT,
                LOrt.class)) {
            return new Response(false, 699, null);
        }
        /* Delete the messwert object*/
        return defaultRepo.delete(ortObj, "land");
    }
}
