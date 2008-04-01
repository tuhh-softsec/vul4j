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
import org.apache.commons.functor.BinaryProcedure;
import org.apache.commons.functor.core.Constant;
import org.apache.commons.functor.core.NoOp;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestBinaryProcedureBinaryFunction extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestBinaryProcedureBinaryFunction(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestBinaryProcedureBinaryFunction.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new BinaryProcedureBinaryFunction(new NoOp());
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
        BinaryFunction f = new BinaryProcedureBinaryFunction(new NoOp());
        assertNull(f.evaluate(null,null));
    }
    
    public void testEquals() throws Exception {
        BinaryFunction f = new BinaryProcedureBinaryFunction(new NoOp());
        assertEquals(f,f);
        assertObjectsAreEqual(f,new BinaryProcedureBinaryFunction(new NoOp()));
        assertObjectsAreNotEqual(f,new Constant("x"));
        assertObjectsAreNotEqual(f,new BinaryProcedureBinaryFunction(new BinaryProcedure() { public void run(Object a, Object b) { } }));
        assertObjectsAreNotEqual(f,new Constant(null));
        assertObjectsAreNotEqual(f,new BinaryProcedureBinaryFunction(null));
        assertObjectsAreEqual(new BinaryProcedureBinaryFunction(null),new BinaryProcedureBinaryFunction(null));
    }

    public void testAdaptNull() throws Exception {
        assertNull(BinaryFunctionBinaryProcedure.adapt(null));
    }

    public void testAdapt() throws Exception {
        assertNotNull(BinaryProcedureBinaryFunction.adapt(new NoOp()));
    }
}
