package de.intevation.lada.rest;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.faces.bean.RequestScoped;
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
import de.intevation.lada.data.Repository;
import de.intevation.lada.model.SDatenbasis;

/**
 * This class produces a RESTful service to read SDatenbasis objects.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("/datenbasis")
@RequestScoped
public class SDatenbasisService
{
    /**
     * The Repository for SDatenbasis.
     */
    @Inject
    @Named("readonlyrepository")
    private Repository repository;

    /**
     * The authorization module.
     */
    @Inject
    @Named("ldapauth")
    private Authentication authentication;

    /**
     * The logger for this class
     */
    @Inject
    private Logger logger;

    /**
     * Request all SDatenbasis objects.
     *
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @GET
    @Produces("text/json")
    public Response findAll(@Context HttpHeaders headers) {
        try {
            if (authentication.isAuthorizedUser(headers)) {
                return repository.findAll(SDatenbasis.class);
            }
            return new Response(false, 699, new ArrayList<SDatenbasis>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<SDatenbasis>());
        }
    }

    /**
     * Request a single SDatenbasis via its id.
     *
     * @param id        The object id.
     * @param header    The HTTP header containing authorization information.
     * @return Response object.
     */
    @GET
    @Path("/{id}")
    @Produces("text/json")
    public Response findById(
        @PathParam("id") String id,
        @Context HttpHeaders headers
    ) {
        try {
            if (authentication.isAuthorizedUser(headers)) {
                return repository.findById(SDatenbasis.class, id);
            }
            return new Response(false, 699, new ArrayList<SDatenbasis>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<SDatenbasis>());
        }
    }
}
