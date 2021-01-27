/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada;

import static de.intevation.lada.BaseTest.archiveName;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.intevation.lada.test.validator.MessungTest;
import de.intevation.lada.test.validator.ProbeTest;
import de.intevation.lada.test.validator.StatusTest;
import de.intevation.lada.validation.Validator;
import de.intevation.lada.validation.annotation.ValidationConfig;

/**
 * Test validators.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@RunWith(Arquillian.class)
@Ignore
// TODO make tests independent of test data which do not exist anymore
public class ValidatorTest {

    private static Logger logger = Logger.getLogger(StammdatenTest.class);

    /**
     * Test protocol for output of results.
     */
    protected static List<Protocol> testProtocol;

    /**
     * Enables verbose logging.
     */
    protected static boolean verboseLogging = false;

    @Inject
    @ValidationConfig(type = "Probe")
    private Validator probeValidator;
    private ProbeTest probeTest;

    @Inject
    @ValidationConfig(type = "Messung")
    private Validator messungValidator;
    private MessungTest messungTest;

    @Inject
    @ValidationConfig(type = "Status")
    private Validator statusValidator;
    private StatusTest statusTest;


    public ValidatorTest() {
        probeTest = new ProbeTest();
        messungTest = new MessungTest();
        statusTest = new StatusTest();
        testProtocol = new ArrayList<Protocol>();
    }

    /**
     * Create a deployable WAR archive.
     * @throws Exception that can occur during the test.
     * @return WebArchive to deploy in wildfly application server.
     */
    @Deployment(testable = true)
    public static WebArchive createDeployment() throws Exception {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, archiveName)
            .addPackages(true, Package.getPackage("de.intevation.lada"))
            .addAsResource("log4j.properties", "log4j.properties")
            .addAsResource("shibboleth.properties", "shibboleth.properties")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsResource("META-INF/test-persistence.xml",
                "META-INF/persistence.xml");
        return archive;
    }


    /**
     * Output  for current test run.
     */
    @BeforeClass
    public static void beforeTests() {
        logger.info("---------- Testing Lada Validator ----------");
    }

    /**
     * Output of the current test results from protocol.
     */
    @After
    public final void printLogs() {
        for (Protocol p : testProtocol) {
            logger.info(p.toString(verboseLogging));
        }
    }

    /**
     * Output for formatting.
     */
    @AfterClass
    public static final void afterTests() {
        System.out.println("");
    }

    /**
     * Test hauptprobennr.
     */
    @Test
    public final void probeHasHauptprobenNr() {
        probeTest.setValidator(probeValidator);
        probeTest.hasHauptprobenNr(testProtocol);
    }

    /**
     * Test hauptprobennr missing.
     */
    @Test
    public final void probeHasNoHauptprobenNr() {
        probeTest.setValidator(probeValidator);
        probeTest.hasNoHauptprobenNr(testProtocol);
    }

    /**
     * Test existing hauptprobennr new.
     */
    @Test
    public final void probeExistingHauptprobenNrNew() {
        probeTest.setValidator(probeValidator);
        probeTest.existingHauptprobenNrNew(testProtocol);
    }

    /**
     * Test unique hauptprobennr new.
     */
    @Test
    public final void probeUniqueHauptprobenNrNew() {
        probeTest.setValidator(probeValidator);
        probeTest.uniqueHauptprobenNrNew(testProtocol);
    }

    /**
     * Test existing hauptprobennr update.
     */
    @Test
    public final void probeExistingHauptprobenNrUpdate() {
        probeTest.setValidator(probeValidator);
        probeTest.existingHauptprobenNrUpdate(testProtocol);
    }

    /**
     * Test unique hauptprobennr update.
     */
    @Test
    public final void probeUniqueHauptprobenNrUpdate() {
        probeTest.setValidator(probeValidator);
        probeTest.uniqueHauptprobenNrUpdate(testProtocol);
    }

    /**
     * Test probe has entnahmeort.
     */
    @Test
    public final void probeHasEntnahmeOrt() {
        probeTest.setValidator(probeValidator);
        probeTest.hasEntnahmeOrt(testProtocol);
    }

    /**
     * Test probe has no entnahmeort.
     */
    @Test
    public final void probeHasNoEntnahmeOrt() {
        probeTest.setValidator(probeValidator);
        probeTest.hasNoEntnahmeOrt(testProtocol);
    }

    /**
     * Test probe has probenahmebegin.
     */
    @Test
    public final void probeHasProbenahmeBegin() {
        probeTest.setValidator(probeValidator);
        probeTest.hasProbeentnahmeBegin(testProtocol);
    }

    /**
     * Test probe has no probenahmebegin.
     */
    @Test
    public final void probeHasNoProbenahmeBegin() {
        probeTest.setValidator(probeValidator);
        probeTest.hasNoProbeentnahmeBegin(testProtocol);
    }

    /**
     * Test probe has no time end probenahmebegin.
     */
    @Test
    public final void probeTimeNoEndProbenahmeBegin() {
        probeTest.setValidator(probeValidator);
        probeTest.timeNoEndProbeentnahmeBegin(testProtocol);
    }

    /**
     * Test probe has no time begin probenahmebegin.
     */
    @Test
    public final void probeTimeNoBeginProbenahmeBegin() {
        probeTest.setValidator(probeValidator);
        probeTest.timeNoBeginProbeentnahmeBegin(testProtocol);
    }

    /**
     * Test probe time begin after end probenahmebegin.
     */
    @Test
    public final void probeTimeBeginAfterEndProbenahmeBegin() {
        probeTest.setValidator(probeValidator);
        probeTest.timeBeginAfterEndProbeentnahmeBegin(testProtocol);
    }

    /**
     * Test probe begin in future probenahmebegin.
     */
    @Test
    public final void probeTimeBeginFutureProbenahmeBegin() {
        probeTest.setValidator(probeValidator);
        probeTest.timeBeginFutureProbeentnahmeBegin(testProtocol);
    }

    /**
     * Test probe has umwelt.
     */
    @Test
    public final void probeHasUmwelt() {
        probeTest.setValidator(probeValidator);
        probeTest.hasUmwelt(testProtocol);
    }

    /**
     * Test probe has no umwelt.
     */
    @Test
    public final void probeHasNoUmwelt() {
        probeTest.setValidator(probeValidator);
        probeTest.hasNoUmwelt(testProtocol);
    }

    /**
     * Test probe has empty umwelt.
     */
    @Test
    public final void probeHasEmptyUmwelt() {
        probeTest.setValidator(probeValidator);
        probeTest.hasEmptyUmwelt(testProtocol);
    }

    /**
     * Test messung has nebenprobennr.
     */
    @Test
    public final void messungHasNebenprobenNr() {
        messungTest.setValidator(messungValidator);
        messungTest.hasNebenprobenNr(testProtocol);
    }

    /**
     * Test messung has no nebenprobennr.
     */
    @Test
    public final void messungHasNoNebenprobenNr() {
        messungTest.setValidator(messungValidator);
        messungTest.hasNoNebenprobenNr(testProtocol);
    }

    /**
     * Test messung has empty nebenprobennr.
     */
    @Test
    public final void messungHasEmptyNebenprobenNr() {
        messungTest.setValidator(messungValidator);
        messungTest.hasEmptyNebenprobenNr(testProtocol);
    }

    /**
     * Test messung has unique nebenprobennr.
     */
    @Test
    public final void messungUniqueNebenprobenNrNew() {
        messungTest.setValidator(messungValidator);
        messungTest.uniqueNebenprobenNrNew(testProtocol);
    }

    /**
     * Test messung unique nebenprobennr update.
     */
    @Test
    public final void messungUniqueNebenprobenNrUpdate() {
        messungTest.setValidator(messungValidator);
        messungTest.uniqueNebenprobenNrUpdate(testProtocol);
    }

    /**
     * Test messung existing nebenprobennr new.
     */
    @Test
    public final void messungExistingNebenprobenNrNew() {
        messungTest.setValidator(messungValidator);
        messungTest.existingNebenprobenNrNew(testProtocol);
    }

    /**
     * Test messung existing nebenprobennr update.
     */
    @Test
    public final void messungExistingNebenprobenNrUpdate() {
        messungTest.setValidator(messungValidator);
        messungTest.existingNebenprobenNrUpdate(testProtocol);
    }

    /**
     * Test messung has messwert.
     */
    @Test
    public final void messungHasMesswert() {
        messungTest.setValidator(messungValidator);
        messungTest.hasMesswert(testProtocol);
    }

    /**
     * Test messung has no messwert.
     */
    @Test
    public final void messungHasNoMesswert() {
        messungTest.setValidator(messungValidator);
        messungTest.hasNoMesswert(testProtocol);
    }

    /**
     * Test negative status kombi.
     */
    @Test
    public final void statusKombiNegative() {
        statusTest.setValidator(statusValidator);
        statusTest.checkKombiNegative(testProtocol);
    }
}
