package org.esigate.util;

import java.util.Collection;
import java.util.Properties;

import junit.framework.TestCase;

/**
 * @author Alexis Thaveau
 */
public class ParameterTest extends TestCase {

    public void testGetValueString() throws Exception {
        Parameter<String> parameter1 = new ParameterString("test1", null);
        Parameter<String> parameter2 = new ParameterString("test2", "2");
        Properties properties = new Properties();
        assertNull(parameter1.getValue(properties));
        assertEquals("2", parameter2.getValue(properties));

        properties.put("test1", "0");
        properties.put("test2", "2");
        assertEquals("0", parameter1.getValue(properties));
        assertEquals("2", parameter2.getValue(properties));

    }

    public void testGetValueInteger() throws Exception {
        Parameter<Integer> parameter1 = new ParameterInteger("test1", null);
        Parameter<Integer> parameter2 = new ParameterInteger("test2", 2);
        Properties properties = new Properties();
        assertEquals((Integer) 0, parameter1.getValue(properties));
        assertEquals((Integer) 2, parameter2.getValue(properties));

        properties.put("test1", "0");
        properties.put("test2", "2");
        assertEquals((Integer) 0, parameter1.getValue(properties));
        assertEquals((Integer) 2, parameter2.getValue(properties));

    }

    public void testGetValueFloat() throws Exception {
        Parameter<Float> parameter1 = new ParameterFloat("test1", null);
        Parameter<Float> parameter2 = new ParameterFloat("test2", 2f);
        Properties properties = new Properties();
        assertEquals(0f, parameter1.getValue(properties));
        assertEquals(2f, parameter2.getValue(properties));

        properties.put("test1", "0");
        properties.put("test2", "2");
        assertEquals(0f, parameter1.getValue(properties));
        assertEquals(2f, parameter2.getValue(properties));

    }

    public void testGetValueBoolean() throws Exception {
        Parameter<Boolean> parameter1 = new ParameterBoolean("test1", Boolean.FALSE);
        Parameter<Boolean> parameter2 = new ParameterBoolean("test2", Boolean.TRUE);
        Properties properties = new Properties();
        assertFalse(parameter1.getValue(properties));
        assertTrue(parameter2.getValue(properties));
        properties.put("test1", "false");
        properties.put("test2", "false");
        assertFalse(parameter1.getValue(properties));
        assertFalse(parameter2.getValue(properties));
        properties.put("test1", "true");
        properties.put("test2", "true");
        assertTrue(parameter1.getValue(properties));
        assertTrue(parameter2.getValue(properties));

    }

    public void testGetValueCollection() throws Exception {
        Parameter<Collection<String>> parameter1 = new ParameterCollection("test1");
        Parameter<Collection<String>> parameter2 = new ParameterCollection("test2", "v1", "v2");
        Properties properties = new Properties();
        assertNotNull(parameter1.getValue(properties));
        assertTrue(parameter1.getValue(properties).isEmpty());
        assertEquals(2, parameter2.getValue(properties).size());
        assertTrue(parameter2.getValue(properties).contains("v1"));
        assertTrue(parameter2.getValue(properties).contains("v2"));

        properties.put("test1", "v1");
        properties.put("test2", "v1,v3");

        assertEquals(1, parameter1.getValue(properties).size());
        assertTrue(parameter1.getValue(properties).contains("v1"));

        assertEquals(2, parameter2.getValue(properties).size());
        assertTrue(parameter2.getValue(properties).contains("v1"));
        assertTrue(parameter2.getValue(properties).contains("v3"));

    }

    public void testGetValueArray() throws Exception {
        Parameter<String[]> parameter1 = new ParameterArray("test1", null);
        Parameter<String[]> parameter2 = new ParameterArray("test2", new String[] {"v1", "v2"});
        Properties properties = new Properties();
        assertNull(parameter1.getValue(properties));
        assertEquals(2, parameter2.getValue(properties).length);
        assertEquals("v1", parameter2.getValue(properties)[0]);
        assertEquals("v2", parameter2.getValue(properties)[1]);

        properties.put("test1", "v1");
        properties.put("test2", "v1,v3");

        assertEquals(1, parameter1.getValue(properties).length);
        assertEquals("v1", parameter1.getValue(properties)[0]);

        assertEquals(2, parameter2.getValue(properties).length);
        assertEquals("v1", parameter2.getValue(properties)[0]);
        assertEquals("v3", parameter2.getValue(properties)[1]);

    }

}
