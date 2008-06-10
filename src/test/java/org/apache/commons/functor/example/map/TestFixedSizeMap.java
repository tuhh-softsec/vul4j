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
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
@SuppressWarnings("unchecked")
public class TestFixedSizeMap extends TestCase {

    public TestFixedSizeMap(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestFixedSizeMap.class);
    }

    private Map baseMap = null;
    private Map fixedMap = null;

    public void setUp() throws Exception {
        super.setUp();
        baseMap = new HashMap();
        baseMap.put(new Integer(1),"one");
        baseMap.put(new Integer(2),"two");
        baseMap.put(new Integer(3),"three");
        baseMap.put(new Integer(4),"four");
        baseMap.put(new Integer(5),"five");

        fixedMap = new FixedSizeMap(baseMap);
    }

    public void tearDown() throws Exception {
        super.tearDown();
        baseMap = null;
        fixedMap = null;
    }

    // tests

    public void testCantPutNewPair() {
        try {
            fixedMap.put("xyzzy","xyzzy");
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // expected
        }
    }

    public void testCantPutNewPairViaPutAll() {
        Map map = new HashMap();
        map.put(new Integer(1),"uno");
        map.put("xyzzy","xyzzy");
        map.put(new Integer(2),"dos");

        try {
            fixedMap.putAll(map);
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // expected
        }

        assertEquals("one",fixedMap.get(new Integer(1)));
        assertEquals("two",fixedMap.get(new Integer(2)));
    }

    public void testCantClear() {
        try {
            fixedMap.clear();
            fail("Expected UnsupportedOperationException");
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    public void testCantRemove() {
        try {
            fixedMap.remove(new Integer(1));
            fail("Expected UnsupportedOperationException");
        } catch(UnsupportedOperationException e) {
            // expected
        }
    }

    public void testCanAssociateNewValueWithOldKey() {
        fixedMap.put(new Integer(1),"uno");
        assertEquals("uno",fixedMap.get(new Integer(1)));
        assertEquals("two",fixedMap.get(new Integer(2)));
        assertEquals("three",fixedMap.get(new Integer(3)));
    }

    public void testCanAssociateNewValueWithOldKeyViaPutAll() {
        Map map = new HashMap();
        map.put(new Integer(1),"uno");
        map.put(new Integer(2),"dos");

        fixedMap.putAll(map);

        assertEquals("uno",fixedMap.get(new Integer(1)));
        assertEquals("dos",fixedMap.get(new Integer(2)));
        assertEquals("three",fixedMap.get(new Integer(3)));
    }


}
