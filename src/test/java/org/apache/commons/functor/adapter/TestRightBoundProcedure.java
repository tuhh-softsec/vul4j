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
        return new RightBoundProcedure(new NoOp(),"xyzzy");
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
        UnaryProcedure p = new RightBoundProcedure(new BinaryFunctionBinaryProcedure(LeftIdentity.FUNCTION),"foo");
        p.run(Boolean.TRUE);
        p.run(Boolean.FALSE);
    }

    public void testEquals() throws Exception {
        UnaryProcedure f = new RightBoundProcedure(new NoOp(),"xyzzy");
        assertEquals(f,f);
        assertObjectsAreEqual(f,new RightBoundProcedure(new NoOp(),"xyzzy"));
        assertObjectsAreNotEqual(f,new NoOp());
        assertObjectsAreNotEqual(f,new RightBoundProcedure(new BinaryFunctionBinaryProcedure(LeftIdentity.FUNCTION),"xyzzy"));
        assertObjectsAreNotEqual(f,new RightBoundProcedure(new NoOp(),"foo"));
        assertObjectsAreNotEqual(f,new RightBoundProcedure(null,"xyzzy"));
        assertObjectsAreNotEqual(f,new RightBoundProcedure(new NoOp(),null));
        assertObjectsAreEqual(new RightBoundProcedure(null,null),new RightBoundProcedure(null,null));
    }

    public void testAdaptNull() throws Exception {
        assertNull(RightBoundProcedure.bind(null,"xyzzy"));
    }

    public void testAdapt() throws Exception {
        assertNotNull(RightBoundProcedure.bind(new NoOp(),"xyzzy"));
        assertNotNull(RightBoundProcedure.bind(new NoOp(),null));
    }
}
