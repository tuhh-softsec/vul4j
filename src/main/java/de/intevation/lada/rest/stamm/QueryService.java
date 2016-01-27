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
import javax.ws.rs.Produces;

import de.intevation.lada.model.stamm.Query;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.Response;


/**
 * REST-Service for preconfigured queries.
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
 *      "id": [string],
 *      "name": [string],
 *      "description": [string],
 *      "sql": [string],
 *      "filters": [array],
 *      "results": [array]
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
@Path("rest/query")
@RequestScoped
public class QueryService {

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    /**
     * Request all configured probe queries.
     */
    @GET
    @Path("/probe")
    @Produces("application/json")
    public Response getProbe() {
        QueryBuilder<Query> builder = new QueryBuilder<Query>(
            repository.entityManager("stamm"),
            Query.class
        );
        builder.and("type", "probe");
        return repository.filter(builder.getQuery(), "stamm");
        //return new Response(true, 200, QueryTools.getProbeConfig());
    }

    /**
     * Request all configured messprogramm queries.
     */
    @GET
    @Path("/messprogramm")
    @Produces("application/json")
    public Response getMessprogramm() {
        QueryBuilder<Query> builder = new QueryBuilder<Query>(
            repository.entityManager("stamm"),
            Query.class
        );
        builder.and("type", "messprogramm");
        return repository.filter(builder.getQuery(), "stamm");
    }

    /**
     * Request all configured stammdaten queries.
     */
    @GET
    @Path("/stammdaten")
    @Produces("application/json")
    public Response getStammdaten() {
        QueryBuilder<Query> builder = new QueryBuilder<Query>(
            repository.entityManager("stamm"),
            Query.class
        );
        builder.or("type", "ort");
        builder.or("type", "probenehmer");
        builder.or("type", "datensatzerzeuger");
        builder.or("type", "messprogrammkategorie");
        return repository.filter(builder.getQuery(), "stamm");
    }
}
