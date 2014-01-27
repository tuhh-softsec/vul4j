package org.esigate.util;

import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

public class PropertiesUtilTest extends TestCase {

    public void testEmptyList() {
        Collection<String> values = PropertiesUtil.toCollection("");
        assertNotNull(values);
        assertTrue(values.isEmpty());
    }

    public void testSingleValueList() {
        Collection<String> values = PropertiesUtil.toCollection("test ");
        assertEquals(1, values.size());
        assertEquals("test", values.iterator().next());
    }

    public void testDoubleList() {
        Collection<String> values = PropertiesUtil.toCollection(" test1,test2");
        assertEquals(2, values.size());
        Iterator<String> it = values.iterator();
        assertEquals("test1", it.next());
        assertEquals("test2", it.next());
    }

}
