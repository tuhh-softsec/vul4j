package org.esigate.cache;

import org.esigate.cache.LRUMap;

import junit.framework.TestCase;

public class LRUMapTest extends TestCase{
	public void testRemoveValueFromMap() {
        LRUMap<String, String> cache = new LRUMap<String, String>(3);
        cache.put("key1", "val1");
        cache.put("key2", "val2");
        cache.put("key3", "val3");
        cache.put("key4", "val4");
        assertEquals(3, cache.size());
        
        assertFalse(cache.containsKey("key1"));
        assertTrue(cache.containsKey("key2"));
        assertTrue(cache.containsKey("key3"));
        assertTrue(cache.containsKey("key4"));
        
        assertEquals(null, cache.get("key1"));
        assertEquals("val2", cache.get("key2"));
        assertEquals("val3", cache.get("key3"));
        assertEquals("val4", cache.get("key4"));  
    }
	
	public void testRemoveLRUValueFromMap() {
        LRUMap<String, String> cache = new LRUMap<String, String>(3);
        cache.put("key1", "val1");
        cache.put("key2", "val2");
        cache.put("key3", "val3");
        assertNotNull(cache.get("key1"));
        assertNotNull(cache.get("key2"));
        assertTrue(cache.containsValue("val3"));
        cache.put("key4", "val4");
        assertEquals(3, cache.size());
        
        assertFalse("", cache.containsKey("key3"));
        assertTrue(cache.containsKey("key1"));
        assertTrue(cache.containsKey("key2"));
        assertTrue(cache.containsKey("key4"));
        
        assertEquals(null, cache.get("key3"));
        assertEquals("val1", cache.get("key1"));
        assertEquals("val2", cache.get("key2"));
        assertEquals("val4", cache.get("key4"));  
    }
}
