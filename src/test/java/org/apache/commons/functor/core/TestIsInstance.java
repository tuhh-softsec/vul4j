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
package org.apache.commons.functor.core;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.functor.BaseFunctorTest;
import org.apache.commons.functor.BinaryPredicate;
import org.apache.commons.functor.UnaryPredicate;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestIsInstance extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestIsInstance(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestIsInstance.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return IsInstance.of(String.class);
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
        BinaryPredicate p = IsInstance.INSTANCE;
        assertFalse(p.test(null, Number.class));
        assertFalse(p.test("foo", Number.class));
        assertTrue(p.test(3, Number.class));
        assertTrue(p.test(3L, Number.class));
    }

    public void testBoundTest() throws Exception {
        UnaryPredicate p = IsInstance.of(Number.class);
        assertFalse(p.test(null));
        assertFalse(p.test("foo"));
        assertTrue(p.test(3));
        assertTrue(p.test(3L));
    }

    public void testInstanceOfNull() throws Exception {
        BinaryPredicate p = new IsInstance();
        try {
            p.test("foo", null);
            fail("Expected NullPointerException");
        } catch(NullPointerException e) {
            // expected
        }
    }

    public void testEquals() throws Exception {
        BinaryPredicate p = IsInstance.INSTANCE;
        assertEquals(p, p);
        assertObjectsAreEqual(p, IsInstance.instance());
        assertObjectsAreNotEqual(p,Constant.truePredicate());
    }

    public void testBoundEquals() throws Exception {
        UnaryPredicate p = IsInstance.of(Object.class);
        assertEquals(p,p);
        assertObjectsAreEqual(p,IsInstance.of(Object.class));
        assertObjectsAreNotEqual(p,Constant.truePredicate());
        assertObjectsAreNotEqual(p,IsInstance.of(null));
        assertObjectsAreNotEqual(p,IsInstance.of(String.class));
    }
}
