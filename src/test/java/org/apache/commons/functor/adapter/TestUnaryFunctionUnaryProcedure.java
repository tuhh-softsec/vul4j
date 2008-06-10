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
import org.apache.commons.functor.UnaryProcedure;
import org.apache.commons.functor.core.Constant;
import org.apache.commons.functor.core.NoOp;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestUnaryFunctionUnaryProcedure extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestUnaryFunctionUnaryProcedure(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestUnaryFunctionUnaryProcedure.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new UnaryFunctionUnaryProcedure<Object>(Constant.of("K"));
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
        class EvaluateCounter implements UnaryFunction<Object, Integer> {
            int count = 0;
            public Integer evaluate(Object a) { return count++; }
        }
        EvaluateCounter counter = new EvaluateCounter();
        UnaryProcedure<Object> p = new UnaryFunctionUnaryProcedure<Object>(counter);
        assertEquals(0,counter.count);
        p.run(null);
        assertEquals(1,counter.count);
        p.run("x");
        assertEquals(2,counter.count);
    }

    public void testEquals() throws Exception {
        UnaryProcedure<Object> p = new UnaryFunctionUnaryProcedure<Object>(Constant.of("K"));
        assertEquals(p,p);
        assertObjectsAreEqual(p,new UnaryFunctionUnaryProcedure<Object>(Constant.of("K")));
        assertObjectsAreNotEqual(p,NoOp.INSTANCE);
        assertObjectsAreNotEqual(p,new UnaryFunctionUnaryProcedure<Object>(Constant.of("J")));
    }

    public void testAdaptNull() throws Exception {
        assertNull(UnaryFunctionUnaryProcedure.adapt(null));
    }

    public void testAdapt() throws Exception {
        assertNotNull(UnaryFunctionUnaryProcedure.adapt(Constant.of("K")));
    }
}
