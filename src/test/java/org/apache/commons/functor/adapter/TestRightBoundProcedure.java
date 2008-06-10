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
import org.apache.commons.functor.UnaryProcedure;
import org.apache.commons.functor.core.LeftIdentity;
import org.apache.commons.functor.core.NoOp;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestRightBoundProcedure extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestRightBoundProcedure(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestRightBoundProcedure.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new RightBoundProcedure<Object, Object>(NoOp.INSTANCE,"xyzzy");
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

    public void testRun() throws Exception {
        UnaryProcedure<Object> p = new RightBoundProcedure<Object, Object>(
                new BinaryFunctionBinaryProcedure<Object, Object>(LeftIdentity.FUNCTION), "foo");
        p.run(Boolean.TRUE);
        p.run(Boolean.FALSE);
    }

    public void testEquals() throws Exception {
        UnaryProcedure<Object> f = new RightBoundProcedure<Object, Object>(NoOp.INSTANCE,"xyzzy");
        assertEquals(f,f);
        assertObjectsAreEqual(f,new RightBoundProcedure<Object, Object>(NoOp.INSTANCE,"xyzzy"));
        assertObjectsAreNotEqual(f,NoOp.INSTANCE);
        assertObjectsAreNotEqual(f,new RightBoundProcedure<Object, Object>(new BinaryFunctionBinaryProcedure<Object, Object>(LeftIdentity.FUNCTION),"xyzzy"));
        assertObjectsAreNotEqual(f,new RightBoundProcedure<Object, Object>(NoOp.INSTANCE,"foo"));
        assertObjectsAreNotEqual(f,new RightBoundProcedure<Object, Object>(NoOp.INSTANCE,null));
    }

    public void testAdaptNull() throws Exception {
        assertNull(RightBoundProcedure.bind(null,"xyzzy"));
    }

    public void testAdapt() throws Exception {
        assertNotNull(RightBoundProcedure.bind(NoOp.INSTANCE,"xyzzy"));
        assertNotNull(RightBoundProcedure.bind(NoOp.INSTANCE,null));
    }
}
