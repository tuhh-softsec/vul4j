package de.intevation.rest;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import de.intevation.data.LProbeRepository;
import de.intevation.model.LProbe;

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
   @Produces("text/plain")
   public String listAllMembers() {
      final List<LProbe> result = repository.findAll();
      System.out.println(result);
      return "Probenliste";
   }

   @GET
   @Path("/{id:[0-9][0-9]*}")
   @Produces("text/plain")
   public String loadById() {
      return "Eine Probe!";
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
