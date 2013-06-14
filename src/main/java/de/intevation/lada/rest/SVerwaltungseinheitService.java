package de.intevation.lada.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import de.intevation.lada.data.Repository;
import de.intevation.lada.model.SVerwaltungseinheit;

/**
 * This class produces a RESTful service to read the contents of
 * s_verwaltungseinheit table.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("/verwaltungseinheit")
@RequestScoped
public class SVerwaltungseinheitService
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
        return repository.findAll(SVerwaltungseinheit.class);
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
        return repository.findById(SVerwaltungseinheit.class, id);
    }
}
