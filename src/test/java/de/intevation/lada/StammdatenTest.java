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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.Cleanup;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.intevation.lada.model.stammdaten.DatensatzErzeuger;
import de.intevation.lada.model.stammdaten.Deskriptoren;
import de.intevation.lada.model.stammdaten.MessprogrammKategorie;
import de.intevation.lada.model.stammdaten.Ort;
import de.intevation.lada.model.stammdaten.Probenehmer;
import de.intevation.lada.test.stamm.DatensatzErzeugerTest;
import de.intevation.lada.test.stamm.DeskriptorenTest;
import de.intevation.lada.test.stamm.MessprogrammKategorieTest;
import de.intevation.lada.test.stamm.OrtTest;
import de.intevation.lada.test.stamm.ProbenehmerTest;
import de.intevation.lada.test.stamm.Stammdaten;


/**
 * Class to test the Lada server stammdaten services.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@RunWith(Arquillian.class)
public class StammdatenTest extends BaseTest {

    private static final int T1 = 1;
    private static final int T2 = 2;
    private static final int T3 = 3;
    private static final int T4 = 4;
    private static final int T5 = 5;
    private static final int T6 = 6;
    private static final int T7 = 7;
    private static final int T8 = 8;
    private static final int T9 = 9;
    private static final int T10 = 10;
    private static final int T11 = 11;
    private static final int T12 = 12;
    private static final int T13 = 13;
    private static final int T14 = 14;
    private static final int T15 = 15;
    private static final int T16 = 16;
    private static final int T17 = 17;
    private static final int T18 = 18;
    private static final int T19 = 19;
    private static final int T20 = 20;
    private static final int T21 = 21;
    private static final int T22 = 22;
    private static final int T23 = 23;
    private static final int T24 = 24;
    private static final int T25 = 25;
    private static final int T26 = 26;
    private static final int T27 = 27;
    private static final int T28 = 28;
    private static final int T29 = 29;
    private static final int T30 = 30;
    private static final int T31 = 31;
    private static final int T32 = 32;
    private static final int T33 = 33;
    private static final int T34 = 34;
    private static final int T35 = 35;
    private static final int T36 = 36;

    private static final int ID5 = 5;
    private static final int ID9 = 9;
    private static final int ID56 = 56;
    private static final int ID207 = 207;
    private static final int ID1000 = 1000;

    private static Logger logger = Logger.getLogger(StammdatenTest.class);

    @PersistenceContext(unitName = "stamm")
    EntityManager em;

    private Stammdaten stammdatenTest;
    private DatensatzErzeugerTest datensatzerzeugerTest;
    private ProbenehmerTest probenehmerTest;
    private MessprogrammKategorieTest messprogrammkategorieTest;
    private OrtTest ortTest;
    private DeskriptorenTest deskriptorenTest;

    public StammdatenTest() {
        stammdatenTest = new Stammdaten();
        datensatzerzeugerTest = new DatensatzErzeugerTest();
        probenehmerTest = new ProbenehmerTest();
        messprogrammkategorieTest = new MessprogrammKategorieTest();
        ortTest = new OrtTest();
        deskriptorenTest = new DeskriptorenTest();
        testProtocol = new ArrayList<Protocol>();
        verboseLogging = false;
    }

    /**
     * Output  for current test run.
     */
    @BeforeClass
    public static void beforeTests() {
        logger.info("---------- Testing Lada Stamm Services ----------");
    }

    /**
     * Insert a probe object into the database.
     * @throws Exception that can occur during the test.
     */
    @Test
    @InSequence(T1)
    @ApplyScriptBefore("datasets/clean_and_seed.sql")
    @UsingDataSet("datasets/dbUnit_datensatzerzeuger.json")
    @DataSource("java:jboss/lada-stamm-test")
    @Cleanup(phase = TestExecutionPhase.NONE)
    public final void prepareDatabaseDatensatzerzeuger() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("database");
        protocol.setType("insert datensatzerzeuger");
        protocol.addInfo("database", "Insert datensatzerzeuger into database");
        testProtocol.add(protocol);
        DatensatzErzeuger erzeuger = em.find(DatensatzErzeuger.class, ID1000);
        Assert.assertNotNull(erzeuger);
        protocol.setPassed(true);
    }

    /**
     * Tests for probe operations.
     * @param baseUrl The server url used for the request.
     * @throws Exception that can occur during the test.
     */
    @Test
    @InSequence(T2)
    @RunAsClient
    public final void testDatensatzerzeuger(@ArquillianResource URL baseUrl)
    throws Exception {
        datensatzerzeugerTest.init(baseUrl, testProtocol);
        datensatzerzeugerTest.execute();
    }

    /**
     * Insert a probe object into the database.
     * @throws Exception that can occur during the test.
     */
    @Test
    @InSequence(T3)
    @UsingDataSet("datasets/dbUnit_probenehmer.json")
    @DataSource("java:jboss/lada-stamm-test")
    @Cleanup(phase = TestExecutionPhase.NONE)
    public final void prepareDatabaseProbenehmer() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("database");
        protocol.setType("insert probenehmer");
        protocol.addInfo("database", "Insert Probenehmer into database");
        testProtocol.add(protocol);
        Probenehmer probenehmer = em.find(Probenehmer.class, ID1000);
        Assert.assertNotNull(probenehmer);
        protocol.setPassed(true);
    }

    /**
     * Tests for probe operations.
     * @param baseUrl The server url used for the request.
     * @throws Exception that can occur during the test.
     */
    @Test
    @InSequence(T4)
    @RunAsClient
    public final void testProbenehmer(@ArquillianResource URL baseUrl)
    throws Exception {
        probenehmerTest.init(baseUrl, testProtocol);
        probenehmerTest.execute();
    }

    /**
     * Insert a probe object into the database.
     * @throws Exception that can occur during the test.
     */
    @Test
    @InSequence(T5)
    @UsingDataSet("datasets/dbUnit_messprogrammkategorie.json")
    @DataSource("java:jboss/lada-stamm-test")
    @Cleanup(phase = TestExecutionPhase.NONE)
    public final void prepareDatabaseMessprogrammKategorie() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("database");
        protocol.setType("insert messprogrammkategorie");
        protocol.addInfo(
            "database",
            "Insert messprogrammkategorie into database");
        testProtocol.add(protocol);
        MessprogrammKategorie kategorie =
            em.find(MessprogrammKategorie.class, ID1000);
        Assert.assertNotNull(kategorie);
        protocol.setPassed(true);
    }

    /**
     * Tests for probe operations.
     * @param baseUrl The server url used for the request.
     * @throws Exception that can occur during the test.
     */
    @Test
    @InSequence(T6)
    @RunAsClient
    public final void testMessprogrammKategorie(@ArquillianResource URL baseUrl)
    throws Exception {
        messprogrammkategorieTest.init(baseUrl, testProtocol);
        messprogrammkategorieTest.execute();
    }

    /**
     * Insert a probe object into the database.
     * TODO Geometry field does not work using dbunit
     * @throws Exception that can occur during the test.
     */
    @Test
    @Ignore
    @InSequence(T7)
    @UsingDataSet("datasets/dbUnit_ort.json")
    @DataSource("java:jboss/lada-stamm-test")
    @Cleanup(phase = TestExecutionPhase.NONE)
    public final void prepareDatabaseOrt() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("database");
        protocol.setType("insert ort");
        protocol.addInfo("database", "Insert ortinto database");
        testProtocol.add(protocol);
        Ort ort = em.find(Ort.class, ID1000);
        Assert.assertNotNull(ort);
        protocol.setPassed(true);
    }

    /**
     * Tests for probe operations.
     * TODO Geometry field does not work using dbunit
     * @param baseUrl The server url used for the request.
     * @throws Exception that can occur during the test.
     */
    @Test
    @Ignore
    @InSequence(T8)
    @RunAsClient
    public final void testOrt(@ArquillianResource URL baseUrl)
    throws Exception {
        ortTest.init(baseUrl, testProtocol);
        ortTest.execute();
    }

    /**
     * Tests for datenbasis operations.
     * @param baseUrl The server url used for the request.
     */
    @Test
    @InSequence(T9)
    @RunAsClient
    public final void testDatenbasisAll(@ArquillianResource URL baseUrl) {
        stammdatenTest.getAll(baseUrl, "datenbasis", testProtocol);
    }

    /**
     * Tests for datenbasis by id operations.
     * @param baseUrl The server url used for the request.
     */
    @Test
    @InSequence(T10)
    @RunAsClient
    public final void testDatenbasisById(@ArquillianResource URL baseUrl) {
        stammdatenTest.getById(baseUrl, "datenbasis", ID9, testProtocol);
    }

    /**
     * Tests for messeinheit operations.
     * @param baseUrl The server url used for the request.
     */
    @Test
    @InSequence(T11)
    @RunAsClient
    public final void testMesseinheitAll(@ArquillianResource URL baseUrl) {
        stammdatenTest.getAll(baseUrl, "messeinheit", testProtocol);
    }

    /**
     * Tests for messeinheit by id operations.
     * @param baseUrl The server url used for the request.
     */
    @Test
    @InSequence(T12)
    @RunAsClient
    public final void testMesseinheitById(@ArquillianResource URL baseUrl) {
        stammdatenTest.getById(baseUrl, "messeinheit", ID207, testProtocol);
    }

    /**
     * Tests for messgroesse operations.
     * @param baseUrl The server url used for the request.
     */
    @Test
    @InSequence(T13)
    @RunAsClient
    public final void testMessgroesseAll(@ArquillianResource URL baseUrl) {
        stammdatenTest.getAll(baseUrl, "messgroesse", testProtocol);
    }

    /**
     * Tests for messgroesse by id operations.
     * @param baseUrl The server url used for the request.
     */
    @Test
    @InSequence(T14)
    @RunAsClient
    public final void testMessgroesseById(@ArquillianResource URL baseUrl) {
        stammdatenTest.getById(baseUrl, "messgroesse", ID56, testProtocol);
    }

    /**
     * Tests for messmethode operations.
     * @param baseUrl The server url used for the request.
     */
    @Test
    @InSequence(T15)
    @RunAsClient
    public final void testMessmethodeAll(@ArquillianResource URL baseUrl) {
        stammdatenTest.getAll(baseUrl, "messmethode", testProtocol);
    }

    /**
     * Tests for messmethode by id operations.
     * @param baseUrl The server url used for the request.
     */
    @Test
    @InSequence(T16)
    @RunAsClient
    public final void testMessmethodeById(@ArquillianResource URL baseUrl) {
        stammdatenTest.getById(baseUrl, "messmethode", "A3", testProtocol);
    }

    /**
     * Tests for messstelle operations.
     * @param baseUrl The server url used for the request.
     */
    @Test
    @InSequence(T17)
    @RunAsClient
    public final void testMessstelleAll(@ArquillianResource URL baseUrl) {
        stammdatenTest.getAll(baseUrl, "messstelle", testProtocol);
    }

    /**
     * Tests for messstelle by id operations.
     * @param baseUrl The server url used for the request.
     */
    @Test
    @InSequence(T18)
    @RunAsClient
    public final void testMessstelleById(@ArquillianResource URL baseUrl) {
        stammdatenTest.getById(baseUrl, "messstelle", "06010", testProtocol);
    }

    /**
     * Tests for netzbetreiber operations.
     * @param baseUrl The server url used for the request.
     */
    @Test
    @InSequence(T19)
    @RunAsClient
    public final void testNetzbetreiberAll(@ArquillianResource URL baseUrl) {
        stammdatenTest.getAll(baseUrl, "netzbetreiber", testProtocol);
    }

    /**
     * Tests for netzbetreiber by id operations.
     * @param baseUrl The server url used for the request.
     */
    @Test
    @InSequence(T20)
    @RunAsClient
    public final void testNetzbetreiberById(@ArquillianResource URL baseUrl) {
        stammdatenTest.getById(baseUrl, "netzbetreiber", "06", testProtocol);
    }

    /**
     * Tests for pflichtmessgroesse operations.
     * @param baseUrl The server url used for the request.
     */
    @Test
    @InSequence(T21)
    @RunAsClient
    public final void testPflichtmessgroesseAll(
        @ArquillianResource URL baseUrl
    ) {
        stammdatenTest.getAll(baseUrl, "pflichtmessgroesse", testProtocol);
    }

    /**
     * Tests for pflichtmessgroesse by id operations.
     * @param baseUrl The server url used for the request.
     */
    @Test
    @InSequence(T22)
    @RunAsClient
    public final void testPflichtmessgroesseById(
        @ArquillianResource URL baseUrl
    ) {
        stammdatenTest.getById(
            baseUrl,
            "pflichtmessgroesse",
            "A3",
            testProtocol);
    }

    /**
     * Tests for probeart operations.
     * @param baseUrl The server url used for the request.
     */
    @Test
    @InSequence(T23)
    @RunAsClient
    public final void testProbenartAll(@ArquillianResource URL baseUrl) {
        stammdatenTest.getAll(baseUrl, "probenart", testProtocol);
    }

    /**
     * Tests for probeart by id operations.
     * @param baseUrl The server url used for the request.
     */
    @Test
    @InSequence(T24)
    @RunAsClient
    public final void testProbenartById(@ArquillianResource URL baseUrl) {
        stammdatenTest.getById(baseUrl, "probenart", 1, testProtocol);
    }

    /**
     * Tests for probenzusatz operations.
     * @param baseUrl The server url used for the request.
     */
    @Test
    @InSequence(T25)
    @RunAsClient
    public final void testProbenzusatzAll(@ArquillianResource URL baseUrl) {
        stammdatenTest.getAll(baseUrl, "probenzusatz", testProtocol);
    }

    /**
     * Tests for probenzusatz by id operations.
     * @param baseUrl The server url used for the request.
     */
    @Test
    @InSequence(T26)
    @RunAsClient
    public final void testProbenzusatzById(@ArquillianResource URL baseUrl) {
        stammdatenTest.getById(baseUrl, "probenzusatz", "A74", testProtocol);
    }

    /**
     * Tests for koordinatenart operations.
     * @param baseUrl The server url used for the request.
     */
    @Test
    @InSequence(T27)
    @RunAsClient
    public final void testKoordinatenartAll(@ArquillianResource URL baseUrl) {
        stammdatenTest.getAll(baseUrl, "koordinatenart", testProtocol);
    }

    /**
     * Tests for koordinatenart by id operations.
     * @param baseUrl The server url used for the request.
     */
    @Test
    @InSequence(T28)
    @RunAsClient
    public final void testKoordinatenartById(@ArquillianResource URL baseUrl) {
        stammdatenTest.getById(baseUrl, "koordinatenart", ID5, testProtocol);
    }

    /**
     * Tests for staat operations.
     * @param baseUrl The server url used for the request.
     */
    @Test
    @InSequence(T29)
    @RunAsClient
    public final void testStaatAll(@ArquillianResource URL baseUrl) {
        stammdatenTest.getAll(baseUrl, "staat", testProtocol);
    }

    /**
     * Tests for staat by id operations.
     * @param baseUrl The server url used for the request.
     */
    @Test
    @InSequence(T30)
    @RunAsClient
    public final void testStaatById(@ArquillianResource URL baseUrl) {
        stammdatenTest.getById(baseUrl, "staat", 0, testProtocol);
    }

    /**
     * Tests for umwelt  operations.
     * @param baseUrl The server url used for the request.
     */
    @Test
    @InSequence(T31)
    @RunAsClient
    public final void testUmweltAll(@ArquillianResource URL baseUrl) {
        stammdatenTest.getAll(baseUrl, "umwelt", testProtocol);
    }

    /**
     * Tests for umwelt by id operations.
     * @param baseUrl The server url used for the request.
     */
    @Test
    @InSequence(T32)
    @RunAsClient
    public final void testUmweltById(@ArquillianResource URL baseUrl) {
        stammdatenTest.getById(baseUrl, "umwelt", "L6", testProtocol);
    }

    /**
     * Tests for verwaltungseinheit operations.
     * @param baseUrl The server url used for the request.
     */
    @Test
    @InSequence(T33)
    @RunAsClient
    public final void testVerwaltungseinheitAll(
        @ArquillianResource URL baseUrl
    ) {
        stammdatenTest.getAll(baseUrl, "verwaltungseinheit", testProtocol);
    }

    /**
     * Tests for verwaltungseinheit by id operations.
     * @param baseUrl The server url used for the request.
     */
    @Test
    @InSequence(T34)
    @RunAsClient
    public final void testVerwaltungseinheitById(
        @ArquillianResource URL baseUrl
    ) {
        stammdatenTest.getById(baseUrl, "verwaltungseinheit",
            "11000000", testProtocol);
    }

    /**
     * Insert deskriptoren into the database.
     * @throws Exception that can occur during the test.
     */
    @Test
    @InSequence(T35)
    @UsingDataSet("datasets/dbUnit_deskriptor.json")
    @DataSource("java:jboss/lada-stamm-test")
    @Cleanup(phase = TestExecutionPhase.NONE)
    public final void prepareDatabaseDeskriptoren() throws Exception {
        Protocol protocol = new Protocol();
        protocol.setName("database");
        protocol.setType("insert deskriptor");
        protocol.addInfo("database", "Insert deskriptor into database");
        testProtocol.add(protocol);
        Deskriptoren deskriptor = em.find(Deskriptoren.class, ID1000);
        Assert.assertNotNull(deskriptor);
        protocol.setPassed(true);
    }

    /**
     * Tests deskriptoren service.
     * @param baseUrl The server url used for the request.
     * @throws Exception that can occur during the test.
     */
    @Test
    @InSequence(T36)
    @UsingDataSet("datasets/dbUnit_pep_gen.json")
    @DataSource("java:jboss/lada-land-test")
    @RunAsClient
    public final void testDeskriptoren(@ArquillianResource URL baseUrl)
    throws Exception {
        deskriptorenTest.init(baseUrl, testProtocol);
        deskriptorenTest.execute();
    }
}
