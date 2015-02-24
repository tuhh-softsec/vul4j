/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3) 
 * and comes with ABSOLUTELY NO WARRANTY! Check out 
 * the documentation coming with IMIS-Labordaten-Application for details. 
 */
package de.intevation.lada.rest;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.persistence.Query;
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

import de.intevation.lada.model.land.LProbe;
import de.intevation.lada.model.land.ProbeTranslation;
import de.intevation.lada.query.QueryTools;
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
import de.intevation.lada.validation.Validator;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationConfig;


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

    @Inject
    @ValidationConfig(type="Probe")
    private Validator validator;

    /**
     * Get all probe objects.
     *
     * @return Response object containing all probe objects.
     */
    @SuppressWarnings("unchecked")
    @GET
    @Path("/")
    @Produces("application/json")
    public Response get(
        @Context HttpHeaders headers,
        @Context UriInfo info
    ) {
        if (!authentication.isAuthenticated(headers)) {
            logger.debug("User is not authenticated!");
            return new Response(false, 699, null);
        }
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty() || !params.containsKey("qid")) {
            return defaultRepo.getAll(LProbe.class, "land");
        }
        String qid = params.getFirst("qid");
        JsonObject jsonQuery = QueryTools.getQueryById(qid);
        String sql = "";
        List<String> filters = new ArrayList<String>();
        List<String> results = new ArrayList<String>();
        try {
            sql = jsonQuery.getString("sql");
            JsonArray jsonFilters = jsonQuery.getJsonArray("filters");
            JsonArray jsonResults = jsonQuery.getJsonArray("result");
            for (int i = 0; i < jsonFilters.size(); i++) {
                filters.add(
                    jsonFilters.getJsonObject(i).getString("dataIndex"));
            }
            for (int i = 0; i < jsonResults.size(); i++) {
                results.add(
                    jsonResults.getJsonObject(i).getString("dataIndex"));
            }
        }
        catch (JsonException je) {
            return new Response(false, 603, new ArrayList<Object>());
        }
        Query query = QueryTools.prepareQuery(
            sql,
            filters,
            params,
            defaultRepo.entityManager("land"));
        return new Response(true, 200, QueryTools.prepareResult(query.getResultList(), results));
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
        Response response =
            defaultRepo.getById(LProbe.class, Integer.valueOf(id), "land");
        Violation violation = validator.validate(response.getData());
        if (violation.hasWarnings()) {
            response.setWarnings(violation.getWarnings());
        }
        return response;
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
        Violation violation = validator.validate(probe);
        if (violation.hasErrors()) {
            Response response = new Response(false, 604, probe);
            response.setErrors(violation.getErrors());
            response.setWarnings(violation.getWarnings());
            return response;
        }
        /* Persist the new probe object*/
        Response newProbe = defaultRepo.create(probe, "land");
        LProbe ret = (LProbe)newProbe.getData();
        /* Create and persist a new probe translation object*/
        ProbeTranslation trans = new ProbeTranslation();
        trans.setProbeId(ret);
        defaultRepo.create(trans, "land");
        /* Get and return the new probe object*/
        Response response =
            defaultRepo.getById(LProbe.class, ret.getId(), "land");
        if(violation.hasWarnings()) {
            response.setWarnings(violation.getWarnings());
        }
        return response;
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
        Violation violation = validator.validate(probe);
        if (violation.hasErrors()) {
            Response response = new Response(false, 604, probe);
            response.setErrors(violation.getErrors());
            response.setWarnings(violation.getWarnings());
            return response;
        }
        Response response = defaultRepo.update(probe, "land");
        Response updated = defaultRepo.getById(
            LProbe.class,
            ((LProbe)response.getData()).getId(), "land");
        if (violation.hasWarnings()) {
            updated.setWarnings(violation.getWarnings());
        }
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
