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
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import de.intevation.lada.test.KommentarM;
import de.intevation.lada.test.KommentarP;
import de.intevation.lada.test.Messung;
import de.intevation.lada.test.Messwert;
import de.intevation.lada.test.Probe;
import de.intevation.lada.test.Query;


/**
 * Class to test the Lada server.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LadaTest {

    private static String ARCHIVE_NAME = "lada-basis-test.war";

    private static Logger logger = Logger.getLogger(LadaTest.class);

    private static boolean verboseLogging = false;

    private Probe probeTest;
    private Query queryTest;
    private Messung messungTest;
    private KommentarP kommentarPTest;
    private KommentarM kommentarMTest;
    private Messwert messwertTest;

    private static List<Protocol> testProtocol;

    public LadaTest() {
        probeTest = new Probe();
        queryTest = new Query();
        messungTest = new Messung();
        kommentarPTest = new KommentarP();
        kommentarMTest = new KommentarM();
        messwertTest = new Messwert();
        testProtocol = new ArrayList<Protocol>();
    }

    /**
     * Create a deployable WAR archive.
     */
    @Deployment(testable=true)
    public static WebArchive createDeployment() throws Exception {
        logger.info("\n\n---------- Test Protocol ----------");
        logger.info("Create and deploy: " + ARCHIVE_NAME);
        WebArchive archive = ShrinkWrap.create(WebArchive.class, ARCHIVE_NAME)
            .addPackages(true, Package.getPackage("de.intevation.lada"))
            .addAsResource("log4j.properties", "log4j.properties")
            .addAsResource("queryconf.json", "queryconf.json")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsResource("META-INF/test-persistence.xml",
                "META-INF/persistence.xml");
        return archive;
    }

    @After
    public final void printLogs() {
        for (Protocol p : testProtocol) {
            logger.info(p.toString(verboseLogging));
        }
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_ProbeGetAllServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.probeTest.getAllService(baseUrl, testProtocol);
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_ProbeGetByIdServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.probeTest.getByIdService(baseUrl, testProtocol);
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_ProbeGetByFilterServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.probeTest.filterService(baseUrl, testProtocol);
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_GetQueryServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.queryTest.getAllService(baseUrl, testProtocol);
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_MessungGetAllServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.messungTest.getAllService(baseUrl, testProtocol);
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_MessungGetByIdServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.messungTest.getByIdService(baseUrl, testProtocol);
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_MessungGetFilterServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.messungTest.filterService(baseUrl, testProtocol);
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_KommentarPGetAllServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.kommentarPTest.getAllService(baseUrl, testProtocol);
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_KommentarPGetByIdServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.kommentarPTest.getByIdService(baseUrl, testProtocol);
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_KommentarPGetFilterServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.kommentarPTest.filterService(baseUrl, testProtocol);
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_KommentarMGetAllServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.kommentarMTest.getAllService(baseUrl, testProtocol);
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_KommentarMGetByIdServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.kommentarMTest.getByIdService(baseUrl, testProtocol);
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_KommentarMGetFilterServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.kommentarMTest.filterService(baseUrl, testProtocol);
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_MesswertGetAllServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.messwertTest.getAllService(baseUrl, testProtocol);
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_MesswertGetByIdServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.messwertTest.getByIdService(baseUrl, testProtocol);
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_MesswertGetFilterServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.messwertTest.filterService(baseUrl, testProtocol);
    }

    /**
     * Testing CREATE services.
     */
    @Test
    @RunAsClient
    public final void testB_CreateServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.probeTest.createService(baseUrl, testProtocol);
        Assert.assertNotNull(this.probeTest.getCreatedProbeId());
        this.messungTest.createService(
            baseUrl,
            testProtocol,
            this.probeTest.getCreatedProbeId());
        this.kommentarPTest.createService(
            baseUrl,
            testProtocol,
            this.probeTest.getCreatedProbeId());
        Assert.assertNotNull(this.messungTest.getCreatedMessungId());
        this.kommentarMTest.createService(
            baseUrl,
            testProtocol,
            this.messungTest.getCreatedMessungId());
        this.messwertTest.createService(
            baseUrl,
            testProtocol,
            this.messungTest.getCreatedMessungId());
    }

    /**
     * Testing UPDATE services.
     */
    @Test
    @RunAsClient
    public final void testC_probeUpdateService(@ArquillianResource URL baseUrl)
    throws Exception {
        Assert.assertNotNull(this.probeTest.getCreatedProbeId());
        this.probeTest.updateService(baseUrl, testProtocol);
    }

    /**
     * Testing UPDATE services.
     */
    @Test
    @RunAsClient
    public final void testC_messungUpdateService(@ArquillianResource URL baseUrl)
    throws Exception {
        Assert.assertNotNull(this.messungTest.getCreatedMessungId());
        this.messungTest.updateService(baseUrl, testProtocol);
    }

    /**
     * Testing UPDATE services.
     */
    @Test
    @RunAsClient
    public final void testC_kommentarPUpdateService(@ArquillianResource URL baseUrl)
    throws Exception {
        Assert.assertNotNull(this.kommentarPTest.getCreatedKommentarId());
        this.kommentarPTest.updateService(baseUrl, testProtocol);
    }

    /**
     * Testing UPDATE services.
     */
    @Test
    @RunAsClient
    public final void testC_kommentarMUpdateService(@ArquillianResource URL baseUrl)
    throws Exception {
        Assert.assertNotNull(this.kommentarMTest.getCreatedKommentarId());
        this.kommentarMTest.updateService(baseUrl, testProtocol);
    }

    /**
     * Testing UPDATE services.
     */
    @Test
    @RunAsClient
    public final void testC_messwertUpdateService(@ArquillianResource URL baseUrl)
    throws Exception {
        Assert.assertNotNull(this.messwertTest.getCreatedMesswertId());
        this.messwertTest.updateService(baseUrl, testProtocol);
    }

    /**
     * Testing DELETE services.
     */
    @Test
    @RunAsClient
    public final void testD_DeleteServices(@ArquillianResource URL baseUrl)
    throws Exception {
        Assert.assertNotNull(this.messwertTest.getCreatedMesswertId());
        this.messwertTest.deleteService(baseUrl, testProtocol);
        Assert.assertNotNull(this.kommentarMTest.getCreatedKommentarId());
        this.kommentarMTest.deleteService(baseUrl, testProtocol);
        Assert.assertNotNull(this.kommentarPTest.getCreatedKommentarId());
        this.kommentarPTest.deleteService(baseUrl, testProtocol);
        Assert.assertNotNull(this.messungTest.getCreatedMessungId());
        this.messungTest.deleteService(baseUrl, testProtocol);
        Assert.assertNotNull(this.probeTest.getCreatedProbeId());
        this.probeTest.deleteService(baseUrl, testProtocol);
    }
}
