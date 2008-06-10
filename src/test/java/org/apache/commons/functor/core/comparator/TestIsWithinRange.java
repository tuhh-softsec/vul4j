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
import junit.framework.TestSuite;

import org.apache.commons.functor.BaseFunctorTest;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 * @author Jason Horman
 */
public class TestIsWithinRange extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestIsWithinRange(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestIsWithinRange.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new IsWithinRange<Integer>(new Integer(5), new Integer(10));
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

    public void testTest() throws Exception {
        IsWithinRange<Integer> p = new IsWithinRange<Integer>(new Integer(5), new Integer(10));
        assertTrue(p.test(new Integer(5)));
        assertTrue(p.test(new Integer(6)));
        assertTrue(p.test(new Integer(7)));
        assertTrue(p.test(new Integer(8)));
        assertTrue(p.test(new Integer(9)));
        assertTrue(p.test(new Integer(10)));

        assertTrue(!p.test(new Integer(4)));
        assertTrue(!p.test(new Integer(11)));

    }

    public void testInvalidRange() {
        try {
            new IsWithinRange<Integer>(new Integer(5), new Integer(4));
            fail("should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // good
        } catch (Exception e) {
            fail("should have thrown IllegalArgumentException, not " + e);
        }

        try {
            new IsWithinRange<Integer>(new Integer(5), null);
            fail("should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // good
        } catch (Exception e) {
            fail("should have thrown IllegalArgumentException, not " + e);
        }
    }

    public void testEquals() throws Exception {
        IsWithinRange<Integer> p1 = new IsWithinRange<Integer>(new Integer(5), new Integer(10));
        IsWithinRange<Integer> p2 = new IsWithinRange<Integer>(new Integer(5), new Integer(10));
        assertEquals(p1, p2);
        p2 = new IsWithinRange<Integer>(new Integer(5), new Integer(11));
        assertTrue(!p1.equals(p2));
        p2 = new IsWithinRange<Integer>(new Integer(6), new Integer(10));
        assertTrue(!p1.equals(p2));
    }

    public void testFactory() throws Exception {
        IsWithinRange<Integer> p = IsWithinRange.instance(new Integer(5), new Integer(10));
        assertTrue(p.test(new Integer(5)));
        assertTrue(p.test(new Integer(6)));
        assertTrue(p.test(new Integer(7)));
        assertTrue(p.test(new Integer(8)));
        assertTrue(p.test(new Integer(9)));
        assertTrue(p.test(new Integer(10)));

        assertTrue(!p.test(new Integer(4)));
        assertTrue(!p.test(new Integer(11)));

    }
}
