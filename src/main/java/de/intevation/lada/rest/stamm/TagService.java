package de.intevation.lada.rest.stamm;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
import de.intevation.lada.model.land.Messung;
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
     * Get all tags for a Probe or Messung instance, filtered by the users messstelle id.
     * If a pid is set in the url, the tags are filter by the given probe id.
     * If a mid is set in the url, the tags are filter by the given messung id.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTags(
        @Context HttpServletRequest request,
        @Context UriInfo info
    ) {
        MultivaluedMap<String, String> params = info.getQueryParameters();
        Integer probeId = null;
        Integer messungId = null;

        if (!params.isEmpty() && params.containsKey("pid") && params.containsKey("mid")) {
            return new Response(false, 603, "Filtering by both pid and mid not allowed");
        }

        if (!params.isEmpty() && params.containsKey("pid")) {
            try {
                probeId = Integer.valueOf(params.getFirst("pid"));
            }
            catch (NumberFormatException e) {
                return new Response(false, 603, "Not a valid probe id");
            }
        }

        if (!params.isEmpty() && params.containsKey("mid")) {
            try {
                messungId = Integer.valueOf(params.getFirst("mid"));
            } catch (NumberFormatException nfe) {
                return new Response(false, 603, "Not a valid messung id");
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
        Predicate probeFilter = builder.equal(joinTagZuordnung.get("probeId"), probeId);
        Predicate messungFilter = builder.equal(joinTagZuordnung.get("messungId"), messungId);
        Predicate filter = builder.or(zeroMstfilter, userMstFilter);

        if (probeId != null) {
            filter = builder.and(filter, probeFilter);
        }

        if (messungId != null) {
            filter = builder.and(filter, messungFilter);
        }

        criteriaQuery.where(filter);
        List<Tag> tags = repository.filterPlain(criteriaQuery, Strings.STAMM);
        return new Response(true, 200, tags);
    }

    /**
     * Creates and sets a generated tag for a list of generated probe and messung
     * instances.
     * The created tag has the format "PEP_<YYYYMMDD>_<#>", with <#> as a serial.
     * <pre>
     * <code>
     * {
     *   "probeIds": [Integer[]],
     *   "mstId": [String]
     * </code>
     * </pre>
     */
    @POST
    @Path("/generated")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createGeneratedTags(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        JsonObject object
    ) {
        UserInfo userInfo = authorization.getInfo(request);
        List<Integer> probeIds = new ArrayList<Integer>();

        //Check given mstId
        String mstId;
        try {
            mstId = object.getString("mstId");
        } catch (NullPointerException npe) {
            return new Response(false, 699, "Invalid mstId");
        }

        if (mstId == null || !userInfo.getMessstellen().contains(mstId)) {
            return new Response(false, 699, "Invalid mstId");
        }

        //Parse probe ids
        JsonArray probeIdArray = object.getJsonArray("probeIds");
        try {
            probeIdArray.forEach(value -> {
                probeIds.add(Integer.parseInt(value.toString()));
            });
        } catch (NumberFormatException nfe) {
            return new Response(false, 699, "Invalid probe id(s)");
        }
        Response resp = generateTag("PEP", userInfo.getMessstellen().get(0));
        Tag currentTag = (Tag) resp.getData();

        return new Response(true, 200, setTagForProbeRecords(probeIds, currentTag.getId()));
    }

    /**
     * Creates and sets a generated tag for a list of imported probe and messung
     * instances.
     * The created tag has the format "IMP_<YYYYMMDD>_<#>", with <#> as a serial.
     * <pre>
     * <code>
     * {
     *   "probeIds": [Integer[]],
     *   "mstId": [String]
     * </code>
     * </pre>
     */
    @POST
    @Path("/imported")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createImportedTags(
        @Context HttpHeaders headers,
        @Context HttpServletRequest request,
        JsonObject object
    ) {
        UserInfo userInfo = authorization.getInfo(request);
        List<Integer> probeIds = new ArrayList<Integer>();

        //Check given mstId
        String mstId;
        try {
            mstId = object.getString("mstId");
        } catch (NullPointerException npe) {
            return new Response(false, 699, "Invalid mstId");
        }

        if (mstId == null || !userInfo.getMessstellen().contains(mstId)) {
            return new Response(false, 699, "Invalid mstId");
        }

        //Parse probe ids
        JsonArray probeIdArray = object.getJsonArray("probeIds");
        try {
            probeIdArray.forEach(value -> {
                probeIds.add(Integer.parseInt(value.toString()));
            });
        } catch (NumberFormatException nfe) {
            return new Response(false, 699, "Invalid probe id(s)");
        }
        Response resp = generateTag("IMP", userInfo.getMessstellen().get(0));
        Tag currentTag = (Tag) resp.getData();

        return new Response(true, 200, setTagForProbeRecords(probeIds, currentTag.getId()));
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
                || tag == null && tagId == null
                || zuordnung.getProbeId() != null && zuordnung.getMessungId() != null) {
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
            Predicate filter = builder.and(tagFilter, userMstFilter);
            if (zuordnung.getProbeId() != null) {
                Predicate probeFilter = builder.equal(root.get("probeId"),zuordnung.getProbeId());
                filter = builder.and(filter, probeFilter);
            } else {
                Predicate messungFilter = builder.equal(root.get("messungId"), zuordnung.getMessungId());
                filter = builder.and(filter, messungFilter);
            }
            criteriaQuery.where(filter);
            List<TagZuordnung> zuordnungs = repository.filterPlain(criteriaQuery, Strings.STAMM);
            if (zuordnungs.size() > 0) {
                return new Response(false, 604, "Tag is already assigned to probe");
            }

            tag = repository.getByIdPlain(Tag.class, tagId, Strings.STAMM);
            String mstId = tag.getMstId();
            //If user tries to assign a global tag: authorize
            if ( mstId == null) {
                Object data;
                boolean authorized = false;
                if (zuordnung.getMessungId() != null) {
                    data = repository.getByIdPlain(Messung.class, zuordnung.getMessungId(), Strings.LAND);
                    authorized = authorization.isAuthorized(
                        request,
                        data,
                        RequestMethod.PUT,
                        Messung.class
                    );
                } else {
                    data = repository.getByIdPlain(Probe.class, zuordnung.getProbeId(), Strings.LAND);
                    authorized = authorization.isAuthorized(
                        request,
                        data,
                        RequestMethod.PUT,
                        Probe.class
                    );
                }
                if (!authorized) {
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
            if (repository.create(tag, Strings.STAMM).getSuccess() == true) {
                return repository.create(zuordnung, Strings.LAND);
            } else {
                //TODO: Proper response code?
                return new Response(false, 603, "Failed to create Tag");
            }
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
        if ((tagZuordnung.getProbeId() == null && tagZuordnung.getMessungId() == null)
                || tagZuordnung.getTagId() == null) {
            return new Response(false, 699, "Invalid TagZuordnung");
        }
        boolean global = false;
        //Check if its a global tag
        Tag tag = repository.getByIdPlain(Tag.class, tagZuordnung.getTagId(), Strings.STAMM);
        if (tag.getMstId() == null) {
            Object data;
            boolean authorized = false;
            if (tagZuordnung.getMessungId() != null) {
                data = repository.getByIdPlain(Messung.class, tagZuordnung.getMessungId(), Strings.LAND);
                authorized = authorization.isAuthorized(
                    request,
                    data,
                    RequestMethod.PUT,
                    Messung.class
                );
            } else {
                data = repository.getByIdPlain(Probe.class, tagZuordnung.getProbeId(), Strings.LAND);
                authorized = authorization.isAuthorized(
                    request,
                    data,
                    RequestMethod.PUT,
                    Probe.class
                );
            }
            if (!authorized) {
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
        Predicate filter = builder.and(tagFilter, mstFilter);

        if (tagZuordnung.getProbeId() != null) {
            Predicate probeFilter = builder.equal(root.get("probeId"), tagZuordnung.getProbeId());
            filter = builder.and(filter, probeFilter);
        } else {
            Predicate messungFilter = builder.equal(root.get("messungId"), tagZuordnung.getMessungId());
            filter = builder.and(filter, messungFilter);
        }


        criteriaQuery.where(filter);
        List<TagZuordnung> zuordnungs = repository.filterPlain(criteriaQuery, Strings.STAMM);

        //TODO: Error code if no zuordnung is found?
        if (zuordnungs.size() == 0) {
            return new Response(false, 699, "No valid Tags found");
        } else {
            return repository.delete(zuordnungs.get(0), Strings.LAND);
        }
    }

    /**
     * Creates an auto generated tag using the current date and a given prefix.
     * Format is: {prefix}_yyyyMMdd_{serialNumber}
     * @param prefix Prefix to set
     * @param mstId mstId to set in the tag
     * @return Response of tag creation
     */
    public Response generateTag(String prefix, String mstId) {
        //Get current date
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String today = date.format(formatter);

        //Get latest generated tag
        EntityManager stammEm = repository.entityManager(Strings.STAMM);
        CriteriaBuilder builder = stammEm.getCriteriaBuilder();
        CriteriaQuery<Tag> criteriaQuery = builder.createQuery(Tag.class);
        Root<Tag> tagRoot = criteriaQuery.from(Tag.class);
        Predicate nameFilter = builder.like(tagRoot.get("tag"), prefix + "\\_" + today + "\\__");
        Predicate mstFilter = builder.equal(tagRoot.get("mstId"), mstId);
        Predicate filter = builder.and(nameFilter, mstFilter);
        Order nameOrder = builder.asc(tagRoot.get("tag"));
        criteriaQuery.where(filter);
        criteriaQuery.orderBy(nameOrder);
        List<Tag> tags = repository.filterPlain(criteriaQuery, Strings.STAMM);

        Integer serNumber = 1;
        //If tags were found, find next serial number
        if (tags.size() > 0) {
            String lastTag = tags.get(tags.size() - 1 ).getTag();
            Integer lastSerNumber = Integer.parseInt(lastTag.split("_")[2]);
            serNumber = lastSerNumber + 1;
        }

        //Create next tag
        Tag currentTag = new Tag();
        currentTag.setMstId(mstId);
        currentTag.setTag(prefix + "_" + today + "_" + serNumber);

        return repository.create(currentTag, Strings.STAMM);
    }

    /**
     * Sets tags for the given probe records an connected messung records.
     * @param probeIds Probe ids to set tags for
     * @param tagId Tag id to set
     * @return List of created tag references
     */
    public List<TagZuordnung> setTagForProbeRecords(List<Integer> probeIds, Integer tagId) {
        Tag tag = repository.getByIdPlain(Tag.class, tagId, Strings.STAMM);

        //Get given probe and messung records
        EntityManager landEm = repository.entityManager(Strings.LAND);
        CriteriaBuilder probeBuilder = landEm.getCriteriaBuilder();
        CriteriaQuery<Probe> probeQuery = probeBuilder.createQuery(Probe.class);
        Root<Probe> probeRoot = probeQuery.from(Probe.class);
        Predicate pidFilter = probeBuilder.in(probeRoot.get("id")).value(probeIds);
        probeQuery.where(pidFilter);
        List<Probe> probes = repository.filterPlain(probeQuery, Strings.LAND);

        CriteriaBuilder messungBuilder = landEm.getCriteriaBuilder();
        CriteriaQuery<Messung> messungQuery = messungBuilder.createQuery(Messung.class);
        Root<Messung> messungRoot = messungQuery.from(Messung.class);
        Predicate messungPidFilter = messungBuilder.in(messungRoot.get("probeId")).value(probeIds);
        messungQuery.where(messungPidFilter);
        List<Messung> messungs = repository.filterPlain(messungQuery, Strings.LAND);

        //Set tags
        List<TagZuordnung> zuordnungs = new ArrayList<TagZuordnung>();
        probes.forEach(probe -> {
            TagZuordnung zuordnung = new TagZuordnung();
            zuordnung.setTag(tag);
            zuordnung.setProbeId(probe.getId());
            repository.create(zuordnung, Strings.LAND);
            zuordnungs.add(zuordnung);
        });

        messungs.forEach(messung -> {
            TagZuordnung zuordnung = new TagZuordnung();
            zuordnung.setTag(tag);
            zuordnung.setMessungId(messung.getId());
            repository.create(zuordnung, Strings.LAND);
            zuordnungs.add(zuordnung);
        });
        return zuordnungs;
    }
}