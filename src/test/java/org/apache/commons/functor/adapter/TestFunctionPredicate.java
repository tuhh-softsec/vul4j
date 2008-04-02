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
package org.apache.commons.functor.adapter;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.functor.BaseFunctorTest;
import org.apache.commons.functor.Predicate;
import org.apache.commons.functor.core.Constant;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestFunctionPredicate extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestFunctionPredicate(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestFunctionPredicate.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new FunctionPredicate(new Constant(Boolean.TRUE));
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

    public void testTestWhenTrue() throws Exception {
        Predicate p = new FunctionPredicate(new Constant(Boolean.TRUE));
        assertTrue(p.test());
    }
    
    public void testTestWhenFalse() throws Exception {
        Predicate p = new FunctionPredicate(new Constant(Boolean.FALSE));
        assertTrue(!p.test());
    }

    public void testTestWhenNull() throws Exception {
        Predicate p = new FunctionPredicate(new Constant(null));
        try {
            p.test();
            fail("Expected NullPointerException");
        } catch(NullPointerException e) {
            // expected
        }
    }
    
    public void testTestWhenNonBoolean() throws Exception {
        Predicate p = new FunctionPredicate(new Constant(new Integer(2)));
        try {
            p.test();
            fail("Expected ClassCastException");
        } catch(ClassCastException e) {
            // expected
        }
    }
    
    public void testEquals() throws Exception {
        Predicate p = new FunctionPredicate(new Constant(Boolean.TRUE));
        assertEquals(p,p);
        assertObjectsAreEqual(p,new FunctionPredicate(new Constant(Boolean.TRUE)));
        assertObjectsAreNotEqual(p,Constant.truePredicate());
        assertObjectsAreNotEqual(p,new FunctionPredicate(null));
        assertObjectsAreNotEqual(p,new FunctionPredicate(new Constant(Boolean.FALSE)));
        assertObjectsAreEqual(new FunctionPredicate(null),new FunctionPredicate(null));
    }

    public void testAdaptNull() throws Exception {
        assertNull(FunctionPredicate.adapt(null));
    }

    public void testAdapt() throws Exception {
        assertNotNull(FunctionPredicate.adapt(new Constant(Boolean.TRUE)));
    }
}
