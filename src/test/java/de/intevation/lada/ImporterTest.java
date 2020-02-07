/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3) 
 * and comes with ABSOLUTELY NO WARRANTY! Check out 
 * the documentation coming with IMIS-Labordaten-Application for details. 
 */
package de.intevation.lada;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.CleanupStrategy;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.intevation.lada.importer.Identified;
import de.intevation.lada.importer.Identifier;
import de.intevation.lada.importer.IdentifierConfig;
import de.intevation.lada.importer.ObjectMerger;
import de.intevation.lada.model.land.KommentarM;
import de.intevation.lada.model.land.KommentarP;
import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.Messwert;
import de.intevation.lada.model.land.Probe;
import de.intevation.lada.model.land.ZusatzWert;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;

/**
 * Class to test the Lada-Importer
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@RunWith(Arquillian.class)
public class ImporterTest extends BaseTest{

    @Inject
    Logger internalLogger;

    @PersistenceContext(unitName="land")
    EntityManager em;

    @Inject
    @IdentifierConfig(type="Probe")
    Identifier probeIdentifier;

    @Inject
    @IdentifierConfig(type="Messung")
    Identifier messungIdentifier;

    @Inject
    @RepositoryConfig(type=RepositoryType.RW)
    Repository repository;

    @Inject
    ObjectMerger merger;

    public ImporterTest() {
        testProtocol = new ArrayList<Protocol>();
    }

    /**
     * PROBE IDENTIFIER
     */
    @Test
    @ApplyScriptBefore("datasets/clean_and_seed.sql")
    @InSequence(0)
    @UsingDataSet("datasets/dbUnit_probe_import.json")
    @DataSource("java:jboss/lada-land")
    @Cleanup(phase=TestExecutionPhase.AFTER,
        strategy=CleanupStrategy.USED_TABLES_ONLY)
    public final void identifyProbeByHPNrMST() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("import");
        protocol.setType("identify probe");
        protocol.addInfo("import", "Compare and find Probe by HP-Nr. and MST, Update");

        Probe probe = new Probe();
        probe.setHauptprobenNr("120510002");
        probe.setMstId("06010");

        Identified found = probeIdentifier.find(probe);
        Assert.assertEquals(Identified.UPDATE, found);
        protocol.setPassed(true);
        testProtocol.add(protocol);
    }

    @Test
    @ApplyScriptBefore("datasets/clean_and_seed.sql")
    @InSequence(1)
    @UsingDataSet("datasets/dbUnit_probe_import.json")
    @DataSource("java:jboss/lada-land")
    @Cleanup(phase=TestExecutionPhase.AFTER,
        strategy=CleanupStrategy.USED_TABLES_ONLY)
    public final void identifyProbeByHPNrMSTNew() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("import");
        protocol.setType("identify probe");
        protocol.addInfo("import", "Compare and find Probe by HP-Nr. and MST, New");

        Probe probe = new Probe();
        probe.setHauptprobenNr("120510003");
        probe.setMstId("06010");

        Identified found = probeIdentifier.find(probe);
        Assert.assertEquals(Identified.NEW, found);
        protocol.setPassed(true);
        testProtocol.add(protocol);
    }

    @Test
    @ApplyScriptBefore("datasets/clean_and_seed.sql")
    @InSequence(2)
    @UsingDataSet("datasets/dbUnit_probe_import.json")
    @DataSource("java:jboss/lada-land")
    @Cleanup(phase=TestExecutionPhase.AFTER,
        strategy=CleanupStrategy.USED_TABLES_ONLY)
    public final void identifyProbeByExterneProbeId() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("import");
        protocol.setType("identify probe");
        protocol.addInfo("import", "Compare and find Probe by externeProbeId, Update");

        Probe probe = new Probe();
        probe.setExterneProbeId("T001");

        Identified found = probeIdentifier.find(probe);
        Assert.assertEquals(Identified.UPDATE, found);
        protocol.setPassed(true);
        testProtocol.add(protocol);
    }

    @Test
    @ApplyScriptBefore("datasets/clean_and_seed.sql")
    @InSequence(3)
    @UsingDataSet("datasets/dbUnit_probe_import.json")
    @DataSource("java:jboss/lada-land")
    @Cleanup(phase=TestExecutionPhase.AFTER,
        strategy=CleanupStrategy.USED_TABLES_ONLY)
    public final void identifyProbeByExterneProbeIdNew() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("import");
        protocol.setType("identify probe");
        protocol.addInfo("import", "Compare and find Probe by externeProbeId, New");

        Probe probe = new Probe();
        probe.setExterneProbeId("T002");

        Identified found = probeIdentifier.find(probe);
        Assert.assertEquals(Identified.NEW, found);
        protocol.setPassed(true);
        testProtocol.add(protocol);
    }

    @Test
    @ApplyScriptBefore("datasets/clean_and_seed.sql")
    @InSequence(4)
    @UsingDataSet("datasets/dbUnit_probe_import.json")
    @DataSource("java:jboss/lada-land")
    @Cleanup(phase=TestExecutionPhase.AFTER,
        strategy=CleanupStrategy.USED_TABLES_ONLY)
    public final void identifyProbeByExterneProbeIdReject() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("import");
        protocol.setType("identify probe");
        protocol.addInfo("import", "Compare and find Probe by externeProbeId, Reject");

        Probe probe = new Probe();
        probe.setExterneProbeId("T001");
        probe.setHauptprobenNr("120510003");
        probe.setMstId("06010");

        Identified found = probeIdentifier.find(probe);
        Assert.assertEquals(Identified.REJECT, found);
        protocol.setPassed(true);
        testProtocol.add(protocol);
    }

    @Test
    @ApplyScriptBefore("datasets/clean_and_seed.sql")
    @InSequence(5)
    @UsingDataSet("datasets/dbUnit_probe_import.json")
    @DataSource("java:jboss/lada-land")
    @Cleanup(phase=TestExecutionPhase.AFTER,
        strategy=CleanupStrategy.USED_TABLES_ONLY)
    public final void identifyProbeByExterneProbeIdUpdate() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("import");
        protocol.setType("identify probe");
        protocol.addInfo("import", "Compare and find Probe by externeProbeId, Update");

        Probe probe = new Probe();
        probe.setExterneProbeId("T001");
        probe.setHauptprobenNr("");
        probe.setMstId("06010");

        Identified found = probeIdentifier.find(probe);
        Assert.assertEquals(Identified.UPDATE, found);
        protocol.setPassed(true);
        testProtocol.add(protocol);
    }

    /**
     * MESSUNG IDENTIFIER
     */
    @Test
    @ApplyScriptBefore("datasets/clean_and_seed.sql")
    @InSequence(6)
    @UsingDataSet("datasets/dbUnit_messung_import.json")
    @DataSource("java:jboss/lada-land")
    @Cleanup(phase=TestExecutionPhase.AFTER,
        strategy=CleanupStrategy.USED_TABLES_ONLY)
    public final void identifyMessungByNpNr() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("import");
        protocol.setType("identify messung");
        protocol.addInfo("import", "Compare and find Messung by NP-Nr., Update");

        Messung messung = new Messung();
        messung.setProbeId(1000);
        messung.setNebenprobenNr("06A0");

        Identified found = messungIdentifier.find(messung);
        Assert.assertEquals(Identified.UPDATE, found);
        protocol.setPassed(true);
        testProtocol.add(protocol);
    }

    @Test
    @ApplyScriptBefore("datasets/clean_and_seed.sql")
    @InSequence(7)
    @UsingDataSet("datasets/dbUnit_messung_import.json")
    @DataSource("java:jboss/lada-land")
    @Cleanup(phase=TestExecutionPhase.AFTER,
        strategy=CleanupStrategy.USED_TABLES_ONLY)
    public final void identifyMessungByNpNrNew() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("import");
        protocol.setType("identify messung");
        protocol.addInfo("import", "Compare and find Messung by NP-Nr., New");

        Messung messung = new Messung();
        messung.setProbeId(1000);
        messung.setNebenprobenNr("06A1");

        Identified found = messungIdentifier.find(messung);
        Assert.assertEquals(Identified.NEW, found);
        protocol.setPassed(true);
        testProtocol.add(protocol);
    }

    @Test
    @ApplyScriptBefore("datasets/clean_and_seed.sql")
    @InSequence(8)
    @UsingDataSet("datasets/dbUnit_messung_import.json")
    @DataSource("java:jboss/lada-land")
    @Cleanup(phase=TestExecutionPhase.AFTER,
        strategy=CleanupStrategy.USED_TABLES_ONLY)
    public final void identifyMessungByExterneMessungsId() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("import");
        protocol.setType("identify messung");
        protocol.addInfo("import", "Compare and find Messung by externeMessungsId, Update");

        Messung messung = new Messung();
        messung.setProbeId(1000);
        messung.setExterneMessungsId(1);

        Identified found = messungIdentifier.find(messung);
        Assert.assertEquals(Identified.UPDATE, found);
        protocol.setPassed(true);
        testProtocol.add(protocol);
    }

    @Test
    @ApplyScriptBefore("datasets/clean_and_seed.sql")
    @InSequence(9)
    @UsingDataSet("datasets/dbUnit_messung_import.json")
    @DataSource("java:jboss/lada-land")
    @Cleanup(phase=TestExecutionPhase.AFTER,
        strategy=CleanupStrategy.USED_TABLES_ONLY)
    public final void identifyMessungByExterneMessungsIdNew() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("import");
        protocol.setType("identify messung");
        protocol.addInfo("import", "Compare and find Messung by externeMessungsId, New");

        Messung messung = new Messung();
        messung.setProbeId(1000);
        messung.setExterneMessungsId(2);

        Identified found = messungIdentifier.find(messung);
        Assert.assertEquals(Identified.NEW, found);
        protocol.setPassed(true);
        testProtocol.add(protocol);
    }

    //TODO: Reject with this data should not be possible
    @Test
    @ApplyScriptBefore("datasets/clean_and_seed.sql")
    @Ignore
    @InSequence(10)
    @DataSource("java:jboss/lada-land")
    @Cleanup(phase=TestExecutionPhase.AFTER,
        strategy=CleanupStrategy.USED_TABLES_ONLY)
    public final void identifyMessungByExterneMessungsIdReject() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("import");
        protocol.setType("identify messung");
        protocol.addInfo("import", "Compare and find Messung by externeMessungsId, Reject");

        Messung messung = new Messung();
        messung.setProbeId(1000);
        messung.setExterneMessungsId(1);
        messung.setNebenprobenNr("06A2");

        Identified found = messungIdentifier.find(messung);
        Assert.assertEquals(Identified.REJECT, found);
        protocol.setPassed(true);
        testProtocol.add(protocol);
    }

    @Test
    @ApplyScriptBefore("datasets/clean_and_seed.sql")
    @InSequence(11)
    @UsingDataSet("datasets/dbUnit_messung_import.json")
    @DataSource("java:jboss/lada-land")
    @Cleanup(phase=TestExecutionPhase.AFTER,
        strategy=CleanupStrategy.USED_TABLES_ONLY)
    public final void identifyMessungByExterneMessungsIdUpdate() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("import");
        protocol.setType("identify messung");
        protocol.addInfo("import", "Compare and find Messung by externeMessungsId, Update");

        Messung messung = new Messung();
        messung.setProbeId(1000);
        messung.setExterneMessungsId(1);
        messung.setNebenprobenNr("");

        Identified found = messungIdentifier.find(messung);
        Assert.assertEquals(Identified.UPDATE, found);
        protocol.setPassed(true);
        testProtocol.add(protocol);
    }

    /**
     * MERGE OBJECTS
     */
    @Test
    @ApplyScriptBefore("datasets/clean_and_seed.sql")
    @InSequence(12)
    @UsingDataSet("datasets/dbUnit_import_merge.json")
    @ShouldMatchDataSet(value="datasets/dbUnit_import_merge_match.json",
        excludeColumns={"letzte_aenderung", "tree_modified"})
    @DataSource("java:jboss/lada-land")
    @Cleanup(phase=TestExecutionPhase.AFTER,
        strategy=CleanupStrategy.USED_TABLES_ONLY)
    public final void mergeProbe() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("import");
        protocol.setType("merge probe");
        protocol.addInfo("import", "Merge objects");

        Probe probe = new Probe();
        probe.setExterneProbeId("T001");
        probe.setHauptprobenNr("120510002");
        probe.setMstId("06010");
        probe.setBaId(1);
        probe.setDatenbasisId(9);
        probe.setMedia("Trinkwasser Zentralversorgung Oberflächenwasser aufbereitet");
        probe.setMediaDesk("D: 59 04 01 00 05 05 01 02 00 00 00 00");
        probe.setMprId(1000);
        probe.setProbeNehmerId(726);
        probe.setTest(false);
        probe.setLaborMstId("06010");
        probe.setProbenartId(2);
        probe.setUmwId("A6");
        probe.setSolldatumBeginn(Timestamp.valueOf("2013-05-01 16:00:00"));
        probe.setSolldatumEnde(Timestamp.valueOf("2013-05-05 16:00:00"));
        probe.setProbeentnahmeBeginn(Timestamp.valueOf("2012-05-03 13:07:00"));
        Probe dbProbe = repository.getByIdPlain(Probe.class, 1000, "land");
        merger.merge(dbProbe, probe);

        protocol.setPassed(true);
        testProtocol.add(protocol);
    }

    @Test
    @ApplyScriptBefore("datasets/clean_and_seed.sql")
    @InSequence(13)
    @UsingDataSet("datasets/dbUnit_import_merge.json")
    @ShouldMatchDataSet(value="datasets/dbUnit_import_merge_match_messung.json",
        excludeColumns={"letzte_aenderung", "tree_modified"})
    @DataSource("java:jboss/lada-land")
    @Cleanup(phase=TestExecutionPhase.AFTER,
        strategy=CleanupStrategy.USED_TABLES_ONLY)
    public final void mergeMessung() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("import");
        protocol.setType("merge messung");
        protocol.addInfo("import", "Merge objects");

        Messung messung = new Messung();
        messung.setNebenprobenNr("06A0");
        messung.setGeplant(true);
        messung.setFertig(false);
        messung.setMessdauer(1000);
        messung.setMmtId("A3");
        messung.setMesszeitpunkt(Timestamp.valueOf("2012-05-06 14:00:00"));
        Messung dbMessung = repository.getByIdPlain(Messung.class, 1200, "land");
        merger.mergeMessung(dbMessung, messung);

        protocol.setPassed(true);
        testProtocol.add(protocol);
    }

    /*TODO: Record order can get mixed up here which cause the test to fail as
            different records get compared to each other (e.g. A74 <-> A76) */
    @Test
    @ApplyScriptBefore("datasets/clean_and_seed.sql")
    @Ignore
    @InSequence(14)
    @UsingDataSet("datasets/dbUnit_import_merge.json")
    @ShouldMatchDataSet(value="datasets/dbUnit_import_merge_match_zusatzwert.json",
        excludeColumns={"id", "letzte_aenderung", "tree_modified"})
    @DataSource("java:jboss/lada-land")
    @Cleanup(phase=TestExecutionPhase.AFTER,
        strategy=CleanupStrategy.USED_TABLES_ONLY)
    public final void mergeZusatzwert() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("import");
        protocol.setType("merge zusatzwert");
        protocol.addInfo("import", "Merge objects");

        Probe probe = repository.getByIdPlain(Probe.class, 1000, "land");
        List<ZusatzWert> zusatzwerte = new ArrayList<ZusatzWert>();
        ZusatzWert wert1 = new ZusatzWert();
        wert1.setProbeId(1000);
        wert1.setMessfehler(1.2f);
        wert1.setKleinerAls("<");
        wert1.setPzsId("A74");

        ZusatzWert wert2 = new ZusatzWert();
        wert2.setProbeId(1000);
        wert2.setMessfehler(0.2f);
        wert2.setMesswertPzs(1.8d);
        wert1.setKleinerAls(null);
        wert2.setPzsId("A75");

        ZusatzWert wert3 = new ZusatzWert();
        wert3.setProbeId(1000);
        wert3.setMessfehler(0.2f);
        wert3.setMesswertPzs(1.8d);
        wert1.setKleinerAls(null);
        wert3.setPzsId("A76");

        zusatzwerte.add(wert1);
        zusatzwerte.add(wert2);
        zusatzwerte.add(wert3);
        merger.mergeZusatzwerte(probe, zusatzwerte);

        protocol.setPassed(true);
        testProtocol.add(protocol);
    }

    @Test
    @ApplyScriptBefore("datasets/clean_and_seed.sql")
    @InSequence(15)
    @UsingDataSet("datasets/dbUnit_import_merge.json")
    @ShouldMatchDataSet(value="datasets/dbUnit_import_merge_match_kommentar.json",
        excludeColumns={"id"})
    @DataSource("java:jboss/lada-land")
    @Cleanup(phase=TestExecutionPhase.AFTER,
        strategy=CleanupStrategy.USED_TABLES_ONLY)
    public final void mergeProbeKommentar() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("import");
        protocol.setType("merge probe kommentar");
        protocol.addInfo("import", "Merge objects");

        Probe probe = repository.getByIdPlain(Probe.class, 1000, "land");
        List<KommentarP> kommentare = new ArrayList<KommentarP>();
        KommentarP komm1 = new KommentarP();
        komm1.setProbeId(1000);
        komm1.setDatum(Timestamp.valueOf("2012-05-08 12:00:00"));
        komm1.setMstId("06010");
        komm1.setText("Testtext2");

        KommentarP komm2 = new KommentarP();
        komm2.setProbeId(1000);
        komm2.setDatum(Timestamp.valueOf("2012-04-08 12:00:00"));
        komm2.setMstId("06010");
        komm2.setText("Testtext3");

        kommentare.add(komm1);
        kommentare.add(komm2);

        merger.mergeKommentare(probe, kommentare);
        Assert.assertEquals(2, kommentare.size());

        protocol.setPassed(true);
        testProtocol.add(protocol);
    }

    @Test
    @ApplyScriptBefore("datasets/clean_and_seed.sql")
    @InSequence(16)
    @UsingDataSet("datasets/dbUnit_import_merge.json")
    @ShouldMatchDataSet(value="datasets/dbUnit_import_merge_match_kommentarm.json",
        excludeColumns={"id"})
    @DataSource("java:jboss/lada-land")
    @Cleanup(phase=TestExecutionPhase.AFTER,
        strategy=CleanupStrategy.USED_TABLES_ONLY)
    public final void mergeMessungKommentar() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("import");
        protocol.setType("merge messung kommentar");
        protocol.addInfo("import", "Merge objects");

        Messung messung = repository.getByIdPlain(Messung.class, 1200, "land");
        List<KommentarM> kommentare = new ArrayList<KommentarM>();
        KommentarM komm1 = new KommentarM();
        komm1.setMessungsId(1200);
        komm1.setDatum(Timestamp.valueOf("2012-05-08 12:00:00"));
        komm1.setMstId("06010");
        komm1.setText("Testtext2");

        KommentarM komm2 = new KommentarM();
        komm2.setMessungsId(1200);
        komm2.setDatum(Timestamp.valueOf("2012-03-08 12:00:00"));
        komm2.setMstId("06010");
        komm2.setText("Testtext3");

        kommentare.add(komm1);
        kommentare.add(komm2);

        merger.mergeMessungKommentare(messung, kommentare);
        Assert.assertEquals(2, kommentare.size());

        protocol.setPassed(true);
        testProtocol.add(protocol);
    }

    @Test
    @ApplyScriptBefore("datasets/clean_and_seed.sql")
    @InSequence(17)
    @UsingDataSet("datasets/dbUnit_import_merge.json")
    @ShouldMatchDataSet(value="datasets/dbUnit_import_merge_match_messwert.json",
        excludeColumns={"id"})
    @DataSource("java:jboss/lada-land")
    @Cleanup(phase=TestExecutionPhase.AFTER,
        strategy=CleanupStrategy.USED_TABLES_ONLY)
    public final void mergeMesswerte() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("import");
        protocol.setType("merge messwerte");
        protocol.addInfo("import", "Merge objects");

        Messung messung = repository.getByIdPlain(Messung.class, 1200, "land");
        List<Messwert> messwerte = new ArrayList<Messwert>();
        Messwert wert1 = new Messwert();
        wert1.setMessungsId(1200);
        wert1.setMehId(207);
        wert1.setMessgroesseId(56);
        wert1.setMesswert(1.5d);
        messwerte.add(wert1);

        merger.mergeMesswerte(messung, messwerte);
        QueryBuilder<Messwert> builder = new QueryBuilder<Messwert>(
            repository.entityManager("land"),
            Messwert.class
        );
        builder.and("messungsId", messung.getId());
        List<Messwert> dbWerte =
            repository.filterPlain(builder.getQuery(), "land");
        Assert.assertEquals(1, dbWerte.size());

        protocol.setPassed(true);
        testProtocol.add(protocol);
    }
}
