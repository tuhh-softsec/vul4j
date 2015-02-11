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

import org.apache.log4j.Logger;

import de.intevation.lada.model.land.LProbe;
import de.intevation.lada.model.land.ProbeTranslation;
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


/**
 * This class produces a RESTful service to interact with probe objects.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("probe")
@RequestScoped
public class ProbeService {

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
     * Get all probe objects.
     *
     * @return Response object containing all probe objects.
     */
    @GET
    @Path("/")
    @Produces("application/json")
    public Response get(@Context HttpHeaders headers) {
        if (!authentication.isAuthenticated(headers)) {
            logger.debug("User is not authenticated!");
            return new Response(false, 699, null);
        }
        return defaultRepo.getAll(LProbe.class, "land");
    }

    /**
     * Get a probe object by id.
     *
     * @return Response object containing a single probe.
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
        return defaultRepo.getById(LProbe.class, Integer.valueOf(id), "land");
    }

    /**
     * Create a new probe object.
     *
     * @return Response object containing the new probe object.
     */
    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@Context HttpHeaders headers, LProbe probe) {
        if (!authentication.isAuthenticated(headers)) {
            return new Response(false, 699, null);
        }
        /* Persist the new probe object*/
        Response response = defaultRepo.create(probe, "land");
        LProbe ret = (LProbe)response.getData();
        /* Create and persist a new probe translation object*/
        ProbeTranslation trans = new ProbeTranslation();
        trans.setProbeId(ret);
        defaultRepo.create(trans, "land");
        /* Get and return the new probe object*/
        Response created =
            defaultRepo.getById(LProbe.class, ret.getId(), "land");
        return new Response(true, 200, created.getData());
    }

    /**
     * Update an existing probe object.
     *
     * @return Response object containing the updated probe object.
     */
    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@Context HttpHeaders headers, LProbe probe) {
        if (!authentication.isAuthenticated(headers)) {
            logger.debug("User is not authenticated!");
            return new Response(false, 699, null);
        }
        Response response = defaultRepo.update(probe, "land");
        Response updated = defaultRepo.getById(
            LProbe.class,
            ((LProbe)response.getData()).getId(), "land");
        return updated;
    }

    /**
     * Delete an existing probe object by id.
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
        /* Get the probe object by id*/
        Response probe =
            defaultRepo.getById(LProbe.class, Integer.valueOf(id), "land");
        LProbe probeObj = (LProbe)probe.getData();
        /* Create a query and request the probetranslation object for the
         * probe*/
        QueryBuilder<ProbeTranslation> builder =
            new QueryBuilder<ProbeTranslation>(
                defaultRepo.entityManager("land"), ProbeTranslation.class);
        builder.and("probe", probeObj.getId());
        Response probeTrans = defaultRepo.filter(builder.getQuery(), "land");
        @SuppressWarnings("unchecked")
        ProbeTranslation probeTransObj = ((List<ProbeTranslation>)probeTrans.getData()).get(0);
        /* Delete the probe translation object*/
        defaultRepo.delete(probeTransObj, "land");
        /* Delete the probe object*/
        Response response = defaultRepo.delete(probeObj, "land");
        return response;
    }
}
