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
import org.apache.commons.functor.core.Identity;
import org.apache.commons.functor.core.LeftIdentity;
import org.apache.commons.functor.core.RightIdentity;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestUnaryCompositeBinaryFunction extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestUnaryCompositeBinaryFunction(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestUnaryCompositeBinaryFunction.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new UnaryCompositeBinaryFunction<Object, Object, Object>(
            RightIdentity.FUNCTION,
            Constant.of("left"),
            Identity.instance());
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
        BinaryFunction<Object, Object, Object> f = new UnaryCompositeBinaryFunction<Object, Object, Object>(
                RightIdentity.FUNCTION,
                Constant.of("K"),
                Identity.instance());
        assertEquals("right",f.evaluate("left","right"));
        assertNull("right",f.evaluate("left",null));
        assertEquals("right",f.evaluate(null,"right"));
    }

    public void testEquals() throws Exception {
        BinaryFunction<Object, Object, Object> f = new UnaryCompositeBinaryFunction<Object, Object, Object>(
                LeftIdentity.FUNCTION,
                Constant.of("left"),
                Constant.of("right"));
        assertEquals(f,f);
        assertObjectsAreEqual(f,new UnaryCompositeBinaryFunction<Object, Object, Object>(
                LeftIdentity.FUNCTION,
                Constant.of("left"),
                Constant.of("right")));
        assertObjectsAreNotEqual(f,new UnaryCompositeBinaryFunction<Object, Object, Object>(
                RightIdentity.FUNCTION,
                Constant.of("left"),
                Constant.of("right")));
        assertObjectsAreNotEqual(f,new UnaryCompositeBinaryFunction<Object, Object, Object>(
                LeftIdentity.FUNCTION,
                Identity.instance(),
            Constant.of("right")));
        assertObjectsAreNotEqual(f,new UnaryCompositeBinaryFunction<Object, Object, Object>(
                LeftIdentity.FUNCTION,
                Constant.of("left"),
                Identity.instance()));
    }

}
