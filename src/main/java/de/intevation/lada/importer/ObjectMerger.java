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
import de.intevation.lada.util.rest.Response;

public class ObjectMerger {

    @Inject
    Logger logger;

    @Inject
    @RepositoryConfig(type=RepositoryType.RW)
    private Repository repository;

//    @Inject
//    private UserTransaction userTransaction;
//    @Resource
//    private TransactionManager transactionManager;
    //@PersistenceContext(unitName="land")
    //private EntityManager entityManager;

    public ObjectMerger merge(Probe target, Probe src) {
        target.setBaId(src.getBaId());
        target.setDatenbasisId(src.getDatenbasisId());
        target.setErzeugerId(src.getErzeugerId());
        if (!src.getHauptprobenNr().isEmpty()) {
            target.setHauptprobenNr(src.getHauptprobenNr());
        }
        target.setLaborMstId(src.getLaborMstId());
        target.setMedia(src.getMedia());
        target.setMediaDesk(src.getMediaDesk());
        target.setMittelungsdauer(src.getMittelungsdauer());
        target.setMplId(src.getMplId());
        target.setMprId(src.getMprId());
        target.setProbeentnahmeBeginn(src.getProbeentnahmeBeginn());
        target.setProbeentnahmeEnde(src.getProbeentnahmeEnde());
        target.setProbenartId(src.getProbenartId());
        target.setProbeNehmerId(src.getProbeNehmerId());
        target.setSolldatumBeginn(src.getSolldatumBeginn());
        target.setSolldatumEnde(src.getSolldatumEnde());
        target.setTest(src.getTest());
        target.setUmwId(src.getUmwId());
        repository.update(target, "land");
        return this;
    }

    public ObjectMerger mergeMessung(Messung target, Messung src) {
        if (target.getNebenprobenNr().isEmpty()) {
            target.setNebenprobenNr(src.getNebenprobenNr());
        }
        target.setFertig(src.getFertig());
        target.setGeplant(src.getGeplant() == null ? false : src.getGeplant());
        target.setMessdauer(src.getMessdauer());
        target.setMesszeitpunkt(src.getMesszeitpunkt());
        target.setMmtId(src.getMmtId());
        Response r = repository.update(target, "land");
        target = (Messung)r.getData();
        return this;
    }

    public ObjectMerger mergeZusatzwerte(
        Probe target,
        List<ZusatzWert> zusatzwerte
    ) {
        QueryBuilder<ZusatzWert> builder = new QueryBuilder<ZusatzWert>(
            repository.entityManager("land"),
            ZusatzWert.class);
        for (int i = 0; i < zusatzwerte.size(); i++) {
            builder.and("probeId", target.getId());
            builder.and("pzsId", zusatzwerte.get(i).getPzsId());
            List<ZusatzWert> found =
                repository.filterPlain(builder.getQuery(), "land");
            if (found.isEmpty()) {
                repository.create(zusatzwerte.get(i), "land");
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
            found.get(0).setNwgZuMesswert(zusatzwerte.get(i).getNwgZuMesswert());
            repository.update(found.get(0), "land");
            builder = builder.getEmptyBuilder();
        }
        return this;
    }

    public ObjectMerger mergeKommentare(
        Probe target,
        List<KommentarP> kommentare
    ) {
        QueryBuilder<KommentarP> builder = new QueryBuilder<KommentarP>(
            repository.entityManager("land"),
            KommentarP.class);
        for (int i = 0; i < kommentare.size(); i++) {
            builder.and("probeId", target.getId());
            builder.and("mstId", kommentare.get(i).getMstId());
            builder.and("datum", kommentare.get(i).getDatum());
            List<KommentarP> found =
                repository.filterPlain(builder.getQuery(), "land");
            if (found.isEmpty()) {
                repository.create(kommentare.get(i), "land");
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
            repository.entityManager("land"),
            KommentarM.class);
        for (int i = 0; i < kommentare.size(); i++) {
            builder.and("messungsId", target.getId());
            builder.and("mstId", kommentare.get(i).getMstId());
            builder.and("datum", kommentare.get(i).getDatum());
            List<KommentarM> found =
                repository.filterPlain(builder.getQuery(), "land");
            if (found.isEmpty()) {
                repository.create(kommentare.get(i), "land");
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
            repository.entityManager("land"),
            Messwert.class);
        builder.and("messungsId", target.getId());
        List<Messwert> found =
            repository.filterPlain(builder.getQuery(), "land");
        if (found.isEmpty()) {
            for (int i = 0; i < messwerte.size(); i++) {
                repository.create(messwerte.get(i), "land");
            }
            return this;
        }
        try {
            for (int i = 0; i < found.size(); i++) {
                repository.delete(found.get(i), "land");
            }
            for (int i = 0; i < messwerte.size(); i++) {
                repository.create(messwerte.get(i), "land");
            }
        } catch (SecurityException |
            IllegalStateException |
            PersistenceException e
        ) {
            // Restore messwerte.
            logger.debug("exception: ", e);
            for (int i = 0; i < found.size(); i++) {
                repository.update(found.get(i), "land");
            }
        }
        return this;
    }

    public ObjectMerger mergeEntnahmeOrt(
        int probeId,
        Ortszuordnung ort
    ) {
        QueryBuilder<Ortszuordnung> builder = new QueryBuilder<Ortszuordnung>(
            repository.entityManager("land"),
            Ortszuordnung.class);
        builder.and("probeId", probeId);
        builder.and("ortszuordnungTyp", "E");
        List<Ortszuordnung> found =
            repository.filterPlain(builder.getQuery(), "land");
        if (found.isEmpty()) {
            repository.create(ort, "land");
            return this;
        }
        try {
            for (int i = 0; i < found.size(); i++) {
                repository.delete(found.get(i), "land");
            }
            repository.create(ort, "land");
        } catch (SecurityException |
            IllegalStateException |
            PersistenceException e
        ) {
            // Restore orte.
            logger.debug("exception: ", e);
            for (int i = 0; i < found.size(); i++) {
                repository.update(found.get(i), "land");
            }
        }
        return this;
    }

    public ObjectMerger mergeUrsprungsOrte(
        int probeId,
        List<Ortszuordnung> orte
    ) {
        QueryBuilder<Ortszuordnung> builder = new QueryBuilder<Ortszuordnung>(
            repository.entityManager("land"),
            Ortszuordnung.class);
        for (int i = 0; i < orte.size(); i++) {
            builder.and("probeId", probeId);
            builder.and("ortszuordnungTyp", "U");
            builder.and("ortId", orte.get(i).getOrtId());
            List<Ortszuordnung> found =
                repository.filterPlain(builder.getQuery(), "land");
            if (found.isEmpty()) {
                repository.create(orte.get(i), "land");
            }
            builder = builder.getEmptyBuilder();
        }
        return this;
    }
}
