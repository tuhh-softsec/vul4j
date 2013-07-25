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

class Info {
    String user;
    String groups;
    String version;
    public Info(String user, String groups, String version) {
        this.user = user;
        this.groups = groups;
        this.version = version;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getGroups() {
        return groups;
    }
    public void setGroups(String groups) {
        this.groups = groups;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
}

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
            Response response = new Response(true, 200, new Info("-/-", "-/-", "-/-"));
            return response;
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, null);
        }
    }
}
