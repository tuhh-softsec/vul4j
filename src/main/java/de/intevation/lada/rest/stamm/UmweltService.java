/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest.stamm;

import java.util.ArrayList;
import java.util.List;

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

import de.intevation.lada.model.stammdaten.ReiProgpunktGrpUmwZuord;
import de.intevation.lada.model.stammdaten.Umwelt;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.StatusCodes;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.util.rest.Response;

/**
 * REST service for Umwelt objects.
 * <p>
 * The services produce data in the application/json media type.
 * A typical response holds information about the action performed and the data.
 * <pre>
 * <code>
 * {
 *  "success": [boolean];
 *  "message": [string],
 *  "data":[{
 *      "id": [string],
 *      "beschreibung": [string],
 *      "umweltBereich": [string],
 *      "mehId": [number]
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
@Path("rest/umwelt")
@RequestScoped
public class UmweltService {

    /**
     * The data repository granting read access.
     */
    @Inject
    @RepositoryConfig(type = RepositoryType.RO)
    private Repository repository;

    /**
     * Get all Umwelt objects.
     * <p>
     * Example: http://example.com/umwelt
     *
     * @return Response object containing all Umwelt objects.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
        @Context HttpHeaders headers,
        @Context UriInfo info
    ) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty() || !params.containsKey("reiprogpunktgruppe")) {
            return repository.getAll(Umwelt.class, "stamm");
        }
        Integer id = null;
        try {
            id = Integer.valueOf(params.getFirst("reiprogpunktgruppe"));
        } catch (NumberFormatException e) {
            return new Response(
                false,
                StatusCodes.ERROR_DB_CONNECTION,
                "Not a valid filter id");
        }
        QueryBuilder<ReiProgpunktGrpUmwZuord> builder =
            new QueryBuilder<ReiProgpunktGrpUmwZuord>(
                repository.entityManager("stamm"),
                ReiProgpunktGrpUmwZuord.class
            );
        builder.and("reiProgpunktGrpId", id);
        List<ReiProgpunktGrpUmwZuord> zuord =
            repository.filterPlain(builder.getQuery(), "stamm");
        if (zuord.isEmpty()) {
            return new Response(true, StatusCodes.OK, null);
        }
        QueryBuilder<Umwelt> builder1 =
            new QueryBuilder<Umwelt>(
                repository.entityManager("stamm"),
                Umwelt.class
            );
        List<String> ids = new ArrayList<String>();
        for (int i = 0; i < zuord.size(); i++) {
            ids.add(zuord.get(i).getUmwId());
        }
        builder1.orIn("id", ids);
        return repository.filter(builder1.getQuery(), "stamm");
    }

    /**
     * Get a single Umwelt object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/umwelt/{id}
     *
     * @return Response object containing a single Umwelt.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(
        @Context HttpHeaders headers,
        @PathParam("id") String id
    ) {
        return repository.getById(
            Umwelt.class,
            id,
            Strings.STAMM);
    }
}
