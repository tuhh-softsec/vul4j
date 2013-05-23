package de.intevation.lada.rest;

import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.data.LProbeRepository;
import de.intevation.lada.model.LProbe;
import de.intevation.lada.service.LProbeService;

/**
 * JAX-RS Example
 *
 * This class produces a RESTful service to read the contents of the members table.
*/

@Path("/proben")
@RequestScoped
public class LProbeRESTService {

    @Inject
    private LProbeRepository repository;

    @Inject
    private Logger log;

    @GET
    @Path("/{id}")
    @Produces("text/json")
    public LProbe loadById(@PathParam("id") String id) {
       return repository.findById(id);
    }

    @GET
    @Path("/deleteLast")
    @Produces("text/plain")
    public String deleteLast() {
       final List<LProbe> result = repository.findAll();
       LProbe last_element = result.get(result.size()-1);
       repository.delete(last_element);
       return "Gelöscht id" + last_element.getProbeId();
    }

    @GET
    @Produces("text/json")
    public List<LProbe> filter(@Context UriInfo info) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty()) {
            return repository.findAll();
        }
        String mstId = "";
        String uwbId = "";
        Long begin = null;
        if (params.containsKey("mst")) {
            mstId = params.getFirst("mst");
        }
        if (params.containsKey("uwb")) {
            uwbId = params.getFirst("uwb");
        }
        if (params.containsKey("begin")) {
            String tmp = params.getFirst("begin");
            try {
                begin = Long.valueOf(tmp);
            }
            catch (NumberFormatException nfe) {
                begin = null;
            }
        }
        return repository.filter(mstId, uwbId, begin);
    }
    //@GET
    //@Produces("text/xml")
    //public List<Member> listAllMembers() {
    //   // Us @SupressWarnings to force IDE to ignore warnings about "genericizing" the results of
    //   // this query
    //   @SuppressWarnings("unchecked")
    //   // We recommend centralizing inline queries such as this one into @NamedQuery annotations on
    //   // the @Entity class
    //   // as described in the named query blueprint:
    //   // https://blueprints.dev.java.net/bpcatalog/ee5/persistence/namedquery.html
    //   final List<Member> results = em.createQuery("select m from Member m order by m.name").getResultList();
    //   return results;
    //}

    //@GET
    //@Path("/{id:[0-9][0-9]*}")
    //@Produces("text/xml")
    //public Member lookupMemberById(@PathParam("id") long id) {
    //   return em.find(Member.class, id);
    //}
}
