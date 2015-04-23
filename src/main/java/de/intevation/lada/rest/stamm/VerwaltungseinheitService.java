/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest.stamm;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.model.stamm.Verwaltungseinheit;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.Response;

/**
 * REST service for Verwaltungseinheit  objects.
 * <p>
 * The services produce data in the application/json media type.
 * A typical response holds information about the action performed and the data.
 * <pre>
 * <code>
 * {
 *  "success": [boolean];
 *  "message": [string],
 *  "data":[{
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
@Path("verwaltungseinheit")
@RequestScoped
public class VerwaltungseinheitService {

    /**
     * The data repository granting read access.
     */
    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository defaultRepo;

    /**
     * Get all Verwaltungseinheit objects.
     * <p>
     * The result list can be filtered using the URL parameter 'query'. A filter
     * is defined as the first letters of the 'bezeichnung'
     * <p>
     * Example: http://example.com/verwaltungseinheit?query=[string]
     *
     * @return Response object containing all Verwaltungseinheit objects.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
        @Context HttpHeaders headers,
        @Context UriInfo info
    ) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty() || !params.containsKey("query")) {
            return defaultRepo.getAll(Verwaltungseinheit.class, "stamm");
        }
        String filter = params.getFirst("query");
        QueryBuilder<Verwaltungseinheit> builder =
            new QueryBuilder<Verwaltungseinheit>(
                defaultRepo.entityManager("stamm"), Verwaltungseinheit.class);
        builder.andLike("bezeichnung", filter + "%");
        return defaultRepo.filter(builder.getQuery(), "stamm");
    }

    /**
     * Get a single Verwaltungseinheit object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/verwaltungseinheit/{id}
     *
     * @return Response object containing a single Verwaltungseinheit.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(
        @Context HttpHeaders headers,
        @PathParam("id") String id
    ) {
        return defaultRepo.getById(
            Verwaltungseinheit.class,
            id,
            "stamm");
    }
}
