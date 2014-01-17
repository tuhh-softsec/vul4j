package org.esigate.util;

import junit.framework.TestCase;

public class FilterListTest extends TestCase {

    public void testAdd() {
        FilterList list = new FilterList();
        assertFalse(list.contains("test"));
        list.add("test");
        assertTrue(list.contains("test"));
        assertFalse(list.contains("test2"));
    }

    public void testAddAll() {
        FilterList list = new FilterList();
        list.add("*");
        assertTrue(list.contains("test"));
    }

    public void testAddRemove() {
        FilterList list = new FilterList();
        list.add("test");
        assertTrue(list.contains("test"));
        list.remove("test");
        assertFalse(list.contains("test"));
    }

    public void testAddList() {
        FilterList list = new FilterList();
        list.add("test");
        list.add("test2");
        assertTrue(list.contains("test"));
        assertTrue(list.contains("test2"));
    }

    public void testAddListRemoveAll() {
        FilterList list = new FilterList();
        list.add("test");
        list.add("test2");
        list.remove("*");
        assertFalse(list.contains("test2"));
    }

    public void testAddAllRemoveList() {
        FilterList list = new FilterList();
        list.add("*");
        list.remove("test");
        list.remove("test2");
        list.remove("test3");
        assertFalse(list.contains("test"));
        assertFalse(list.contains("test2"));
        assertFalse(list.contains("test3"));
        assertTrue(list.contains("anything"));
    }

    public void testAddCaseInsensitive() {
        FilterList list = new FilterList();
        list.add("TeSt");
        assertTrue(list.contains("test"));
        assertTrue(list.contains("tEst"));
    }

}
