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

import de.intevation.lada.lock.LockConfig;
import de.intevation.lada.lock.LockType;
import de.intevation.lada.lock.ObjectLocker;
import de.intevation.lada.model.land.LMessung;
import de.intevation.lada.model.land.MessungTranslation;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.RequestMethod;
import de.intevation.lada.util.rest.Response;

@Path("messung")
@RequestScoped
public class MessungService {

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
        if (params.isEmpty() || !params.containsKey("probeId")) {
            return defaultRepo.getAll(LMessung.class, "land");
        }
        String probeId = params.getFirst("probeId");
        QueryBuilder<LMessung> builder =
            new QueryBuilder<LMessung>(
                defaultRepo.entityManager("land"),
                LMessung.class);
        builder.and("probeId", probeId);
        return authorization.filter(
            request,
            defaultRepo.filter(builder.getQuery(), "land"),
            LMessung.class);
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
            defaultRepo.getById(LMessung.class, Integer.valueOf(id), "land"),
            LMessung.class);
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        LMessung messung
    ) {
        if (!authorization.isAuthorized(
                request,
                messung,
                RequestMethod.POST,
                LMessung.class)
        ) {
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
        return authorization.filter(
            request,
            new Response(true, 200, created.getData()),
            LMessung.class);
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
        LMessung messung
    ) {
        if (!authorization.isAuthorized(
                request,
                messung,
                RequestMethod.PUT,
                LMessung.class)
        ) {
            return new Response(false, 699, null);
        }
        if (lock.isLocked(messung)) {
            return new Response(false, 697, null);
        }
        messung.setLetzteAenderung(new Timestamp(new Date().getTime()));
        Response response = defaultRepo.update(messung, "land");
        Response updated = defaultRepo.getById(
            LMessung.class,
            ((LMessung)response.getData()).getId(), "land");
        return authorization.filter(
            request,
            updated,
            LMessung.class);
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
        /* Get the messung object by id*/
        Response messung =
            defaultRepo.getById(LMessung.class, Integer.valueOf(id), "land");
        LMessung messungObj = (LMessung)messung.getData();
        if (!authorization.isAuthorized(
                request,
                messungObj,
                RequestMethod.DELETE,
                LMessung.class)
        ) {
            return new Response(false, 699, null);
        }
        if (lock.isLocked(messung)) {
            return new Response(false, 697, null);
        }
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
