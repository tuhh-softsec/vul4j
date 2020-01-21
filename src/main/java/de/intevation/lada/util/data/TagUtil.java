/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */

package de.intevation.lada.util.data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.Probe;
import de.intevation.lada.model.land.TagZuordnung;
import de.intevation.lada.model.stammdaten.Tag;
import de.intevation.lada.util.rest.Response;

/**
 * Utility class containing methods for creating and setting tags.
 */
public class TagUtil {

    /**
     * Creates an auto generated tag using the current date and a given prefix.
     * Format is: {prefix}_yyyyMMdd_{serialNumber}
     * @param prefix Prefix to set
     * @param mstId mstId to set in the tag
     * @param repository Repository to use
     * @return Response of tag creation
     */
    public static synchronized Response generateTag(String prefix, String mstId, Repository repository) {
        //Get current date
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String today = date.format(formatter);

        //Get latest generated tag
        EntityManager stammEm = repository.entityManager(Strings.STAMM);
        CriteriaBuilder builder = stammEm.getCriteriaBuilder();
        CriteriaQuery<Tag> criteriaQuery = builder.createQuery(Tag.class);
        Root<Tag> tagRoot = criteriaQuery.from(Tag.class);
        Predicate nameFilter = builder.like(tagRoot.get("tag"), prefix + "\\_" + today + "\\_%");
        Order nameOrder = builder.asc(tagRoot.get("tag"));
        criteriaQuery.where(nameFilter);
        criteriaQuery.orderBy(nameOrder);
        List<Tag> tags = repository.filterPlain(criteriaQuery, Strings.STAMM);

        Integer serNumber = 1;
        //If tags were found, find next serial number
        if (tags.size() > 0) {
            AtomicInteger lastSerNumber = new AtomicInteger(0);
            tags.forEach(item -> {
                try {
                    Integer currentserial = Integer.parseInt(item.getTag().split("_")[2]);
                    if (lastSerNumber.get() < currentserial) {
                        lastSerNumber.set(currentserial);
                    }
                } catch (NumberFormatException nfe) {
                    //There might be a user generated tag also matching the generated tag pattern: Skip
                }
            });
            serNumber = lastSerNumber.get() + 1;
        }

        //Create next tag
        Tag currentTag = new Tag();
        currentTag.setGenerated(true);
        currentTag.setMstId(mstId);
        currentTag.setTag(prefix + "_" + today + "_" + serNumber);

        return repository.create(currentTag, Strings.STAMM);
    }

    /**
     * Sets tags for the given probe records an connected messung records.
     * @param probeIds Probe ids to set tags for
     * @param tagId Tag id to set
     * @param repository Repository to use
     * @return List of created tag references
     */
    public static List<TagZuordnung> setTagForProbeRecords(
            List<Integer> probeIds, Integer tagId, Repository repository) {
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