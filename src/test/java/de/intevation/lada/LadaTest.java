package de.intevation.lada;

import java.net.URL;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.intevation.lada.rest.QueryService;
import de.intevation.lada.test.ProbeServiceTest;
import de.intevation.lada.test.QueryServiceTest;

@RunWith(Arquillian.class)
public class LadaTest {

    @Inject
    private QueryService queryService;

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
    public final void testQueryService() throws Exception {
        QueryServiceTest queryServiceTest = new QueryServiceTest();
        queryServiceTest.test(queryService);
    }

    /**
     * Testing the ProbeService.
     */
    @Test
    @RunAsClient
    public final void testProbeService(
        @ArquillianResource URL baseUrl)
    throws Exception {
        Assert.assertNotNull(baseUrl);
        ProbeServiceTest test = new ProbeServiceTest();
        test.test(baseUrl);
    }
}
