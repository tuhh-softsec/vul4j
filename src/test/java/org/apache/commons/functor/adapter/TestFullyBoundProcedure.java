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
import org.apache.commons.functor.Procedure;
import org.apache.commons.functor.core.NoOp;
import org.apache.commons.functor.core.RightIdentity;

/**
 * @version $Revision$ $Date$
 * @author Matt Benson
 */
public class TestFullyBoundProcedure extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestFullyBoundProcedure(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestFullyBoundProcedure.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new FullyBoundProcedure<Object, Object>(NoOp.INSTANCE, "xyzzy", null);
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
        Procedure p = new FullyBoundProcedure<Object, Object>(new BinaryFunctionBinaryProcedure<Object, Object>(
                RightIdentity.FUNCTION), "foo", null);
        p.run();
    }

    public void testEquals() throws Exception {
        Procedure f = new FullyBoundProcedure<Object, Object>(NoOp.INSTANCE, "xyzzy", null);
        assertEquals(f, f);
        assertObjectsAreEqual(f, new FullyBoundProcedure<Object, Object>(NoOp.INSTANCE, "xyzzy", null));
        assertObjectsAreNotEqual(f, new NoOp());
        assertObjectsAreNotEqual(f, new FullyBoundProcedure<Object, Object>(
                new BinaryFunctionBinaryProcedure<Object, Object>(RightIdentity.FUNCTION), "xyzzy", null));
        assertObjectsAreNotEqual(f, new FullyBoundProcedure<Object, Object>(NoOp.INSTANCE, "foo", null));
        assertObjectsAreNotEqual(f, new FullyBoundProcedure<Object, Object>(NoOp.INSTANCE, null, null));
    }

    public void testAdaptNull() throws Exception {
        assertNull(FullyBoundProcedure.bind(null, "xyzzy", null));
    }

    public void testAdapt() throws Exception {
        assertNotNull(FullyBoundProcedure.bind(new NoOp(), "xyzzy", "foobar"));
        assertNotNull(FullyBoundProcedure.bind(new NoOp(), null, null));
    }
}
