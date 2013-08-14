package de.intevation.lada.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.auth.Authentication;
import de.intevation.lada.auth.AuthenticationException;
import de.intevation.lada.auth.Authorization;

/**
 * This class produces a RESTful service to get information about
 * access of probe objects.
 *
 * @author <a href="mailto:raimund.renkert@intevation.de">Raimund Renkert</a>
 */
@Path("/authinfo")
@RequestScoped
public class AuthInfoService
{
    /**
     * The authorization module.
     */
    @Inject
    @Named("ldapauth")
    private Authentication authentication;

    @Inject
    @Named("dataauthorization")
    private Authorization authorization;

    /**
     * Request information about access to probe objects
     *
     * @param info      The URL query parameters.
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @GET
    @Path("/{id}")
    @Produces("text/json")
    public Response get(
        @PathParam("id") String id,
        @Context HttpHeaders headers
    ) {
        try {
            if (!authentication.isAuthorizedUser(headers)) {
                return new Response(false, 699, null);
            }
            boolean isOwner = false;
            boolean isReadonly = true;
            if (authentication.hasAccess(headers, id)) {
                isOwner = true;
            }
            if (!authorization.isReadOnly(id)) {
                isReadonly = false;
            }
            Response response = new Response(
                true,
                200,
                "{isOwner: " + isOwner + ", readonly:" + isReadonly + "}");
            return response;
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, null);
        }
    }
}
