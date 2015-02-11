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
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.intevation.lada.test.ProbeServiceTest;
import de.intevation.lada.test.QueryServiceTest;


/**
 * Class to test the Lada server.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@RunWith(Arquillian.class)
public class LadaTest {

    /**
     * Create a deployable WAR archive.
     * */
    @Deployment
    public static WebArchive createDeployment() throws Exception {
        return ShrinkWrap.create(WebArchive.class, "lada-basis-test.war")
            .addPackages(true, Package.getPackage("de.intevation.lada"))
            .addClass(QueryServiceTest.class)
            .addAsResource("log4j.properties", "log4j.properties")
            .addAsResource("queryconf.json", "queryconf.json")
            .addAsResource("META-INF/test-persistence.xml",
                "META-INF/persistence.xml");
    }

    /**
     * Testing the QueryService.
     */
    @Test
    @RunAsClient
    public final void testQueryService(@ArquillianResource URL baseUrl)
    throws Exception {
        Assert.assertNotNull(baseUrl);
        QueryServiceTest queryServiceTest = new QueryServiceTest();
        queryServiceTest.test(baseUrl);
    }

    /**
     * Testing the ProbeService.
     */
    @Test
    @RunAsClient
    public final void testProbeService(@ArquillianResource URL baseUrl)
    throws Exception {
        Assert.assertNotNull(baseUrl);
        ProbeServiceTest test = new ProbeServiceTest();
        test.test(baseUrl);
    }
}
