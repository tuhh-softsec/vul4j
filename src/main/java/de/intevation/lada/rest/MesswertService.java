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

import de.intevation.lada.model.land.LMesswert;
import de.intevation.lada.util.annotation.AuthenticationConfig;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authentication;
import de.intevation.lada.util.auth.AuthenticationType;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.Response;

@Path("messwert")
@RequestScoped
public class MesswertService {

    /* The logger used in this class.*/
    @Inject
    private Logger logger;

    /* The data repository granting read/write access.*/
    @Inject
    @RepositoryConfig(type=RepositoryType.RW)
    private Repository defaultRepo;

    /* The authentication module.*/
    @Inject
    @AuthenticationConfig(type=AuthenticationType.NONE)
    private Authentication authentication;

    /* The authorization module.*/
    @Inject
    @AuthorizationConfig(type=AuthorizationType.NONE)
    private Authorization authorization;

    /**
     * Get all messung objects.
     *
     * @return Response object containing all messung objects.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
        @Context HttpHeaders headers,
        @Context UriInfo info
    ) {
        if (!authentication.isAuthenticated(headers)) {
            logger.debug("User is not authenticated!");
            return new Response(false, 699, null);
        }
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty() || !params.containsKey("messungId")) {
            logger.debug("get all");
            return defaultRepo.getAll(LMesswert.class, "land");
        }
        String messungId = params.getFirst("messungId");
        QueryBuilder<LMesswert> builder =
            new QueryBuilder<LMesswert>(
                defaultRepo.entityManager("land"),
                LMesswert.class);
        builder.and("messungsId", messungId);
        return defaultRepo.filter(builder.getQuery(), "land");
    }

    /**
     * Get a messung object by id.
     *
     * @return Response object containing a single messung.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(
        @Context HttpHeaders headers,
        @PathParam("id") String id
    ) {
        if (!authentication.isAuthenticated(headers)) {
            logger.debug("User is not authenticated!");
            return new Response(false, 699, null);
        }
        return defaultRepo.getById(LMesswert.class, Integer.valueOf(id), "land");
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
        @Context HttpHeaders headers,
        LMesswert messwert
    ) {
        if (!authentication.isAuthenticated(headers)) {
            return new Response(false, 699, null);
        }
        /* Persist the new messung object*/
        return defaultRepo.create(messwert, "land");
    }

    /**
     * Update an existing messung object.
     *
     * @return Response object containing the updated probe object.
     */
    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@Context HttpHeaders headers, LMesswert messwert) {
        if (!authentication.isAuthenticated(headers)) {
            logger.debug("User is not authenticated!");
            return new Response(false, 699, null);
        }
        Response response = defaultRepo.update(messwert, "land");
        Response updated = defaultRepo.getById(
            LMesswert.class,
            ((LMesswert)response.getData()).getId(), "land");
        return updated;
    }

    /**
     * Delete an existing messung object by id.
     *
     * @return Response object.
     */
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(
        @Context HttpHeaders headers,
        @PathParam("id") String id
    ) {
        if (!authentication.isAuthenticated(headers)) {
            logger.debug("User is not authenticated!");
            return new Response(false, 699, null);
        }
        /* Get the messwert object by id*/
        Response messwert =
            defaultRepo.getById(LMesswert.class, Integer.valueOf(id), "land");
        LMesswert messwertObj = (LMesswert)messwert.getData();
        /* Delete the messwert object*/
        return defaultRepo.delete(messwertObj, "land");
    }
}
