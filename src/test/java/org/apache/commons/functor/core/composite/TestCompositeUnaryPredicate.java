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
package org.apache.commons.functor.core.composite;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.functor.BaseFunctorTest;
import org.apache.commons.functor.core.Constant;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestCompositeUnaryPredicate extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestCompositeUnaryPredicate(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestCompositeUnaryPredicate.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return Composite.predicate(Constant.TRUE);
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
        assertTrue(Composite.predicate(Constant.TRUE).test(null));
        assertTrue(Composite.predicate(Constant.TRUE, Constant.of(3)).test("xyzzy"));
        assertFalse(Composite.predicate(Constant.FALSE, Constant.of(4)).test("xyzzy"));
    }

    public void testNullNotAllowed() throws Exception {
        try {
            new CompositeUnaryPredicate(null);
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // expected
        }
        try {
            Composite.function(Constant.TRUE, null);
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // expected
        }
    }

    public void testOf() throws Exception {
        CompositeUnaryPredicate<Object> f = new CompositeUnaryPredicate<Object>(Constant.TRUE);
        assertTrue(f.test(null));
        for (int i=0;i<10;i++) {
            f = f.of(Constant.FALSE);
            assertTrue(f.test(null));
        }
    }

    public void testEquals() throws Exception {
        CompositeUnaryPredicate<Object> f = new CompositeUnaryPredicate<Object>(Constant.TRUE);
        assertEquals(f,f);
        CompositeUnaryPredicate<Object> g = new CompositeUnaryPredicate<Object>(Constant.TRUE);
        assertObjectsAreEqual(f,g);

        for (int i=0;i<3;i++) {
            f = f.of(Constant.of("x"));
            assertObjectsAreNotEqual(f,g);
            g = g.of(Constant.of("x"));
            assertObjectsAreEqual(f,g);
            f = f.of(Constant.of("y")).of(Constant.of("z"));
            assertObjectsAreNotEqual(f,g);
            g = g.of(Constant.of("y")).of(Constant.of("z"));
            assertObjectsAreEqual(f,g);
        }

        assertObjectsAreNotEqual(f,Constant.FALSE);
    }

}
