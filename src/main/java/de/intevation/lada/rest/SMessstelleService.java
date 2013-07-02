package de.intevation.lada.rest;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import de.intevation.lada.authentication.Authentication;
import de.intevation.lada.authentication.AuthenticationException;
import de.intevation.lada.authentication.AuthenticationResponse;
import de.intevation.lada.data.QueryBuilder;
import de.intevation.lada.data.Repository;
import de.intevation.lada.model.SMessStelle;

/**
 * This class produces a RESTful service to read the contents of s_messstelle
 * table.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("/mst")
@RequestScoped
public class SMessstelleService
{
    /**
     * The Repository for SMessStelle.
     */
    @Inject
    @Named("readonlyrepository")
    private Repository repository;

    @Inject
    @Named("ldapauth")
    private Authentication authentication;

    /**
     * The logger for this class
     */
    @Inject
    private Logger logger;

    /**
     * Request all SMessStelle objects.
     *
     * @return JSON Object via Rest service
     */
    @GET
    @Produces("text/json")
    public Response findAll(@Context HttpHeaders headers) {
        try {
            AuthenticationResponse auth =
                authentication.authorizedGroups(headers);
            QueryBuilder<SMessStelle> builder =
                new QueryBuilder<SMessStelle>(
                    repository.getEntityManager(), SMessStelle.class);
            builder.or("mstId", auth.getMst());
            return repository.filter(builder.getQuery());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<SMessStelle>());
        }
    }

    /**
     * Request a single SMessStelle via its id.
     *
     * @param id The mst_id
     * @return JSON Object via REST service.
     */
    @GET
    @Path("/{id}")
    @Produces("text/json")
    public Response findById(
        @PathParam("id") String id,
        @Context HttpHeaders headers) {
        try {
            AuthenticationResponse auth =
                authentication.authorizedGroups(headers);
            if (auth.getMst().contains(id)) {
                return repository.findById(SMessStelle.class, id);
            }
            return new Response(false, 698, new ArrayList<SMessStelle>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<SMessStelle>());
        }
    }
}
