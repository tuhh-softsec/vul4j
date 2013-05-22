package de.intevation.lada.rest;

import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import de.intevation.lada.data.SUmweltRepository;
import de.intevation.lada.model.SUmwelt;

@Path("/uwb")
@RequestScoped
public class SUmweltRESTService {
    @Inject
    private SUmweltRepository repository;

    @Inject
    private Logger log;

    @GET
    @Produces("text/json")
    public List<SUmwelt> listAllMembers() {
        final List<SUmwelt> result = repository.findAll();
        return result;
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces("text/json")
    public SUmwelt loadById(@PathParam("id") String id) {
        return repository.findById(id);
    }
}
