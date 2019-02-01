/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3) 
 * and comes with ABSOLUTELY NO WARRANTY! Check out 
 * the documentation coming with IMIS-Labordaten-Application for details. 
 */
package de.intevation.lada.importer;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.log4j.Logger;

import de.intevation.lada.model.land.KommentarM;
import de.intevation.lada.model.land.KommentarP;
import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.Messwert;
import de.intevation.lada.model.land.Ortszuordnung;
import de.intevation.lada.model.land.Probe;
import de.intevation.lada.model.land.ZusatzWert;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.util.rest.Response;

public class ObjectMerger {

    @Inject
    Logger logger;

    @Inject
    @RepositoryConfig(type=RepositoryType.RW)
    private Repository repository;

    public boolean merge(Probe target, Probe src) {
        if (src.getBaId() != null) target.setBaId(src.getBaId());
        if (src.getDatenbasisId() != null) target.setDatenbasisId(src.getDatenbasisId());
        if (src.getErzeugerId() != null) target.setErzeugerId(src.getErzeugerId());
        if (src.getHauptprobenNr() != null &&
            !src.getHauptprobenNr().isEmpty()) {
            target.setHauptprobenNr(src.getHauptprobenNr());
        }
        if (src.getLaborMstId() != null) target.setLaborMstId(src.getLaborMstId());
        if (src.getMedia() != null) target.setMedia(src.getMedia());
        if (src.getMediaDesk() != null) target.setMediaDesk(src.getMediaDesk());
        if (src.getMittelungsdauer() != null) target.setMittelungsdauer(src.getMittelungsdauer());
        if (src.getMplId() != null) target.setMplId(src.getMplId());
        if (src.getProbeentnahmeBeginn() != null) target.setProbeentnahmeBeginn(src.getProbeentnahmeBeginn());
        if (src.getProbeentnahmeEnde() != null) target.setProbeentnahmeEnde(src.getProbeentnahmeEnde());
        if (src.getProbenartId() != null) target.setProbenartId(src.getProbenartId());
        if (src.getProbeNehmerId() != null) target.setProbeNehmerId(src.getProbeNehmerId());
        if (src.getSolldatumBeginn() != null) target.setSolldatumBeginn(src.getSolldatumBeginn());
        if (src.getSolldatumEnde() != null) target.setSolldatumEnde(src.getSolldatumEnde());
        if (src.getTest() != null) {
            if (target.getTest() == null) target.setTest(src.getTest());
        } else {
            // Set explicit to false, if is null in src to not violate constraints
            target.setTest(false);
        }
        if (src.getUmwId() != null) target.setUmwId(src.getUmwId());
        Response r = repository.update(target, Strings.LAND);
        return r.getSuccess();
    }

    public ObjectMerger mergeMessung(Messung target, Messung src) {
        if (target.getNebenprobenNr() == null ||
            target.getNebenprobenNr().isEmpty()) {
            target.setNebenprobenNr(src.getNebenprobenNr());
        }
        if (src.getFertig() != null) {
            if (target.getFertig() == null) target.setFertig(src.getFertig());
        } else {
            target.setFertig(false);
        }
        if (src.getGeplant() != null) {
            if (target.getGeplant() == null) target.setGeplant(src.getGeplant());
        } else {
            target.setGeplant(false);
        }
        if (src.getMessdauer() != null) target.setMessdauer(src.getMessdauer());
        if (src.getMesszeitpunkt() != null) target.setMesszeitpunkt(src.getMesszeitpunkt());
        if (src.getMmtId() != null) target.setMmtId(src.getMmtId());
        Response r = repository.update(target, Strings.LAND);
        target = (Messung)r.getData();
        return this;
    }

    public ObjectMerger mergeZusatzwerte(
        Probe target,
        List<ZusatzWert> zusatzwerte
    ) {
        QueryBuilder<ZusatzWert> builder = new QueryBuilder<ZusatzWert>(
            repository.entityManager(Strings.LAND),
            ZusatzWert.class);
        for (int i = 0; i < zusatzwerte.size(); i++) {
            builder.and("probeId", target.getId());
            builder.and("pzsId", zusatzwerte.get(i).getPzsId());
            List<ZusatzWert> found =
                repository.filterPlain(builder.getQuery(), Strings.LAND);
            if (found.isEmpty()) {
                repository.create(zusatzwerte.get(i), Strings.LAND);
                continue;
            }
            else if (found.size() > 1) {
                // something is wrong (probeId and pzsId should be unique).
                // Continue and skip this zusatzwert.
                continue;
            }
            // Update the objects.
            // direktly update the db or update the list!?
            // Updating the list could be a problem. List objects are detatched.
            //
            // Current solution:
            // Remove all db objects to be able to create new ones.
            found.get(0).setMessfehler(zusatzwerte.get(i).getMessfehler());
            found.get(0).setMesswertPzs(zusatzwerte.get(i).getMesswertPzs());
            repository.update(found.get(0), Strings.LAND);
            builder = builder.getEmptyBuilder();
        }
        return this;
    }

    public ObjectMerger mergeKommentare(
        Probe target,
        List<KommentarP> kommentare
    ) {
        QueryBuilder<KommentarP> builder = new QueryBuilder<KommentarP>(
            repository.entityManager(Strings.LAND),
            KommentarP.class);
        for (int i = 0; i < kommentare.size(); i++) {
            builder.and("probeId", target.getId());
            builder.and("mstId", kommentare.get(i).getMstId());
            builder.and("datum", kommentare.get(i).getDatum());
            List<KommentarP> found =
                repository.filterPlain(builder.getQuery(), Strings.LAND);
            if (found.isEmpty()) {
                repository.create(kommentare.get(i), Strings.LAND);
                continue;
            }
            else if (found.size() > 1) {
                // something is wrong (probeId and mstId and datum should be unique).
                // Continue and skip this kommentar.
                continue;
            }
            builder = builder.getEmptyBuilder();
        }
        return this;
    }

    public ObjectMerger mergeMessungKommentare(
        Messung target,
        List<KommentarM> kommentare
    ) {
        QueryBuilder<KommentarM> builder = new QueryBuilder<KommentarM>(
            repository.entityManager(Strings.LAND),
            KommentarM.class);
        for (int i = 0; i < kommentare.size(); i++) {
            builder.and("messungsId", target.getId());
            builder.and("mstId", kommentare.get(i).getMstId());
            builder.and("datum", kommentare.get(i).getDatum());
            List<KommentarM> found =
                repository.filterPlain(builder.getQuery(), Strings.LAND);
            if (found.isEmpty()) {
                repository.create(kommentare.get(i), Strings.LAND);
                continue;
            }
            else if (found.size() > 1) {
                // something is wrong (probeId and mstId and datum should be unique).
                // Continue and skip this zusatzwert.
                continue;
            }
            builder = builder.getEmptyBuilder();
        }
        return this;
    }

    public ObjectMerger mergeMesswerte(
        Messung target,
        List<Messwert> messwerte
    ) {
        QueryBuilder<Messwert> builder = new QueryBuilder<Messwert>(
            repository.entityManager(Strings.LAND),
            Messwert.class);
        builder.and("messungsId", target.getId());
        List<Messwert> found =
            repository.filterPlain(builder.getQuery(), Strings.LAND);
        if (found.isEmpty()) {
            for (int i = 0; i < messwerte.size(); i++) {
                repository.create(messwerte.get(i), Strings.LAND);
            }
            return this;
        }
        try {
            for (int i = 0; i < found.size(); i++) {
                repository.delete(found.get(i), Strings.LAND);
            }
            for (int i = 0; i < messwerte.size(); i++) {
                repository.create(messwerte.get(i), Strings.LAND);
            }
        } catch (SecurityException |
            IllegalStateException |
            PersistenceException e
        ) {
            // Restore messwerte.
            logger.debug("exception: ", e);
            for (int i = 0; i < found.size(); i++) {
                repository.update(found.get(i), Strings.LAND);
            }
        }
        return this;
    }

    public ObjectMerger mergeEntnahmeOrt(
        int probeId,
        Ortszuordnung ort
    ) {
        QueryBuilder<Ortszuordnung> builder = new QueryBuilder<Ortszuordnung>(
            repository.entityManager(Strings.LAND),
            Ortszuordnung.class);
        builder.and("probeId", probeId);
        builder.and("ortszuordnungTyp", "E");
        List<Ortszuordnung> found =
            repository.filterPlain(builder.getQuery(), Strings.LAND);
        if (found.isEmpty()) {
            repository.create(ort, Strings.LAND);
            return this;
        }
        try {
            for (int i = 0; i < found.size(); i++) {
                repository.delete(found.get(i), Strings.LAND);
            }
            repository.create(ort, Strings.LAND);
        } catch (SecurityException |
            IllegalStateException |
            PersistenceException e
        ) {
            // Restore orte.
            logger.debug("exception: ", e);
            for (int i = 0; i < found.size(); i++) {
                repository.update(found.get(i), Strings.LAND);
            }
        }
        return this;
    }

    public ObjectMerger mergeUrsprungsOrte(
        int probeId,
        List<Ortszuordnung> orte
    ) {
        QueryBuilder<Ortszuordnung> builder = new QueryBuilder<Ortszuordnung>(
            repository.entityManager(Strings.LAND),
            Ortszuordnung.class);
        for (int i = 0; i < orte.size(); i++) {
            builder.and("probeId", probeId);
            builder.and("ortszuordnungTyp", "U");
            builder.and("ortId", orte.get(i).getOrtId());
            List<Ortszuordnung> found =
                repository.filterPlain(builder.getQuery(), Strings.LAND);
            if (found.isEmpty()) {
                repository.create(orte.get(i), Strings.LAND);
            }
            builder = builder.getEmptyBuilder();
        }
        return this;
    }
}
