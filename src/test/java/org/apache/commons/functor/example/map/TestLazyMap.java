/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.functor.example.map;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.functor.core.collection.Size;


/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
@SuppressWarnings("unchecked")
public class TestLazyMap extends TestCase {

    public TestLazyMap(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestLazyMap.class);
    }

    private Map baseMap = null;
    private Map lazyMap = null;
    private Map expectedMap = null;

    public void setUp() throws Exception {
        super.setUp();
        expectedMap = new HashMap();
        expectedMap.put("one",new Integer(3));
        expectedMap.put("two",new Integer(3));
        expectedMap.put("three", new Integer(5));
        expectedMap.put("four", new Integer(4));
        expectedMap.put("five", new Integer(4));

        baseMap = new HashMap();
        lazyMap = new LazyMap(baseMap,Size.instance());
    }

    public void tearDown() throws Exception {
        super.tearDown();
        baseMap = null;
        lazyMap = null;
        expectedMap = null;
    }

    // tests

    public void test() {
        for (Iterator iter = expectedMap.keySet().iterator(); iter.hasNext();) {
            Object key = iter.next();
            assertFalse(baseMap.containsKey(key));
            assertFalse(lazyMap.containsKey(key));
            assertEquals(expectedMap.get(key),lazyMap.get(key));
            assertEquals(expectedMap.get(key),baseMap.get(key));
            assertTrue(lazyMap.containsKey(key));
            assertTrue(baseMap.containsKey(key));
        }
        assertEquals(expectedMap,lazyMap);
        assertEquals(expectedMap,baseMap);
        baseMap.clear();
        for (Iterator iter = expectedMap.keySet().iterator(); iter.hasNext();) {
            Object key = iter.next();
            assertFalse(baseMap.containsKey(key));
            assertFalse(lazyMap.containsKey(key));
            assertEquals(expectedMap.get(key),lazyMap.get(key));
            assertEquals(expectedMap.get(key),baseMap.get(key));
            assertTrue(lazyMap.containsKey(key));
            assertTrue(baseMap.containsKey(key));
        }
        assertEquals(expectedMap,lazyMap);
        assertEquals(expectedMap,baseMap);
    }


    public void testBaseMapOverrides() {
        assertEquals(new Integer(5),lazyMap.get("xyzzy"));
        baseMap.put("xyzzy","xyzzy");
        assertEquals("xyzzy",lazyMap.get("xyzzy"));
    }

}
