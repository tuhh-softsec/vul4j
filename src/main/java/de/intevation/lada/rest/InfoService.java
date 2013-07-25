package de.intevation.lada.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.auth.Authentication;
import de.intevation.lada.auth.AuthenticationException;

/**
 * This class produces a RESTful service to read, write and update
 * LOrt objects.
 *
 * @author <a href="mailto:torsten.irlaender@intevation.de">Torsten Irländer</a>
 */
@Path("/info")
@RequestScoped
public class InfoService
{
    /**
     * The authorization module.
     */
    @Inject
    @Named("ldapauth")
    private Authentication authentication;

    /**
     * Request SQL-Queries
     *
     * Query parameters are used for the filter in form of key-value pairs.
     *
     * @param info      The URL query parameters.
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @GET
    @Produces("text/json")
    public Response filter(
        @Context UriInfo info,
        @Context HttpHeaders headers
    ) {
        try {
            if (!authentication.isAuthorizedUser(headers)) {
                return new Response(false, 699, null);
            }
            Response response = new Response(true, 200, null);
            return response;
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, null);
        }
    }
}
