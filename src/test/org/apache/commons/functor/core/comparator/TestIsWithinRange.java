/*
 * Copyright 2003,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
        return new IsWithinRange(new Integer(5), new Integer(10));
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
        IsWithinRange p = new IsWithinRange(new Integer(5), new Integer(10));
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
            new IsWithinRange(new Integer(5), new Integer(4));
            fail("should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // good
        } catch (Exception e) {
            fail("should have thrown IllegalArgumentException, not " + e);
        }

        try {
            new IsWithinRange(new Integer(5), null);
            fail("should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // good
        } catch (Exception e) {
            fail("should have thrown IllegalArgumentException, not " + e);
        }
    }

    public void testEquals() throws Exception {
        IsWithinRange p1 = new IsWithinRange(new Integer(5), new Integer(10));
        IsWithinRange p2 = new IsWithinRange(new Integer(5), new Integer(10));
        assertEquals(p1, p2);
        p2 = new IsWithinRange(new Integer(5), new Integer(11));
        assertTrue(!p1.equals(p2));
        p2 = new IsWithinRange(new Integer(6), new Integer(10));
        assertTrue(!p1.equals(p2));
    }
}
