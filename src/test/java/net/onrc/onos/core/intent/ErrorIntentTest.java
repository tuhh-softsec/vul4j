package net.onrc.onos.core.intent;

import static org.junit.Assert.assertEquals;

import net.onrc.onos.core.intent.ErrorIntent.ErrorType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the ErrorIntent class.
 *
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class ErrorIntentTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test the result of executing constructor of the ErrorIntent class.
     * This test checks the fields of id, errorType, message and parentIntent.
     */
    @Test
    public void testCreate() {
        Intent parentIntent = new Intent("1");
        ErrorIntent errorIntent = new ErrorIntent(ErrorType.PATH_NOT_FOUND, "path not found", parentIntent);

        assertEquals("1", errorIntent.getId());
        assertEquals(ErrorType.PATH_NOT_FOUND, errorIntent.errorType);
        assertEquals("path not found", errorIntent.message);
        assertEquals(parentIntent, errorIntent.parentIntent);
    }
}
