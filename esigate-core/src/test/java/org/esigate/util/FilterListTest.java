package org.esigate.util;

import java.util.Collection;
import java.util.HashSet;

import junit.framework.TestCase;

public class FilterListTest extends TestCase {

    private static Collection<String> collection(String... values) {
        Collection<String> result = new HashSet<String>();
        for (String value : values) {
            result.add(value);
        }
        return result;
    }

    public void testAdd() {
        FilterList list = new FilterList();
        assertFalse(list.contains("test"));
        list.add(collection("test"));
        assertTrue(list.contains("test"));
        assertFalse(list.contains("test2"));
    }

    public void testAddAll() {
        FilterList list = new FilterList();
        list.add(collection("*"));
        assertTrue(list.contains("test"));
    }

    public void testAddRemove() {
        FilterList list = new FilterList();
        list.add(collection("test"));
        assertTrue(list.contains("test"));
        list.remove(collection("test"));
        assertFalse(list.contains("test"));
    }

    public void testAddList() {
        FilterList list = new FilterList();
        list.add(collection("test", "test2"));
        assertTrue(list.contains("test"));
        assertTrue(list.contains("test2"));
    }

    public void testAddListRemoveAll() {
        FilterList list = new FilterList();
        list.add(collection("test", "test2"));
        list.remove(collection("*"));
        assertFalse(list.contains("test2"));
    }

    public void testAddAllRemoveList() {
        FilterList list = new FilterList();
        list.add(collection("*"));
        list.remove(collection("test", "test2", "test3"));
        assertFalse(list.contains("test"));
        assertFalse(list.contains("test2"));
        assertFalse(list.contains("test3"));
        assertTrue(list.contains("anything"));
    }

    public void testAddCaseInsensitive() {
        FilterList list = new FilterList();
        list.add(collection("TeSt"));
        assertTrue(list.contains("test"));
        assertTrue(list.contains("tEst"));
    }

}
