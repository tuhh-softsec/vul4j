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
package org.apache.commons.functor.adapter;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.functor.BaseFunctorTest;
import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.core.Constant;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestBinaryPredicateBinaryFunction extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestBinaryPredicateBinaryFunction(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestBinaryPredicateBinaryFunction.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new BinaryPredicateBinaryFunction(new Constant(true));
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
        BinaryFunction f = new BinaryPredicateBinaryFunction(new Constant(true));
        assertEquals(Boolean.TRUE,f.evaluate(null,null));
    }
    
    public void testTestWhenFalse() throws Exception {
        BinaryFunction f = new BinaryPredicateBinaryFunction(new Constant(false));
        assertEquals(Boolean.FALSE,f.evaluate(null,null));
    }

    public void testEquals() throws Exception {
        BinaryFunction f = new BinaryPredicateBinaryFunction(new Constant(true));
        assertEquals(f,f);
        assertObjectsAreEqual(f,new BinaryPredicateBinaryFunction(new Constant(true)));
        assertObjectsAreNotEqual(f,new Constant("x"));
        assertObjectsAreNotEqual(f,new BinaryPredicateBinaryFunction(new Constant(false)));
        assertObjectsAreNotEqual(f,new BinaryPredicateBinaryFunction(null));
        assertObjectsAreEqual(new BinaryPredicateBinaryFunction(null),new BinaryPredicateBinaryFunction(null));
    }

    public void testAdaptNull() throws Exception {
        assertNull(BinaryFunctionBinaryPredicate.adapt(null));
    }

    public void testAdapt() throws Exception {
        assertNotNull(BinaryPredicateBinaryFunction.adapt(new Constant(true)));
    }
}
