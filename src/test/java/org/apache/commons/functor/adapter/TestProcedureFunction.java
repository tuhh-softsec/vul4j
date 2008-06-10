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
import org.apache.commons.functor.Function;
import org.apache.commons.functor.Procedure;
import org.apache.commons.functor.core.Constant;
import org.apache.commons.functor.core.NoOp;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestProcedureFunction extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestProcedureFunction(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestProcedureFunction.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new ProcedureFunction<Object>(NoOp.INSTANCE);
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
        Function<Object> f = new ProcedureFunction<Object>(NoOp.INSTANCE);
        assertNull(f.evaluate());
    }

    public void testEquals() throws Exception {
        Function<Object> f = new ProcedureFunction<Object>(NoOp.INSTANCE);
        assertEquals(f,f);
        assertObjectsAreEqual(f,new ProcedureFunction<Object>(NoOp.INSTANCE));
        assertObjectsAreNotEqual(f,Constant.of("x"));
        assertObjectsAreNotEqual(f,new ProcedureFunction<Object>(new Procedure() { public void run() { } }));
        assertObjectsAreNotEqual(f,Constant.of(null));
    }

    public void testAdaptNull() throws Exception {
        assertNull(FunctionProcedure.adapt(null));
    }

    public void testAdapt() throws Exception {
        assertNotNull(ProcedureFunction.adapt(new NoOp()));
    }
}
