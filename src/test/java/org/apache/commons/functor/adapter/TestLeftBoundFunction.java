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
import org.apache.commons.functor.core.LeftIdentity;
import org.apache.commons.functor.core.RightIdentity;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestLeftBoundFunction extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestLeftBoundFunction(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestLeftBoundFunction.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new LeftBoundFunction<Object, Object>(RightIdentity.FUNCTION,"xyzzy");
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
        UnaryFunction<Object, Object> f = new LeftBoundFunction<Object, Object>(RightIdentity.FUNCTION,"foo");
        assertEquals("xyzzy",f.evaluate("xyzzy"));
    }

    public void testEquals() throws Exception {
        UnaryFunction<Object, Object> f = new LeftBoundFunction<Object, Object>(RightIdentity.FUNCTION,"xyzzy");
        assertEquals(f,f);
        assertObjectsAreEqual(f,new LeftBoundFunction<Object, Object>(RightIdentity.FUNCTION,"xyzzy"));
        assertObjectsAreNotEqual(f,Constant.of("xyzzy"));
        assertObjectsAreNotEqual(f,new LeftBoundFunction<Object, Object>(LeftIdentity.FUNCTION,"xyzzy"));
        assertObjectsAreNotEqual(f,new LeftBoundFunction<Object, Object>(RightIdentity.FUNCTION,"bar"));
    }

    public void testAdaptNull() throws Exception {
        assertNull(LeftBoundFunction.bind(null,"xyzzy"));
    }

    public void testAdapt() throws Exception {
        assertNotNull(LeftBoundFunction.bind(RightIdentity.FUNCTION,"xyzzy"));
        assertNotNull(LeftBoundFunction.bind(RightIdentity.FUNCTION,null));
    }
}
