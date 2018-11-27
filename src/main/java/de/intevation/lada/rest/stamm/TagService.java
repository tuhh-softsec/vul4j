package de.intevation.lada.rest.stamm;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.auth.UserInfo;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.model.land.Probe;
import de.intevation.lada.model.land.TagZuordnung;
import de.intevation.lada.model.stammdaten.Tag;
import de.intevation.lada.util.rest.RequestMethod;
import de.intevation.lada.util.rest.Response;

/**
 * REST-Service for the probe tags
 */

 @Path("rest/tag")
 @RequestScoped
 public class TagService {

    @Inject
    @RepositoryConfig(type=RepositoryType.RW)
    private Repository repository;

    @Inject
    @AuthorizationConfig(type=AuthorizationType.HEADER)
    private Authorization authorization;

    /**
     * Get all tags for a Probe instance, filtered by the users messstelle id.
     * If a pid is set in the url, the tags are filter by the given probe id.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTags(
        @Context HttpServletRequest request,
        @Context UriInfo info
    ) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        Integer id = null;

        if (!params.isEmpty() && params.containsKey("pid")) {
            try {
                id = Integer.valueOf(params.getFirst("pid"));
            }
            catch (NumberFormatException e) {
                return new Response(false, 603, "Not a valid probe id");
            }
        }

        UserInfo userInfo = authorization.getInfo(request);
        EntityManager em = repository.entityManager(Strings.STAMM);
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Tag> criteriaQuery = builder.createQuery(Tag.class);
        Root<Tag> root = criteriaQuery.from(Tag.class);
        Join<Tag, TagZuordnung> joinTagZuordnung = root.join("tagZuordnungs", javax.persistence.criteria.JoinType.LEFT);
        Predicate zeroMstfilter = builder.isNull(root.get("mstId"));
        Predicate userMstFilter = builder.in(root.get("mstId")).value(userInfo.getMessstellen());
        Predicate probeFilter = builder.equal(joinTagZuordnung.get("probeId"),id);
        Predicate filter = builder.or(zeroMstfilter, userMstFilter);
        if (id != null) {
            filter = builder.and(filter, probeFilter);
        }
        criteriaQuery.where(filter);
        List<Tag> tags = repository.filterPlain(criteriaQuery, Strings.STAMM);
        return new Response(true, 200, tags);
    }

    /**
     * Creates a new reference between a tag and a probe.
     * The tag can be an existing one or a new one, embedded in the request.
     * Request for creating a new tag:
     * <pre>
     * <code>
     * {
     *   "probeId": [Integer],
     *   "tag": {
     *     "tag": [String],
     *     "mstId": [String]
     *   }
     * }
     * </code>
     * </pre>
     *
     * Existing tags can be used with the following request:
     * <pre>
     * <code>
     * {
     *   "probeId": [Integer],
     *   "tagId": [Integer]
     * }
     * </code>
     * </pre>
     * Requests containing both, tag and tagId will be rejected.
     * Setting a mstId is mandatory, as only global tags have no mstId.
     */
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTagReference(
        @Context HttpServletRequest request,
        TagZuordnung zuordnung
    ) {
        Tag tag = zuordnung.getTag();
        Integer tagId = zuordnung.getTagId();
        if (zuordnung == null
                || tag != null && tagId != null
                || tag == null && tagId == null) {
            return new Response(false, 603, "Not a valid tag");
        }

        UserInfo userInfo = authorization.getInfo(request);
        //Use existing tag
        if (tag == null) {
            //Check if tag is already assigned to the probe
            EntityManager em = repository.entityManager(Strings.STAMM);
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<TagZuordnung> criteriaQuery = builder.createQuery(TagZuordnung.class);
            Root<TagZuordnung> root = criteriaQuery.from(TagZuordnung.class);
            Join<TagZuordnung, Tag> joinTagZuordnung = root.join("tag", javax.persistence.criteria.JoinType.LEFT);
            Predicate tagFilter = builder.equal(root.get("tag").get("id"), zuordnung.getTagId());
            Predicate userMstFilter = builder.in(joinTagZuordnung.get("mstId")).value(userInfo.getMessstellen());
            Predicate probeFilter = builder.equal(root.get("probeId"),zuordnung.getProbeId());
            Predicate filter = builder.and(tagFilter, userMstFilter);
            filter = builder.and(filter, probeFilter);
            criteriaQuery.where(filter);
            List<TagZuordnung> zuordnungs = repository.filterPlain(criteriaQuery, Strings.STAMM);
            if (zuordnungs.size() > 0) {
                return new Response(false, 604, "Tag is already assigned to probe");
            }

            tag = repository.getByIdPlain(Tag.class, tagId, Strings.STAMM);
            String mstId = tag.getMstId();
            //If user tries to assign a global tag: authorize
            if ( mstId == null) {
                Probe probe = repository.getByIdPlain(Probe.class, zuordnung.getProbeId(), Strings.LAND);
                if (!authorization.isAuthorized(
                    request,
                    probe,
                    RequestMethod.PUT,
                    Probe.class
                )) {
                    return new Response(false, 699, "Not authorized to set global tag");
                }
            //Else check if it is the users private tag
            } else if (!userInfo.getMessstellen().contains(mstId)) {
                return new Response(false, 603, "Invalid mstId");
            }
            
            repository.create(zuordnung, Strings.LAND);
            zuordnung.setTag(tag);
            return repository.update(zuordnung, Strings.LAND);
        //Create new
        } else {
            String mstId = zuordnung.getTag().getMstId();
            //mstId may not be null, global tags cannot be created
            if ( mstId == null || !userInfo.getMessstellen().contains(mstId)) {
                return new Response(false, 603, "Invalid/empty mstId");
            }
            return repository.create(zuordnung, Strings.LAND);
        }
    }

    /**
     * Delete a reference between a tag and a probe
     */
    @DELETE
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTagReference(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        TagZuordnung tagZuordnung
    ) {
        if (tagZuordnung.getProbeId() == null || tagZuordnung.getTagId() == null) {
            return new Response(false, 699, "Invalid TagZuordnung");
        }
        boolean global = false;
        //Check if its a global tag
        Tag tag = repository.getByIdPlain(Tag.class, tagZuordnung.getTagId(), Strings.STAMM);
        if (tag.getMstId() == null) {
            Probe probe = repository.getByIdPlain(Probe.class, tagZuordnung.getProbeId(), Strings.LAND);
            if (!authorization.isAuthorized(
                request,
                probe,
                RequestMethod.PUT,
                Probe.class
            )) {
                return new Response(false, 699, "Not authorized to delete global tag");
            } else {
                global = true;
            }
        }

        UserInfo userInfo = authorization.getInfo(request);
        EntityManager em = repository.entityManager(Strings.STAMM);
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<TagZuordnung> criteriaQuery = builder.createQuery(TagZuordnung.class);
        Root<TagZuordnung> root = criteriaQuery.from(TagZuordnung.class);
        Join<TagZuordnung, Tag> joinTagZuordnung = root.join("tag", javax.persistence.criteria.JoinType.LEFT);
        Predicate tagFilter = builder.equal(root.get("tag").get("id"), tagZuordnung.getTagId());
        Predicate mstFilter;
        if (global == true) {
            mstFilter = builder.isNull(joinTagZuordnung.get("mstId"));
        } else {
            mstFilter = builder.in(joinTagZuordnung.get("mstId")).value(userInfo.getMessstellen());
        }
        Predicate probeFilter = builder.equal(root.get("probeId"),tagZuordnung.getProbeId());

        Predicate filter = builder.and(tagFilter, mstFilter);
        filter = builder.and(filter, probeFilter);
        criteriaQuery.where(filter);
        List<TagZuordnung> zuordnungs = repository.filterPlain(criteriaQuery, Strings.STAMM);

        //TODO: Error code if no zuordnung is found?
        if (zuordnungs.size() == 0) {
            return new Response(false, 699, "No valid Tags found");
        } else {
            return repository.delete(zuordnungs.get(0), Strings.LAND);
        }
    }
}