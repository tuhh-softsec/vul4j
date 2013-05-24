package de.intevation.lada.rest;

import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import de.intevation.lada.data.Repository;
import de.intevation.lada.model.SUmwelt;
import javax.inject.Named;

/**
 * This class produces a RESTful service to read the contents of s_umwelt table.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("/uwb")
@RequestScoped
public class SUmweltService {

    /**
     * The Repository for SUmwelt.
     */
    @Inject @Named
    private Repository repository;

    /**
     * The logger for this class.
     */
    @Inject
    private Logger log;

    /**
     * Request all SUmwelt objects.
     *
     * @return JSON Object via Rest service
     */
    @GET
    @Produces("text/json")
    public List<SUmwelt> findAll() {
        List<SUmwelt> result = repository.findAll(SUmwelt.class);
        return result;
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
    public SUmwelt loadById(@PathParam("id") String id) {
        return repository.findById(SUmwelt.class, id);
    }
}
