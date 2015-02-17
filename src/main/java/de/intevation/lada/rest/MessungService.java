/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3) 
 * and comes with ABSOLUTELY NO WARRANTY! Check out 
 * the documentation coming with IMIS-Labordaten-Application for details. 
 */
package de.intevation.lada.rest;

import java.util.List;

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

import de.intevation.lada.model.land.LMessung;
import de.intevation.lada.model.land.MessungTranslation;
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

@Path("messung")
@RequestScoped
public class MessungService {

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
        if (params.isEmpty() || !params.containsKey("probeId")) {
            return defaultRepo.getAll(LMessung.class, "land");
        }
        String probeId = params.getFirst("probeId");
        QueryBuilder<LMessung> builder =
            new QueryBuilder<LMessung>(
                defaultRepo.entityManager("land"),
                LMessung.class);
        builder.and("probeId", probeId);
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
        return defaultRepo.getById(LMessung.class, Integer.valueOf(id), "land");
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
        @Context HttpHeaders headers,
        LMessung messung
    ) {
        if (!authentication.isAuthenticated(headers)) {
            return new Response(false, 699, null);
        }
        /* Persist the new messung object*/
        Response response = defaultRepo.create(messung, "land");
        LMessung ret = (LMessung)response.getData();
        /* Create and persist a new probe translation object*/
        MessungTranslation trans = new MessungTranslation();
        trans.setMessungsId(ret);
        defaultRepo.create(trans, "land");
        /* Get and return the new probe object*/
        Response created =
            defaultRepo.getById(LMessung.class, ret.getId(), "land");
        return new Response(true, 200, created.getData());
    }

    /**
     * Update an existing messung object.
     *
     * @return Response object containing the updated probe object.
     */
    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@Context HttpHeaders headers, LMessung messung) {
        if (!authentication.isAuthenticated(headers)) {
            logger.debug("User is not authenticated!");
            return new Response(false, 699, null);
        }
        Response response = defaultRepo.update(messung, "land");
        Response updated = defaultRepo.getById(
            LMessung.class,
            ((LMessung)response.getData()).getId(), "land");
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
        /* Get the messung object by id*/
        Response messung =
            defaultRepo.getById(LMessung.class, Integer.valueOf(id), "land");
        LMessung messungObj = (LMessung)messung.getData();
        /* Create a query and request the messungTranslation object for the
         * messung*/
        QueryBuilder<MessungTranslation> builder =
            new QueryBuilder<MessungTranslation>(
                defaultRepo.entityManager("land"), MessungTranslation.class);
        builder.and("messungs", messungObj.getId());
        Response messungTrans = defaultRepo.filter(builder.getQuery(), "land");
        @SuppressWarnings("unchecked")
        MessungTranslation messungTransObj = ((List<MessungTranslation>)messungTrans.getData()).get(0);
        /* Delete the messung translation object*/
        defaultRepo.delete(messungTransObj, "land");
        /* Delete the probe object*/
        Response response = defaultRepo.delete(messungObj, "land");
        return response;
    }
}
