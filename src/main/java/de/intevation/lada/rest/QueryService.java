/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import de.intevation.lada.query.QueryTools;
import de.intevation.lada.util.rest.Response;


/**
 * REST-Service for preconfigured queries.
 */
@Path("/query")
@RequestScoped
public class QueryService {

    /**
     * Request all configured queries.
     */
    @GET
    @Produces("text/json")
    public Response get() {
        return new Response(true, 200, QueryTools.getConfig());
    }
}
