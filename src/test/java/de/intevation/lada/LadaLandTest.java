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

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import de.intevation.lada.test.land.KommentarM;
import de.intevation.lada.test.land.KommentarP;
import de.intevation.lada.test.land.Messprogramm;
import de.intevation.lada.test.land.Messung;
import de.intevation.lada.test.land.Messwert;
import de.intevation.lada.test.land.Ort;
import de.intevation.lada.test.land.Probe;
import de.intevation.lada.test.land.Query;
import de.intevation.lada.test.land.Status;
import de.intevation.lada.test.land.Zusatzwert;


/**
 * Class to test the Lada server 'land' services.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LadaLandTest extends BaseTest {

    private static Logger logger = Logger.getLogger(LadaLandTest.class);

    private Probe probeTest;
    private Query queryTest;
    private Messung messungTest;
    private KommentarP kommentarPTest;
    private KommentarM kommentarMTest;
    private Messwert messwertTest;
    private Ort ortTest;
    private Status statusTest;
    private Zusatzwert zusatzwertTest;
    private Messprogramm messprogrammTest;

    public LadaLandTest() {
        probeTest = new Probe();
        queryTest = new Query();
        messungTest = new Messung();
        kommentarPTest = new KommentarP();
        kommentarMTest = new KommentarM();
        messwertTest = new Messwert();
        ortTest = new Ort();
        statusTest = new Status();
        zusatzwertTest = new Zusatzwert();
        messprogrammTest = new Messprogramm();
        testProtocol = new ArrayList<Protocol>();
        verboseLogging = false;
    }

    @BeforeClass
    public static void beforeTests() {
        logger.info("---------- Testing Lada Land Services ----------");
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
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_OrtGetAllServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.ortTest.getAllService(baseUrl, testProtocol);
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_OrtGetByIdServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.ortTest.getByIdService(baseUrl, testProtocol);
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_OrtGetFilterServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.ortTest.filterService(baseUrl, testProtocol);
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_StatusGetAllServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.statusTest.getAllService(baseUrl, testProtocol);
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_StatusGetByIdServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.statusTest.getByIdService(baseUrl, testProtocol);
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_StatusGetFilterServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.statusTest.filterService(baseUrl, testProtocol);
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_ZusatzwertGetAllServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.zusatzwertTest.getAllService(baseUrl, testProtocol);
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_ZusatzwertGetByIdServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.zusatzwertTest.getByIdService(baseUrl, testProtocol);
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_ZusatzwertGetFilterServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.zusatzwertTest.filterService(baseUrl, testProtocol);
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_MessprogrammGetAllServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.messprogrammTest.getAllService(baseUrl, testProtocol);
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_MessprogrammGetByIdServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.messprogrammTest.getByIdService(baseUrl, testProtocol);
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_MessprogrammGetFilterServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.messprogrammTest.filterService(baseUrl, testProtocol);
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
        this.ortTest.createService(
            baseUrl,
            testProtocol,
            this.probeTest.getCreatedProbeId());
        this.zusatzwertTest.createService(
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
        this.statusTest.createService(
            baseUrl,
            testProtocol,
            this.messungTest.getCreatedMessungId());
        this.messprogrammTest.createService(
            baseUrl,
            testProtocol);
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
     * Testing UPDATE services.
     */
    @Test
    @RunAsClient
    public final void testC_ortUpdateService(@ArquillianResource URL baseUrl)
    throws Exception {
        Assert.assertNotNull(this.ortTest.getCreatedId());
        this.ortTest.updateService(baseUrl, testProtocol);
    }

    /**
     * Testing UPDATE services.
     */
    @Test
    @RunAsClient
    public final void testC_statusUpdateService(@ArquillianResource URL baseUrl)
    throws Exception {
        Assert.assertNotNull(this.ortTest.getCreatedId());
        this.statusTest.updateService(baseUrl, testProtocol);
    }

    /**
     * Testing UPDATE services.
     */
    @Test
    @RunAsClient
    public final void testC_messprogrammUpdateService(@ArquillianResource URL baseUrl)
    throws Exception {
        Assert.assertNotNull(this.ortTest.getCreatedId());
        this.messprogrammTest.updateService(baseUrl, testProtocol);
    }

    /**
     * Testing DELETE services.
     */
    @Test
    @RunAsClient
    public final void testD_DeleteServices(@ArquillianResource URL baseUrl)
    throws Exception {
        Assert.assertNotNull(this.zusatzwertTest.getCreatedId());
        this.zusatzwertTest.deleteService(baseUrl, testProtocol);
        Assert.assertNotNull(this.statusTest.getCreatedId());
        this.statusTest.deleteService(baseUrl, testProtocol);
        Assert.assertNotNull(this.ortTest.getCreatedId());
        this.ortTest.deleteService(baseUrl, testProtocol);
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
        this.messprogrammTest.deleteService(baseUrl, testProtocol);
    }
}
