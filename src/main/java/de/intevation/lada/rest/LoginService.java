/* Copyright (C) 2015 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3) 
 * and comes with ABSOLUTELY NO WARRANTY! Check out 
 * the documentation coming with IMIS-Labordaten-Application for details. 
 */

import javax.enterprise.context.RequestScoped;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;

import de.intevation.lada.util.rest.Response;
/**
 * This class serves as a login check service
 */
@Path("login")
@RequestScoped
public class LoginService {

    /* The logger used in this class.*/
    @Inject
    private Logger logger;

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
        /* This should probably contain the users name and roles. */
        return new Response(true, 200, "Success");
    }
}
