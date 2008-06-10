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

import org.apache.commons.functor.core.IsInstance;


/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
@SuppressWarnings("unchecked")
public class TestPredicatedMap extends TestCase {

    public TestPredicatedMap(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestPredicatedMap.class);
    }

    private Map baseMap = null;
    private Map predicatedMap = null;
    public void setUp() throws Exception {
        super.setUp();
        baseMap = new HashMap();
        predicatedMap = new PredicatedMap(baseMap,IsInstance.of(String.class),IsInstance.of(Integer.class));
    }

    public void tearDown() throws Exception {
        super.tearDown();
        baseMap = null;
        predicatedMap = null;
    }

    // tests

    public void testCanPutMatchingPair() {
        predicatedMap.put("xyzzy", new Integer(17));
    }
    public void testCantPutInvalidValue() {
        try {
            predicatedMap.put("xyzzy", "xyzzy");
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // expected
        }
    }

    public void testCantPutInvalidKey() {
        try {
            predicatedMap.put(new Long(1), new Integer(3));
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // expected
        }
    }

    public void testOnlyValidPairsAreAddedInPutAll() {
        HashMap map = new HashMap();
        map.put("one", new Integer(17));
        map.put("two", "rejected");
        map.put(new Integer(17), "xyzzy");
        map.put(new Integer(7), new Integer(3));

        predicatedMap.putAll(map);
        assertEquals(new Integer(17), predicatedMap.get("one"));
        assertFalse(predicatedMap.containsKey("two"));
/*
        assertFalse(predicatedMap.containsKey(new Integer(17)));
        assertFalse(predicatedMap.containsKey(new Integer(7)));
*/
    }
}
