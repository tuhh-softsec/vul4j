package de.intevation.lada.rest;

import java.util.List;

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
import de.intevation.lada.auth.AuthenticationResponse;
import de.intevation.lada.data.QueryBuilder;
import de.intevation.lada.data.Repository;
import de.intevation.lada.model.SNetzBetreiber;

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
 * This class produces a RESTful service to read information about the user and
 * server
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

    @Inject
    @Named("readonlyrepository")
    private Repository readonlyRepo;

    /**
     * Request User and Server information.
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
            String user = authentication.getUserName(headers);
            AuthenticationResponse ar = authentication.authorizedGroups(headers);
            List<String> groups = ar.getNetzbetreiber();
            QueryBuilder<SNetzBetreiber> builder =
                new QueryBuilder<SNetzBetreiber>(
                    readonlyRepo.getEntityManager(), SNetzBetreiber.class);
            for (String g : groups) {
                builder.or("netzbetreiberId", g);
            }
            Response nResponse = readonlyRepo.filter(builder.getQuery());
            List<SNetzBetreiber> netzbetr = (List<SNetzBetreiber>)nResponse.getData();
            String gString = "";
            boolean first = true;
            for(SNetzBetreiber nb : netzbetr) {
                if (first) {
                    gString += nb.getNetzbetreiber();
                    first = false;
                }
                else {
                    gString += ", " + nb.getNetzbetreiber();
                }
            }
            //TODO: This is the best way to get the version.
            //  Should read the version from MANIFEST.MF but does not work (returns null).
            //String version = getClass().getPackage().getImplementationVersion();
            String version = System.getProperty("de.intevation.lada.server.version");

            Response response = new Response(true, 200, new Info(user, gString, version));
            return response;
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, null);
        }
    }
}
