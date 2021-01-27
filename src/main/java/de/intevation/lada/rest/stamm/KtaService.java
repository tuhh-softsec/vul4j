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

import de.intevation.lada.model.stammdaten.Kta;
import de.intevation.lada.model.stammdaten.KtaGrpZuord;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.util.rest.Response;

/**
 * REST service for Kta objects.
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
 *      "bezeichnung": [string],
 *      "code": [string}
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
@Path("rest/kta")
@RequestScoped
public class KtaService {

    /**
     * The data repository granting read access.
     */
    @Inject
    @RepositoryConfig(type = RepositoryType.RO)
    private Repository repository;

    /**
     * Get all Kta objects.
     * <p>
     * Example: http://example.com/kta
     *
     * @return Response object containing all Kta objects.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
        @Context HttpHeaders headers,
        @Context UriInfo info
    ) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty() || !params.containsKey("ktagruppe")) {
            return repository.getAll(Kta.class, "stamm");
        }
        Integer id = null;
        try {
            id = Integer.valueOf(params.getFirst("ktagruppe"));
        } catch (NumberFormatException e) {
            return new Response(false, 603, "Not a valid filter id");
        }
        QueryBuilder<KtaGrpZuord> builder =
            new QueryBuilder<KtaGrpZuord>(
                repository.entityManager("stamm"),
                KtaGrpZuord.class
            );
        builder.and("ktaGrpId", id);
        List<KtaGrpZuord> zuord =
            repository.filterPlain(builder.getQuery(), "stamm");
        if (zuord.isEmpty()) {
            return new Response(true, 200, null);
        }
        QueryBuilder<Kta> builder1 =
            new QueryBuilder<Kta>(
                repository.entityManager("stamm"),
                Kta.class
            );
        List<Integer> ids = new ArrayList<Integer>();
        for (int i = 0; i < zuord.size(); i++) {
            ids.add(zuord.get(i).getKtaId());
        }
        builder1.orIn("id", ids);
        return repository.filter(builder1.getQuery(), "stamm");
    }

    /**
     * Get a single Kta object by id.
     * <p>
     * The id is appended to the URL as a path parameter.
     * <p>
     * Example: http://example.com/kta/{id}
     *
     * @return Response object containing a single Kta.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(
        @Context HttpHeaders headers,
        @PathParam("id") String id
    ) {
        return repository.getById(
            Kta.class,
            Integer.valueOf(id),
            Strings.STAMM);
    }
}
