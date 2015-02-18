package de.intevation.lada;

import java.net.URL;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import de.intevation.lada.test.stamm.Stammdaten;

@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LadaStammTest extends BaseTest {

    private static Logger logger = Logger.getLogger(LadaStammTest.class);

    public LadaStammTest () {
        testProtocol = new ArrayList<Protocol>();
        verboseLogging = true;
    }

    @BeforeClass
    public static void beforeTests() {
        logger.info("---------- Testing Lada Stamm Services ----------");
    }
}
