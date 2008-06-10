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
import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.UnaryPredicate;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestIdentity extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestIdentity(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestIdentity.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return Identity.INSTANCE;
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

    public void testEvaluate() throws Exception {
        UnaryFunction<Object, Object> f = new Identity<Object>();
        assertNull(f.evaluate(null));
        assertEquals("xyzzy",f.evaluate("xyzzy"));
        assertEquals(new Integer(3),f.evaluate(new Integer(3)));
        Object obj = new Long(12345L);
        assertSame(obj,f.evaluate(obj));
    }

    public void testTest() throws Exception {
        UnaryPredicate<Object> p = new Identity<Object>();
        assertTrue(p.test(Boolean.TRUE));
        assertTrue(!p.test(Boolean.FALSE));
        try {
            p.test("true");
            fail("Expected ClassCastException");
        } catch(ClassCastException e) {
            // expected
        }
        try {
            p.test(null);
            fail("Expected NullPointerException");
        } catch(NullPointerException e) {
            // expected
        }
    }
    public void testEquals() throws Exception {
        UnaryFunction<Object, Object> f = new Identity<Object>();
        assertEquals(f,f);
        assertObjectsAreEqual(f,new Identity<Object>());
        assertObjectsAreEqual(f,Identity.instance());
        assertObjectsAreNotEqual(f,Constant.of("abcde"));
    }

    public void testConstant() throws Exception {
        assertEquals(Identity.instance(),Identity.instance());
    }
}
