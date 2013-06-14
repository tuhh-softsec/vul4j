package de.intevation.lada.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import de.intevation.lada.data.Repository;
import de.intevation.lada.model.SStaat;

/**
 * This class produces a RESTful service to read the contents of s_staat table.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("/staat")
@RequestScoped
public class SStaatService
{
    /**
     * The Repository for SUmwelt.
     */
    @Inject
    @Named("readolyrepository")
    private Repository repository;

    /**
     * Request all SUmwelt objects.
     *
     * @return JSON Object via Rest service
     */
    @GET
    @Produces("text/json")
    public Response findAll() {
        return repository.findAll(SStaat.class);
    }

    /**
     * Request a SUmwelt object via its id.
     *
     * @param id The SUmwelt id
     * @return JSON Object via REST service.
     */
    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces("text/json")
    public Response findById(@PathParam("id") String id) {
        return repository.findById(SStaat.class, id);
    }
}
