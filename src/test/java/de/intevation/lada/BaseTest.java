/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3) 
 * and comes with ABSOLUTELY NO WARRANTY! Check out 
 * the documentation coming with IMIS-Labordaten-Application for details. 
 */
package de.intevation.lada;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import org.junit.AfterClass;

/**
 * Base class for Lada server tests.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class BaseTest {

    protected static String ARCHIVE_NAME = "lada-server-test.war";

    public static String TEST_USER = "testeins";

    public static String TEST_ROLES = "cn=mst_06010 cn=mst_06_status, cn=land_06_stamm";

    private static Logger logger = Logger.getLogger(BaseTest.class);

    protected static List<Protocol> testProtocol;

    protected static boolean verboseLogging = false;

    /**
     * Create a deployable WAR archive.
     */
    @Deployment(testable=true)
    public static WebArchive createDeployment() throws Exception {
        File antlr = Maven.resolver().loadPomFromFile("pom.xml")
            .resolve("org.antlr:antlr4-runtime")
            .withoutTransitivity().asSingleFile();

        WebArchive archive = ShrinkWrap.create(WebArchive.class, ARCHIVE_NAME)
            .addPackages(true, Package.getPackage("de.intevation.lada"))
            .addAsResource("log4j.properties", "log4j.properties")
            .addAsResource("shibboleth.properties", "shibboleth.properties")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsLibrary(antlr)
            .addAsResource("META-INF/test-persistence.xml",
                "META-INF/persistence.xml");
        addWithDependencies("org.geotools:gt-api", archive);
        addWithDependencies("org.geotools:gt-referencing", archive);
        addWithDependencies("org.geotools:gt-epsg-hsql", archive);
        addWithDependencies("org.geotools:gt-opengis", archive);

        return archive;
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

    private static void addWithDependencies(String coordinate, WebArchive archive) {
        File[] files = Maven.resolver().loadPomFromFile("pom.xml")
            .resolve(coordinate).withTransitivity().asFile();
        for (File f : files) {
            archive.addAsLibrary(f);
        }
    }
}
