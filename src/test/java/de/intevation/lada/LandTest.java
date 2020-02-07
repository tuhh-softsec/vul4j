/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3) 
 * and comes with ABSOLUTELY NO WARRANTY! Check out 
 * the documentation coming with IMIS-Labordaten-Application for details. 
 */
package de.intevation.lada;

import java.net.URL;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.CleanupUsingScript;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;

import de.intevation.lada.model.land.KommentarM;
import de.intevation.lada.model.land.KommentarP;
import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.Messwert;
import de.intevation.lada.model.land.Ortszuordnung;
import de.intevation.lada.model.land.Probe;
import de.intevation.lada.model.land.ZusatzWert;
import de.intevation.lada.model.land.Messprogramm;
import de.intevation.lada.test.land.KommentarMTest;
import de.intevation.lada.test.land.KommentarPTest;
import de.intevation.lada.test.land.MessprogrammTest;
import de.intevation.lada.test.land.MessungTest;
import de.intevation.lada.test.land.MesswertTest;
import de.intevation.lada.test.land.OrtszuordnungTest;
import de.intevation.lada.test.land.PepGenerationTest;
import de.intevation.lada.test.land.ProbeTest;
import de.intevation.lada.test.land.QueryTest;
import de.intevation.lada.test.land.StatusTest;
import de.intevation.lada.test.land.ZusatzwertTest;


/**
 * Class to test the Lada server 'land' services.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@RunWith(Arquillian.class)
// TODO: make tests independent of test data which do not exist anymore
public class LandTest extends BaseTest {

    private static Logger logger = Logger.getLogger(StammdatenTest.class);

    @PersistenceContext(unitName="land")
    EntityManager em;

    private ProbeTest probeTest;
    private MessungTest messungTest;
    private KommentarMTest mkommentarTest;
    private KommentarPTest pkommentarTest;
    private OrtszuordnungTest ortszuordnungTest;
    private ZusatzwertTest zusatzwertTest;
    private MesswertTest messwertTest;
    private StatusTest statusTest;
    private MessprogrammTest messprogrammTest;
    private QueryTest queryTest;
    private PepGenerationTest pepGenerationTest;

    public LandTest() {
        probeTest = new ProbeTest();
        messungTest = new MessungTest();
        mkommentarTest = new KommentarMTest();
        pkommentarTest = new KommentarPTest();
        ortszuordnungTest = new OrtszuordnungTest();
        zusatzwertTest = new ZusatzwertTest();
        messwertTest = new MesswertTest();
        statusTest = new StatusTest();
        messprogrammTest = new MessprogrammTest();
        queryTest = new QueryTest();
        pepGenerationTest = new PepGenerationTest();
        testProtocol = new ArrayList<Protocol>();
        verboseLogging = false;
    }

    @BeforeClass
    public static void beforeTests() {
        logger.info("---------- Testing Lada Land Services ----------");
    }

    /*------ REST service tests ------*/

    /**
     * Tests for probe operations
     */
    @Test
    @Ignore
    @InSequence(2)
    @RunAsClient
    public final void testProbe(@ArquillianResource URL baseUrl)
    throws Exception {
        probeTest.init(baseUrl, testProtocol);
        probeTest.execute();
    }

    /**
     * Tests for pkommentar operations
     */
    @Test
    @Ignore
    @InSequence(4)
    @RunAsClient
    public final void testPkommentar(@ArquillianResource URL baseUrl)
    throws Exception {
        pkommentarTest.init(baseUrl, testProtocol);
        pkommentarTest.execute();
    }

    /**
     * Tests for ortszurodnung operations
     */
    @Test
    @Ignore
    @InSequence(6)
    @RunAsClient
    public final void testOrtszuordnung(@ArquillianResource URL baseUrl)
    throws Exception {
        ortszuordnungTest.init(baseUrl, testProtocol);
        ortszuordnungTest.execute();
    }

    /**
     * Tests for zustzwert operations
     */
    @Test
    @Ignore
    @InSequence(8)
    @RunAsClient
    public final void testZusatzwert(@ArquillianResource URL baseUrl)
    throws Exception {
        zusatzwertTest.init(baseUrl, testProtocol);
        zusatzwertTest.execute();
    }
    /**
     * Tests for messung operations
     */
    @Test
    @Ignore
    @InSequence(10)
    @RunAsClient
    public final void testMessung(@ArquillianResource URL baseUrl)
    throws Exception {
        messungTest.init(baseUrl, testProtocol);
        messungTest.execute();
    }

    /**
     * Tests for mkommentar operations
     */
    @Test
    @Ignore
    @InSequence(12)
    @RunAsClient
    public final void testMkommentar(@ArquillianResource URL baseUrl)
    throws Exception {
        mkommentarTest.init(baseUrl, testProtocol);
        mkommentarTest.execute();
    }

    /**
     * Tests for mkommentar operations
     */
    @Test
    @Ignore
    @InSequence(14)
    @RunAsClient
    public final void testMesswert(@ArquillianResource URL baseUrl)
    throws Exception {
        messwertTest.init(baseUrl, testProtocol);
        messwertTest.execute();
    }

    @Test
    @Ignore
    @InSequence(16)
    @RunAsClient
    public final void testStatus(@ArquillianResource URL baseUrl)
    throws Exception {
        statusTest.init(baseUrl, testProtocol);
        statusTest.execute();
    }

    @Test
    @Ignore
    @InSequence(18)
    @RunAsClient
    public final void testMessprogramm(@ArquillianResource URL baseUrl)
    throws Exception {
        messprogrammTest.init(baseUrl, testProtocol);
        messprogrammTest.execute();
    }

    @Test
    @Ignore
    @InSequence(20)
    @RunAsClient
    public final void testQuery(@ArquillianResource URL baseUrl)
    throws Exception {
        queryTest.init(baseUrl, testProtocol);
        queryTest.execute();
    }

    /*------ Database operations ------*/

    /**
     * Insert a probe object into the database.
     */
    @Test
    @Ignore
    @InSequence(1)
    @UsingDataSet("datasets/dbUnit_probe.json")
    @DataSource("java:jboss/lada-land")
    @Cleanup(phase=TestExecutionPhase.NONE)
    public final void prepareDatabaseProbe() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("database");
        protocol.setType("insert probe");
        protocol.addInfo("database", "Insert Probe into database");
        testProtocol.add(protocol);
        Probe probe = em.find(Probe.class, 1000);
        Assert.assertNotNull(probe);
        protocol.setPassed(true);
    }

    /**
     * Insert a probe kommentar into the database.
     */
    @Test
    @Ignore
    @InSequence(3)
    @UsingDataSet("datasets/dbUnit_pkommentar.json")
    @DataSource("java:jboss/lada-land")
    @Cleanup(phase=TestExecutionPhase.NONE)
    public final void prepareDatabaseKommentarP() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("database");
        protocol.setType("insert kommentar_p");
        protocol.addInfo("database", "Insert KommentarP into database");
        testProtocol.add(protocol);
        KommentarP kommentar = em.find(KommentarP.class, 1000);
        Assert.assertNotNull(kommentar);
        protocol.setPassed(true);
    }

    /**
     * Insert a ortszuordnung into the database.
     */
    @Test
    @Ignore
    @InSequence(5)
    @UsingDataSet("datasets/dbUnit_ortszuordnung.json")
    @DataSource("java:jboss/lada-land")
    @Cleanup(phase=TestExecutionPhase.NONE)
    public final void prepareDatabaseOrtszuordnung() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("database");
        protocol.setType("insert ortszuordnung");
        protocol.addInfo("database", "Insert Ortszuordnung into database");
        testProtocol.add(protocol);
        Ortszuordnung ortszuordnung = em.find(Ortszuordnung.class, 1000);
        Assert.assertNotNull(ortszuordnung);
        protocol.setPassed(true);
    }

    /**
     * Insert a zusatzwert into the database.
     */
    @Test
    @Ignore
    @InSequence(7)
    @UsingDataSet("datasets/dbUnit_zusatzwert.json")
    @DataSource("java:jboss/lada-land")
    @Cleanup(phase=TestExecutionPhase.NONE)
    public final void prepareDatabaseZusatzwert() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("database");
        protocol.setType("insert zusatzwert");
        protocol.addInfo("database", "Insert Zusatzwert into database");
        testProtocol.add(protocol);
        ZusatzWert zusatzwert = em.find(ZusatzWert.class, 1000);
        Assert.assertNotNull(zusatzwert);
        protocol.setPassed(true);
    }

    /**
     * Insert a messung object into the database.
     */
    @Test
    @Ignore
    @InSequence(9)
    @UsingDataSet("datasets/dbUnit_messung.json")
    @DataSource("java:jboss/lada-land")
    @Cleanup(phase=TestExecutionPhase.NONE)
    public final void prepareDatabaseMessung() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("database");
        protocol.setType("insert messung");
        protocol.addInfo("database", "Insert Messung into database");
        testProtocol.add(protocol);
        Messung messung = em.find(Messung.class, 1200);
        messung.setStatus(1000);
        em.merge(messung);
        Assert.assertNotNull(messung);
        protocol.setPassed(true);
    }

    /**
     * Insert a messungs kommentar into the database.
     */
    @Test
    @Ignore
    @InSequence(11)
    @UsingDataSet("datasets/dbUnit_mkommentar.json")
    @DataSource("java:jboss/lada-land")
    @Cleanup(phase=TestExecutionPhase.NONE)
    public final void prepareDatabaseKommentarM() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("database");
        protocol.setType("insert kommentar_m");
        protocol.addInfo("database", "Insert KommentarM into database");
        testProtocol.add(protocol);
        KommentarM kommentar = em.find(KommentarM.class, 1000);
        Assert.assertNotNull(kommentar);
        protocol.setPassed(true);
    }

    /**
     * Insert a messwert into the database.
     */
    @Test
    @Ignore
    @InSequence(13)
    @UsingDataSet("datasets/dbUnit_messwert.json")
    @DataSource("java:jboss/lada-land")
    @Cleanup(phase=TestExecutionPhase.NONE)
    public final void prepareDatabaseMesswert() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("database");
        protocol.setType("insert messwert");
        protocol.addInfo("database", "Insert Messwert into database");
        testProtocol.add(protocol);
        Messwert messwert = em.find(Messwert.class, 10000);
        Assert.assertNotNull(messwert);
        protocol.setPassed(true);
    }

    /**
     * Insert a messprogramm into the database.
     */
    @Test
    @Ignore
    @InSequence(17)
    @UsingDataSet("datasets/dbUnit_messprogramm.json")
    @DataSource("java:jboss/lada-land")
    @Cleanup(phase=TestExecutionPhase.NONE)
    public final void prepareDatabaseMessprogramm() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("database");
        protocol.setType("insert messprogramm");
        protocol.addInfo("database", "Insert Messprogramm into database");
        testProtocol.add(protocol);
        Messprogramm messprogramm = em.find(Messprogramm.class, 1000);
        Assert.assertNotNull(messprogramm);
        protocol.setPassed(true);
    }
    /**
     * Cleanup the database.
     */
    // @Test
    // @Ignore
    // @InSequence(19)
    // @DataSource("java:jboss/lada-land")
    // @CleanupUsingScript("datasets/cleanup.sql")
    // public final void cleanUp() {
    //     Protocol protocol = new Protocol();
    //     protocol.addInfo("database", "Cleaning the database.");
    //     protocol.setName("database");
    //     protocol.setType("cleanup");
    //     protocol.setPassed(true);
    //     testProtocol.add(protocol);
    // }

    /**
     * Test probe generation from a messprogramm record
     */
    @Test
    @ApplyScriptBefore("datasets/clean_and_seed.sql")
    @UsingDataSet("datasets/dbUnit_pep_gen.json")
    @DataSource("java:jboss/lada-land")
    @InSequence(21)
    @Cleanup(phase=TestExecutionPhase.NONE)
    public final void prepareTestPepGeneration() {
        Protocol protocol = new Protocol();
        protocol.setName("database");
        protocol.setType("insert messprogramm");
        protocol.addInfo("database", "Insert Messprogramm into database");
        testProtocol.add(protocol);
        Messprogramm messprogramm = em.find(Messprogramm.class, 1000);
        Assert.assertNotNull(messprogramm);
        protocol.setPassed(true);
    }

    @Test
    @DataSource("java:jboss/lada-land")
    @InSequence(22)
    @RunAsClient
    public final void testPepGeneration(@ArquillianResource URL baseUrl)
            throws Exception {
        pepGenerationTest.init(baseUrl, testProtocol);
        pepGenerationTest.execute();
    }
}
