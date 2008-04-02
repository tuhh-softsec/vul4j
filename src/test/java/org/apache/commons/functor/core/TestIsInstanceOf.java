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
import org.apache.commons.functor.UnaryPredicate;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestIsInstanceOf extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestIsInstanceOf(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestIsInstanceOf.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new IsInstanceOf(String.class);
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
        UnaryPredicate p = new IsInstanceOf(Number.class);
        assertTrue(!p.test(null));
        assertTrue(!p.test("foo"));
        assertTrue(p.test(new Integer(3)));
        assertTrue(p.test((Object)(new Integer(3))));
        assertTrue(p.test((new Long(3L))));
    }
    
    public void testInstanceOfNull() throws Exception {
        UnaryPredicate p = new IsInstanceOf(null);
        try {
            p.test("foo");
            fail("Expected NullPointerException");
        } catch(NullPointerException e) {
            // expected
        }
    }
    
    public void testEquals() throws Exception {
        UnaryPredicate p = new IsInstanceOf(Object.class);
        assertEquals(p,p);
        assertObjectsAreEqual(p,new IsInstanceOf(Object.class));
        assertObjectsAreNotEqual(p,Constant.truePredicate());
        assertObjectsAreNotEqual(p,new IsInstanceOf(null));
        assertObjectsAreNotEqual(p,new IsInstanceOf(String.class));
    }
}
