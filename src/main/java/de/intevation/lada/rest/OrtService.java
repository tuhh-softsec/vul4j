package de.intevation.lada.rest;

import java.util.ArrayList;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import de.intevation.lada.auth.Authentication;
import de.intevation.lada.auth.AuthenticationException;
import de.intevation.lada.data.Repository;
import de.intevation.lada.model.Ort;

/**
* This class produces a RESTful service to read, write and update
* Ort objects.
*
* @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
*/
@Path("/ortinfo")
@RequestScoped
public class OrtService
{
    /**
     * The Repository for SUmwelt.
     */
    @Inject
    @Named("ortrepository")
    private Repository repository;

    /**
     * The authorization module.
     */
    @Inject
    @Named("ldapauth")
    private Authentication authentication;

    /**
     * Request a Ort object via its id.
     *
     * @param id        The Ort id
     * @param headers   The HTTP header containing authorization information.
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
                return repository.findById(Ort.class, id);
            }
            return new Response(false, 699, new ArrayList<Ort>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<Ort>());
        }
    }

    /**
     * Request all Ort objects
     *
     * @param headers   The HTTP header containing authorization information.
     * @return JSON Object via Rest service.
     */
    @GET
    @Produces("text/json")
    public Response filter(@Context HttpHeaders headers) {
        try {
            if (authentication.isAuthorizedUser(headers)) {
                return repository.findAll(Ort.class);
            }
            return new Response(false, 699, new ArrayList<Ort>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<Ort>());
        }
    }

    /**
     * Update a Ort object.
     *
     * @param ort       The Ort object to update.
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @PUT
    @Produces("text/json")
    @Path("/{ortId}")
    @Consumes("application/json")
    public Response update(Ort ort, @Context HttpHeaders headers) {
        try {
            if (authentication.isAuthorizedUser(headers)) {
                return repository.update(ort);
            }
            return new Response(false, 699, new ArrayList<Ort>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<Ort>());
        }
    }

    /**
     * Create a new Ort object.
     *
     * @param ort       The new Ort object.
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @POST
    @Produces("text/json")
    @Consumes("application/json")
    public Response create(Ort ort, @Context HttpHeaders headers) {
        try {
            if (authentication.isAuthorizedUser(headers)) {
                return repository.create(ort);
            }
            return new Response(false, 699, new ArrayList<Ort>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<Ort>());
        }
    }

    /**
     * Delete a Ort object.
     *
     * @param ortId     The object od.
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @DELETE
    @Path("/{ortId}/{probeId}")
    public Response delete(
        @PathParam("ortId") String ortId,
        @Context HttpHeaders headers
    ) {
        try {
            if (authentication.isAuthorizedUser(headers)) {
                Response response = repository.findById(Ort.class, ortId);
                Ort ort = (Ort)response.getData();
                if (ort != null) {
                    repository.delete(ort);
                    return new Response(true, 200, null);
                }
                return new Response(false, 600, null);
            }
            return new Response(false, 699, new ArrayList<Ort>());
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<Ort>());
        }
    }
}
