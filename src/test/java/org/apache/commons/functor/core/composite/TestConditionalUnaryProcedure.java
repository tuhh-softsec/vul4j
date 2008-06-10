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
import org.apache.commons.functor.UnaryProcedure;
import org.apache.commons.functor.core.Constant;
import org.apache.commons.functor.core.Identity;
import org.apache.commons.functor.core.NoOp;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestConditionalUnaryProcedure extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestConditionalUnaryProcedure(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestConditionalUnaryProcedure.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new ConditionalUnaryProcedure<Object>(
            Constant.TRUE,
            NoOp.INSTANCE,
            NoOp.INSTANCE);
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
        RunCounter left = new RunCounter();
        RunCounter right = new RunCounter();
        ConditionalUnaryProcedure<Object> p = new ConditionalUnaryProcedure<Object>(
            Identity.INSTANCE,
            left,
            right);
        assertEquals(0,left.count);
        assertEquals(0,right.count);
        p.run(Boolean.TRUE);
        assertEquals(1,left.count);
        assertEquals(0,right.count);
        p.run(Boolean.FALSE);
        assertEquals(1,left.count);
        assertEquals(1,right.count);
        p.run(Boolean.TRUE);
        assertEquals(2,left.count);
        assertEquals(1,right.count);
    }

    public void testEquals() throws Exception {
        ConditionalUnaryProcedure<Object> p = new ConditionalUnaryProcedure<Object>(
            Identity.INSTANCE,
            NoOp.INSTANCE,
            NoOp.INSTANCE);
        assertEquals(p,p);
        assertObjectsAreEqual(p,new ConditionalUnaryProcedure<Object>(
            Identity.INSTANCE,
            NoOp.INSTANCE,
            NoOp.INSTANCE));
        assertObjectsAreNotEqual(p,new ConditionalUnaryProcedure<Object>(
            Constant.TRUE,
            NoOp.INSTANCE,
            NoOp.INSTANCE));
    }

    // Classes
    // ------------------------------------------------------------------------

    static class RunCounter implements UnaryProcedure<Object> {
        public void run(Object obj) {
            count++;
        }
        public int count = 0;
    }
}
