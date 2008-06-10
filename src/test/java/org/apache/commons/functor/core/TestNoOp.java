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
package org.apache.commons.functor.core;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.functor.BaseFunctorTest;
import org.apache.commons.functor.BinaryProcedure;
import org.apache.commons.functor.Procedure;
import org.apache.commons.functor.UnaryProcedure;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestNoOp extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestNoOp(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestNoOp.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new NoOp();
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
        NoOp p = new NoOp();
        p.run();
        p.run(null);
        p.run(null,null);
        p.run("foo");
        p.run("foo",null);
        p.run(null,"bar");
        p.run("foo","bar");
    }

    public void testEquals() throws Exception {
        NoOp p = new NoOp();
        assertEquals(p,p);
        assertObjectsAreEqual(p,new NoOp());
        assertObjectsAreEqual(p,NoOp.instance());
        assertObjectsAreNotEqual(p,new Procedure() { public void run() { } });
        assertObjectsAreNotEqual(p,new UnaryProcedure<Object>() { public void run(Object a) { } });
        assertObjectsAreNotEqual(p,new BinaryProcedure<Object, Object>() { public void run(Object a, Object b) { } });
    }

    public void testConstant() throws Exception {
        assertEquals(NoOp.instance(),NoOp.instance());
        assertSame(NoOp.instance(),NoOp.instance());
    }
}
