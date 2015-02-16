/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3) 
 * and comes with ABSOLUTELY NO WARRANTY! Check out 
 * the documentation coming with IMIS-Labordaten-Application for details. 
 */
package de.intevation.lada;

import java.net.URL;

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

    private GetTests get;

    private CreateTests create;

    private UpdateTests update;

    private DeleteTests delete;

    public LadaTest() {
        get = new GetTests();
        create = new CreateTests();
        update = new UpdateTests();
        delete = new DeleteTests();
    }

    /**
     * Create a deployable WAR archive.
     */
    @Deployment(testable=true)
    public static WebArchive createDeployment() throws Exception {
        return ShrinkWrap.create(WebArchive.class, "lada-basis-test.war")
            .addPackages(true, Package.getPackage("de.intevation.lada"))
            .addAsResource("log4j.properties", "log4j.properties")
            .addAsResource("queryconf.json", "queryconf.json")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsResource("META-INF/test-persistence.xml",
                "META-INF/persistence.xml");
    }

    /**
     * Testing GET Services.
     */
    @Test
    @RunAsClient
    public final void testA_GetServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.get.test(baseUrl);
    }

    /**
     * Testing CREATE services.
     */
    @Test
    @RunAsClient
    public final void testB_CreateServices(@ArquillianResource URL baseUrl)
    throws Exception {
        this.create.test(baseUrl);
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
    }
}
