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
package org.apache.commons.functor.core.comparator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestComparableComparator extends TestCase {

    // Conventional
    // ------------------------------------------------------------------------

    public TestComparableComparator(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestComparableComparator.class);
    }

    // Lifecycle
    // ------------------------------------------------------------------------

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    // Tests
    // ------------------------------------------------------------------------
    
    public void testCompareIntegers() {
        assertTrue(ComparableComparator.instance().compare(new Integer(Integer.MIN_VALUE),new Integer(Integer.MIN_VALUE)) == 0);
        assertTrue(ComparableComparator.instance().compare(new Integer(-1),new Integer(-1)) == 0);
        assertTrue(ComparableComparator.instance().compare(new Integer(0),new Integer(0)) == 0);
        assertTrue(ComparableComparator.instance().compare(new Integer(Integer.MAX_VALUE),new Integer(Integer.MAX_VALUE)) == 0);
        assertTrue(ComparableComparator.instance().compare(new Integer(1),new Integer(1)) == 0);
    }

    public void testCompareIncomparable() {
        try {
            ComparableComparator.instance().compare(new Object(),new Integer(2));
            fail("Expected ClassCastException");
        } catch(ClassCastException e) {
            // expected
        }
    }

    public void testCompareNull() {
        try {
            ComparableComparator.instance().compare(null,new Integer(2));
            fail("Expected NullPointerException");
        } catch(NullPointerException e) {
            // expected
        }
    }

    public void testEqualsAndHashCode() {
        assertEquals(new ComparableComparator(),new ComparableComparator());
        assertEquals(new ComparableComparator().hashCode(),new ComparableComparator().hashCode());
        assertTrue(!new ComparableComparator().equals(null));
    }
}
