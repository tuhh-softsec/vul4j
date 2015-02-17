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
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import de.intevation.lada.test.CreateTests;
import de.intevation.lada.test.DeleteTests;
import de.intevation.lada.test.GetTests;
import de.intevation.lada.test.UpdateTests;


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

    private boolean verboseLogging = false;

    private GetTests get;

    private CreateTests create;

    private UpdateTests update;

    private DeleteTests delete;

    private List<Protocol> testProtocol;

    public LadaTest() {
        get = new GetTests();
        create = new CreateTests();
        update = new UpdateTests();
        delete = new DeleteTests();
        testProtocol = new ArrayList<Protocol>();
    }

    /**
     * Create a deployable WAR archive.
     */
    @Deployment(testable=true)
    public static WebArchive createDeployment() throws Exception {
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

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_GetServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.get.test(baseUrl);
        logger.info("---------- Testprotocol -----------");
        testProtocol.addAll(this.get.getProtocol());
        for (Protocol p : testProtocol) {
            logger.info(p.toString(verboseLogging));
        }
    }

    /**
     * Testing CREATE services.
     */
    @Test
    @RunAsClient
    public final void testB_CreateServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.create.test(baseUrl);
        testProtocol.addAll(this.create.getProtocol());
        for (Protocol p : testProtocol) {
            logger.info(p.toString(verboseLogging));
        }
    }

    /**
     * Testing UPDATE services.
     */
    @Test
    @RunAsClient
    public final void testC_UpdateServices(@ArquillianResource URL baseUrl)
    throws Exception {
        Assert.assertNotNull(this.create.getCreatedProbeId());
        this.update.test(
            baseUrl,
            this.create.getCreatedProbeId(),
            this.create.getCreatedMessungId());
        testProtocol.addAll(this.update.getProtocol());
        for (Protocol p : testProtocol) {
            logger.info(p.toString(verboseLogging));
        }
    }

    /**
     * Testing DELETE services.
     */
    @Test
    @RunAsClient
    public final void testD_DeleteServices(@ArquillianResource URL baseUrl)
    throws Exception {
        Assert.assertNotNull(this.create.getCreatedProbeId());
        this.delete.test(
            baseUrl,
            this.create.getCreatedProbeId(),
            this.create.getCreatedMessungId());
        testProtocol.addAll(this.delete.getProtocol());
        for (Protocol p : testProtocol) {
            logger.info(p.toString(verboseLogging));
        }
    }
}
