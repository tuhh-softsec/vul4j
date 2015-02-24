package de.intevation.lada;

import java.util.ArrayList;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.intevation.lada.test.validator.Probe;
import de.intevation.lada.validation.Validator;
import de.intevation.lada.validation.annotation.ValidationConfig;

@RunWith(Arquillian.class)
public class LadaValidatorTest extends BaseTest {

    private static Logger logger = Logger.getLogger(LadaStammTest.class);

    @Inject
    @ValidationConfig(type="Probe")
    private Validator probeValidator;

    public LadaValidatorTest() {
        testProtocol = new ArrayList<Protocol>();
    }

    @BeforeClass
    public static void beforeTests() {
        logger.info("---------- Testing Lada Validator ----------");
    }
}
