/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest.stamm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.model.stamm.Deskriptoren;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.Response;

/**
 * REST service for Probe objects.
 * <p>
 * The services produce data in the application/json media type.
 * All HTTP methods use the authorization module to determine if the user is
 * allowed to perform the requested action.
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
@Path("deskriptor")
@RequestScoped
public class DeskriptorService {

    /**
     * The data repository granting read/write access.
     */
    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    /**
     * Get all Deskriptor objects.
     * <p>
     * The requested objects can be filtered using the following URL
     * parameters:<br>
     *  <br>
     *  The response data contains a stripped set of Probe objects. The returned fields
     *  are defined in the query used in the request.
     * <p>
     * Example:
     * http://example.com/deskriptor?layer=[LAYER]
     *
     * @return Response object containing all Deskriptor objects.
     */
    @GET
    @Path("/")
    @Produces("application/json")
    public Response get(
        @Context HttpHeaders headers,
        @Context UriInfo info,
        @Context HttpServletRequest request
    ) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty() ||
            !params.containsKey("layer")) {
            return repository.getAll(Deskriptoren.class, "stamm");
        }
        QueryBuilder<Deskriptoren> builder = new QueryBuilder<Deskriptoren>(
            repository.entityManager("stamm"),
            Deskriptoren.class);
        builder.andNot("sn", 0);
        if (params.containsKey("layer") &&
            !params.containsKey("parents")) {
            String layer = params.getFirst("layer");
            builder.and("ebene", layer);
        }
        else {
            String layer = params.getFirst("layer");
            String parents = params.getFirst("parents");
            builder.and("ebene", layer);
            List<String> parentList = new ArrayList<String>();
            String[] parentArray = parents.split(", ");
            parentList = Arrays.asList(parentArray);
            builder.andIn("vorgaenger", parentList);
        }
        return repository.filter(builder.getQuery(), "stamm");
    }

    /**
     * Get a single Deskriptor object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/deskriptor/{id}
     *
     * @return Response object containing a single Deskriptor.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(
        @Context HttpHeaders headers,
        @PathParam("id") String id,
        @Context HttpServletRequest request
    ) {
        return repository.getById(Deskriptoren.class, Integer.valueOf(id), "stamm");
    }
}
