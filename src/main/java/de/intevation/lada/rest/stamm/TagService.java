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
import de.intevation.lada.model.land.TagZuordnung;
import de.intevation.lada.model.stammdaten.Tag;
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
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTags(
        @Context HttpServletRequest request,
        @Context UriInfo info
    ) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        if (params.isEmpty() || !params.containsKey("pid")) {
            return new Response(false, 603, "Not a valid probe id");
        }

        Integer id = null;
        try {
            id = Integer.valueOf(params.getFirst("pid"));
        }
        catch (NumberFormatException e) {
            return new Response(false, 603, "Not a valid probe id");
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
        Predicate filter = builder.and(zeroMstfilter, userMstFilter);
        filter = builder.or(filter, probeFilter);
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

        if (tag == null) {
            repository.create(zuordnung, Strings.LAND);
            tag = repository.getByIdPlain(Tag.class, tagId, Strings.STAMM);
            zuordnung.setTag(tag);
            return repository.update(zuordnung, Strings.LAND);
        } else {
            String mstId = zuordnung.getTag().getMstId();
            if ( mstId == null || !userInfo.getMessstellen().contains(mstId)) {
                return new Response(false, 603, "Invalid mstId");
            }
            return repository.create(zuordnung, Strings.LAND);
        }
    }

    /**
     * Delete a reference between a tag and a probe
     */
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTagReference(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        @PathParam("id") Integer id
    ) {
        TagZuordnung tagZuordnung = repository.getByIdPlain(TagZuordnung.class, id, Strings.LAND);
        UserInfo userInfo = authorization.getInfo(request);

        if (userInfo.getMessstellen().contains(tagZuordnung.getTag().getMstId())) {
            return repository.delete(tagZuordnung, Strings.LAND);
        } else {
            return new Response(false, 699, null);
        }
    }
}