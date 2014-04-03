package net.onrc.onos.core.intent;

import static org.junit.Assert.*;
import net.onrc.onos.core.intent.Intent;
import net.onrc.onos.core.intent.IntentOperation;
import net.onrc.onos.core.intent.IntentOperation.Operator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the IntentOperation class.
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class IntentOperationTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test the result of executing constructor of the IntentOperation class.
     * This test checks the id field and the operator field.
     */
    @Test
    public void testCreate() {
        IntentOperation op1 = new IntentOperation(Operator.ADD, new Intent("1"));
        IntentOperation op2 = new IntentOperation(Operator.REMOVE, new Intent("2"));
        IntentOperation op3 = new IntentOperation(Operator.ERROR, new Intent("3"));

        assertEquals("1", op1.intent.getId());
        assertEquals(Operator.ADD, op1.operator);
        assertEquals("2", op2.intent.getId());
        assertEquals(Operator.REMOVE, op2.operator);
        assertEquals("3", op3.intent.getId());
        assertEquals(Operator.ERROR, op3.operator);
    }
}
