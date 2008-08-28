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

/**
 * @version $Revision$ $Date$
 * @author Matt Benson
 */
public class TestIsSame extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestIsSame(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestIsSame.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new IsSame<Object, Object>();
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
        IsSame<Object, Object> p = new IsSame<Object, Object>();
        assertTrue("For symmetry, two nulls should be same", p.test(null, null));
        assertTrue(p.test("foo", "foo"));
        assertFalse(p.test(null, "foo"));
        assertFalse(p.test("foo", null));
        assertFalse(p.test(new Integer(3), new Integer(3)));
        assertFalse(p.test(null, new Integer(3)));
        assertFalse(p.test(new Integer(3), null));

        assertFalse(p.test(new Integer(3), new Integer(4)));
        assertFalse(p.test(new Integer(4), new Integer(3)));
        assertFalse(p.test("3", new Integer(3)));
        assertFalse(p.test(new Integer(3), "3"));
    }

    public void testEquals() throws Exception {
        BinaryPredicate<Object, Object> f = new IsSame<Object, Object>();
        assertEquals(f, f);

        assertObjectsAreEqual(f, new IsSame<Object, Object>());
        assertObjectsAreEqual(f, IsSame.instance());
        assertObjectsAreNotEqual(f, Constant.truePredicate());
    }

    public void testConstant() throws Exception {
        assertEquals(IsSame.instance(), IsSame.instance());
        assertNotSame(IsSame.instance(), IsSame.instance());
        assertSame(IsSame.INSTANCE, IsSame.INSTANCE);
    }
}
