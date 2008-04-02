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
import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.core.Constant;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestFunctionUnaryFunction extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestFunctionUnaryFunction(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestFunctionUnaryFunction.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new FunctionUnaryFunction(new Constant("xyzzy"));
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
        UnaryFunction f = new FunctionUnaryFunction(new Constant("xyzzy"));
        assertEquals("xyzzy",f.evaluate(null));
        assertEquals("xyzzy",f.evaluate("abc"));
    }
    
    public void testEquals() throws Exception {
        UnaryFunction f = new FunctionUnaryFunction(new Constant("xyzzy"));
        assertEquals(f,f);
        assertObjectsAreEqual(f,new FunctionUnaryFunction(new Constant("xyzzy")));
        assertObjectsAreNotEqual(f,new Constant("x"));
        assertObjectsAreNotEqual(f,new FunctionUnaryFunction(new Constant(null)));
        assertObjectsAreNotEqual(f,new Constant(null));
        assertObjectsAreNotEqual(f,new FunctionUnaryFunction(null));
        assertObjectsAreEqual(new FunctionUnaryFunction(null),new FunctionUnaryFunction(null));
    }

    public void testAdaptNull() throws Exception {
        assertNull(FunctionUnaryFunction.adapt(null));
    }

    public void testAdapt() throws Exception {
        assertNotNull(FunctionUnaryFunction.adapt(new Constant("xyzzy")));
    }
}
