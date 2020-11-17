/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest.stamm;

import java.util.Arrays;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import de.intevation.lada.model.stammdaten.Auth;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.util.rest.Response;

/**
 * REST service for MessStellenKomi objects.
 * <p>
 * The services produce data in the application/json media type.
 * A typical response holds information about the action performed and the data.
 * <pre>
 * <code>
 * {
 *  "success": [boolean];
 *  "message": [string],
 *  "data":[{
 *      "netzbetreiberId": [string],
 *      "mstId": [string],
 *      "laborMstId": [string],
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
@Path("rest/messstellenkombi")
@RequestScoped
public class MessstellenkombiService {

    @Inject Logger logger;

    /**
     * The data repository granting read access.
     */
    @Inject
    @RepositoryConfig(type = RepositoryType.RO)
    private Repository defaultRepo;

    /**
     * Get all MessStellenKombi objects.
     * <p>
     * The requested objects can be filtered using a URL parameter named
     * netzbetreiberId.
     * <p>
      * Example: http://example.com/messstelle
     *
     * @return Response object containing all MessStelle objects.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(
        @Context HttpHeaders headers,
        @Context UriInfo info
    ) {
        MultivaluedMap<String, String> params = info.getQueryParameters();

        QueryBuilder<Auth> mstMlQuery = new QueryBuilder<Auth>(
            defaultRepo.entityManager(Strings.STAMM),
            Auth.class);
        mstMlQuery.orIntList("funktionId", Arrays.asList(0, 1));

        if (params.containsKey("netzbetreiberId")) {
            mstMlQuery.andIn(
                "netzbetreiberId",
                Arrays.asList(params.getFirst("netzbetreiberId").split(",")));
        }

        return defaultRepo.filter(mstMlQuery.getQuery(), Strings.STAMM);
   }
}
