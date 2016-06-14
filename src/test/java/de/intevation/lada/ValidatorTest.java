/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada;

import static de.intevation.lada.BaseTest.ARCHIVE_NAME;

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
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;

import de.intevation.lada.test.validator.Messung;
import de.intevation.lada.test.validator.Probe;
import de.intevation.lada.test.validator.Status;
import de.intevation.lada.validation.Validator;
import de.intevation.lada.validation.annotation.ValidationConfig;

@RunWith(Arquillian.class)
@Ignore
// TODO: make tests independent of test data which do not exist anymore
public class ValidatorTest {

    private static Logger logger = Logger.getLogger(StammdatenTest.class);

    protected static List<Protocol> testProtocol;

    protected static boolean verboseLogging = false;

    @Inject
    @ValidationConfig(type="Probe")
    private Validator probeValidator;
    private Probe probeTest;

    @Inject
    @ValidationConfig(type="Messung")
    private Validator messungValidator;
    private Messung messungTest;

    @Inject
    @ValidationConfig(type="Status")
    private Validator statusValidator;
    private Status statusTest;


    public ValidatorTest() {
        probeTest = new Probe();
        messungTest = new Messung();
        statusTest = new Status();
        testProtocol = new ArrayList<Protocol>();
    }

    /**
     * Create a deployable WAR archive.
     */
    @Deployment(testable=true)
    public static WebArchive createDeployment() throws Exception {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, ARCHIVE_NAME)
            .addPackages(true, Package.getPackage("de.intevation.lada"))
            .addAsResource("log4j.properties", "log4j.properties")
            .addAsResource("shibboleth.properties", "shibboleth.properties")
            .addAsResource("probequery.json", "probequery.json")
            .addAsResource("messprogrammquery.json", "messprogrammquery.json")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsResource("META-INF/test-persistence.xml",
                "META-INF/persistence.xml");
        return archive;
    }


    @BeforeClass
    public static void beforeTests() {
        logger.info("---------- Testing Lada Validator ----------");
    }

    @After
    public final void printLogs() {
        for (Protocol p : testProtocol) {
            logger.info(p.toString(verboseLogging));
        }
    }

    @AfterClass
    public static final void afterTests() {
        System.out.println("");
    }

    @Test
    public final void probeHasHauptprobenNr() {
        probeTest.setValidator(probeValidator);
        probeTest.hasHauptprobenNr(testProtocol);
    }

    @Test
    public final void probeHasNoHauptprobenNr() {
        probeTest.setValidator(probeValidator);
        probeTest.hasNoHauptprobenNr(testProtocol);
    }

    @Test
    public final void probeExistingHauptprobenNrNew() {
        probeTest.setValidator(probeValidator);
        probeTest.existingHauptprobenNrNew(testProtocol);
    }

    @Test
    public final void probeUniqueHauptprobenNrNew() {
        probeTest.setValidator(probeValidator);
        probeTest.uniqueHauptprobenNrNew(testProtocol);
    }

    @Test
    public final void probeExistingHauptprobenNrUpdate() {
        probeTest.setValidator(probeValidator);
        probeTest.existingHauptprobenNrUpdate(testProtocol);
    }

    @Test
    public final void probeUniqueHauptprobenNrUpdate() {
        probeTest.setValidator(probeValidator);
        probeTest.uniqueHauptprobenNrUpdate(testProtocol);
    }

    @Test
    public final void probeHasEntnahmeOrt() {
        probeTest.setValidator(probeValidator);
        probeTest.hasEntnahmeOrt(testProtocol);
    }

    @Test
    public final void probeHasNoEntnahmeOrt() {
        probeTest.setValidator(probeValidator);
        probeTest.hasNoEntnahmeOrt(testProtocol);
    }

    @Test
    public final void probeHasProbenahmeBegin() {
        probeTest.setValidator(probeValidator);
        probeTest.hasProbeentnahmeBegin(testProtocol);
    }

    @Test
    public final void probeHasNoProbenahmeBegin() {
        probeTest.setValidator(probeValidator);
        probeTest.hasNoProbeentnahmeBegin(testProtocol);
    }

    @Test
    public final void probeTimeNoEndProbenahmeBegin() {
        probeTest.setValidator(probeValidator);
        probeTest.timeNoEndProbeentnahmeBegin(testProtocol);
    }

    @Test
    public final void probeTimeNoBeginProbenahmeBegin() {
        probeTest.setValidator(probeValidator);
        probeTest.timeNoBeginProbeentnahmeBegin(testProtocol);
    }

    @Test
    public final void probeTimeBeginAfterEndProbenahmeBegin() {
        probeTest.setValidator(probeValidator);
        probeTest.timeBeginAfterEndProbeentnahmeBegin(testProtocol);
    }

    @Test
    public final void probeTimeBeginFutureProbenahmeBegin() {
        probeTest.setValidator(probeValidator);
        probeTest.timeBeginFutureProbeentnahmeBegin(testProtocol);
    }

    @Test
    public final void probeHasUmwelt() {
        probeTest.setValidator(probeValidator);
        probeTest.hasUmwelt(testProtocol);
    }

    @Test
    public final void probeHasNoUmwelt() {
        probeTest.setValidator(probeValidator);
        probeTest.hasNoUmwelt(testProtocol);
    }

    @Test
    public final void probeHasEmptyUmwelt() {
        probeTest.setValidator(probeValidator);
        probeTest.hasEmptyUmwelt(testProtocol);
    }

    @Test
    public final void messungHasNebenprobenNr() {
        messungTest.setValidator(messungValidator);
        messungTest.hasNebenprobenNr(testProtocol);
    }

    @Test
    public final void messungHasNoNebenprobenNr() {
        messungTest.setValidator(messungValidator);
        messungTest.hasNoNebenprobenNr(testProtocol);
    }

    @Test
    public final void messungHasEmptyNebenprobenNr() {
        messungTest.setValidator(messungValidator);
        messungTest.hasEmptyNebenprobenNr(testProtocol);
    }

    @Test
    public final void messungUniqueNebenprobenNrNew() {
        messungTest.setValidator(messungValidator);
        messungTest.uniqueNebenprobenNrNew(testProtocol);
    }

    @Test
    public final void messungUniqueNebenprobenNrUpdate() {
        messungTest.setValidator(messungValidator);
        messungTest.uniqueNebenprobenNrUpdate(testProtocol);
    }

    @Test
    public final void messungExistingNebenprobenNrNew() {
        messungTest.setValidator(messungValidator);
        messungTest.existingNebenprobenNrNew(testProtocol);
    }

    @Test
    public final void messungExistingNebenprobenNrUpdate() {
        messungTest.setValidator(messungValidator);
        messungTest.existingHauptprobenNrUpdate(testProtocol);
    }

    @Test
    public final void messungHasMesswert() {
        messungTest.setValidator(messungValidator);
        messungTest.hasMesswert(testProtocol);
    }

    @Test
    public final void messungHasNoMesswert() {
        messungTest.setValidator(messungValidator);
        messungTest.hasNoMesswert(testProtocol);
    }

    @Test
    public final void statusKombiNegative() {
        statusTest.setValidator(statusValidator);
        statusTest.checkKombiNegative(testProtocol);
    }
}
