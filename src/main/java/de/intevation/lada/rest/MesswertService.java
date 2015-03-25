/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3) 
 * and comes with ABSOLUTELY NO WARRANTY! Check out 
 * the documentation coming with IMIS-Labordaten-Application for details. 
 */
package de.intevation.lada.rest;

import java.sql.Timestamp;
import java.util.Date;

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

import de.intevation.lada.lock.LockConfig;
import de.intevation.lada.lock.LockType;
import de.intevation.lada.lock.ObjectLocker;
import de.intevation.lada.model.land.LMesswert;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.RequestMethod;
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

    @Inject
    @LockConfig(type=LockType.TIMESTAMP)
    private ObjectLocker lock;

    /* The authorization module.*/
    @Inject
    @AuthorizationConfig(type=AuthorizationType.OPEN_ID)
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
        @Context UriInfo info,
        @Context HttpServletRequest request
    ) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty() || !params.containsKey("messungsId")) {
            logger.debug("get all");
            return defaultRepo.getAll(LMesswert.class, "land");
        }
        String messungId = params.getFirst("messungsId");
        QueryBuilder<LMesswert> builder =
            new QueryBuilder<LMesswert>(
                defaultRepo.entityManager("land"),
                LMesswert.class);
        builder.and("messungsId", messungId);
        return authorization.filter(
            request,
            defaultRepo.filter(builder.getQuery(), "land"),
            LMesswert.class);
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
        @Context HttpServletRequest request,
        @PathParam("id") String id
    ) {
        return authorization.filter(
            request,
            defaultRepo.getById(LMesswert.class, Integer.valueOf(id), "land"),
            LMesswert.class);
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        LMesswert messwert
    ) {
        if (!authorization.isAuthorized(
                request,
                messwert,
                RequestMethod.POST,
                LMesswert.class)
        ) {
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
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        LMesswert messwert
    ) {
        if (!authorization.isAuthorized(
                request,
                messwert,
                RequestMethod.PUT,
                LMesswert.class)
        ) {
            return new Response(false, 699, null);
        }
        if (lock.isLocked(messwert)) {
            return new Response(false, 697, null);
        }
        messwert.setLetzteAenderung(new Timestamp(new Date().getTime()));
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
        @Context HttpServletRequest request,
        @PathParam("id") String id
    ) {
        /* Get the messwert object by id*/
        Response messwert =
            defaultRepo.getById(LMesswert.class, Integer.valueOf(id), "land");
        LMesswert messwertObj = (LMesswert)messwert.getData();
        if (!authorization.isAuthorized(
                request,
                messwertObj,
                RequestMethod.DELETE,
                LMesswert.class)
        ) {
            return new Response(false, 699, null);
        }
        if (lock.isLocked(messwert)) {
            return new Response(false, 697, null);
        }
        /* Delete the messwert object*/
        return defaultRepo.delete(messwertObj, "land");
    }
}
