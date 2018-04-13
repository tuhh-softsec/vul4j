/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest.stamm;

import java.io.StringReader;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
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

import de.intevation.lada.model.stammdaten.Filter;
import de.intevation.lada.model.stammdaten.Probenehmer;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;
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
        /*
        MultivaluedMap<String, String> params = info.getQueryParameters();
        List<Probenehmer> nehmer =
            repository.getAllPlain(Probenehmer.class, Strings.STAMM);
        if (params.containsKey("qid")) {
            Integer id = null;
            try {
                id = Integer.valueOf(params.getFirst("qid"));
            }
            catch (NumberFormatException e) {
                return new Response(false, 603, "Not a valid filter id");
            }
            QueryBuilder<Filter> fBuilder = new QueryBuilder<Filter>(
                repository.entityManager(Strings.STAMM),
                Filter.class
            );
            fBuilder.and("query", id);
            List<Filter> filters = repository.filterPlain(fBuilder.getQuery(), Strings.STAMM);
            QueryBuilder<Probenehmer> builder =
                new QueryBuilder<Probenehmer>(
                    repository.entityManager(Strings.STAMM),
                    Probenehmer.class
                );
            for (Filter filter: filters) {
                String param = params.get(filter.getDataIndex()).get(0);
                if (param == null || param.isEmpty()) {
                    continue;
                }
                if (filter.getMultiselect()) {
                    param = param.trim();
                    String[] parts = param.split(",");
                    for (String part: parts) {
                        builder.or(filter.getDataIndex(), part);
                    }
                }
                else {
                    builder.or(filter.getDataIndex(), param);
                }
            }

            if (params.containsKey("filter")) {
                JsonReader jsonReader = Json.createReader(
                    new StringReader(params.getFirst("filter")));
                JsonArray f = jsonReader.readArray();
                jsonReader.close();
                nehmer = repository.filterPlain(builder, f, Strings.STAMM);
            }
            else {
                nehmer = repository.filterPlain(builder.getQuery(), Strings.STAMM);
            }
        }

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
        */

        return new Response(true, 200, null, 0);
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
            Strings.STAMM);
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
        QueryBuilder<Probenehmer> builder =
            new QueryBuilder<Probenehmer>(
                repository.entityManager(Strings.STAMM),
                Probenehmer.class
            );
        builder.and("prnId", probenehmer.getPrnId());
        builder.and("netzbetreiberId", probenehmer.getNetzbetreiberId());

        List<Probenehmer> nehmer=
            repository.filterPlain(builder.getQuery(), Strings.STAMM);
        if (nehmer.isEmpty()) {
            return repository.create(probenehmer, Strings.STAMM);
        }
        return new Response(false, 672, null);
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
        @Context HttpServletRequest request,
        @PathParam("id") String id,
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

        return repository.update(probenehmer, Strings.STAMM);
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(
        @Context HttpServletRequest request,
        @PathParam("id") String id
    ) {
        Probenehmer probenehmer = repository.getByIdPlain(
            Probenehmer.class, Integer.valueOf(id), Strings.STAMM);
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
        return repository.delete(probenehmer, Strings.STAMM);
    }
}
