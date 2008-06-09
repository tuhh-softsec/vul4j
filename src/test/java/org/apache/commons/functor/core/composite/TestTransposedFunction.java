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
import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.core.Constant;
import org.apache.commons.functor.core.LeftIdentity;
import org.apache.commons.functor.core.RightIdentity;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestTransposedFunction extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestTransposedFunction(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestTransposedFunction.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new TransposedFunction<Object, Object, Object>(LeftIdentity.FUNCTION);
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
        BinaryFunction<Object, Object, Object> f = new TransposedFunction<Object, Object, Object>(LeftIdentity.FUNCTION);
        assertEquals("xyzzy",f.evaluate(null,"xyzzy"));
        assertNull(f.evaluate("xyzzy",null));
    }

    public void testEquals() throws Exception {
        BinaryFunction<Object, Object, Object> f = new TransposedFunction<Object, Object, Object>(LeftIdentity.FUNCTION);
        assertEquals(f,f);
        assertObjectsAreEqual(f,new TransposedFunction<Object, Object, Object>(LeftIdentity.FUNCTION));
        assertObjectsAreNotEqual(f,new TransposedFunction<Object, Object, Object>(RightIdentity.FUNCTION));
        assertObjectsAreNotEqual(f,Constant.of("y"));
    }

    public void testTransposeNull() throws Exception {
        assertNull(TransposedFunction.transpose(null));
    }

    public void testTranspose() throws Exception {
        assertNotNull(TransposedFunction.transpose(Constant.of("x")));
    }
}
