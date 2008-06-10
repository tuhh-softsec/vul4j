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
import org.apache.commons.functor.core.Identity;
import org.apache.commons.functor.core.NoOp;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestBoundProcedure extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestBoundProcedure(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestBoundProcedure.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new BoundProcedure(NoOp.INSTANCE,"xyzzy");
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
        Procedure p = new BoundProcedure(new UnaryFunctionUnaryProcedure<Object>(Identity.INSTANCE),Boolean.TRUE);
        p.run();
    }

    public void testEquals() throws Exception {
        Procedure f = new BoundProcedure(NoOp.INSTANCE,"xyzzy");
        assertEquals(f,f);
        assertObjectsAreEqual(f,new BoundProcedure(NoOp.INSTANCE,"xyzzy"));
        assertObjectsAreNotEqual(f,NoOp.INSTANCE);
        assertObjectsAreNotEqual(f,new BoundProcedure(NoOp.INSTANCE,"foo"));
        assertObjectsAreNotEqual(f,new BoundProcedure(new UnaryFunctionUnaryProcedure<Object>(Identity.INSTANCE),"xyzzy"));
        assertObjectsAreNotEqual(f,new BoundProcedure(NoOp.INSTANCE,null));
    }

    public void testAdaptNull() throws Exception {
        assertNull(BoundProcedure.bind(null,"xyzzy"));
    }

    public void testAdapt() throws Exception {
        assertNotNull(BoundProcedure.bind(new NoOp(),"xyzzy"));
        assertNotNull(BoundProcedure.bind(new NoOp(),null));
    }
}
