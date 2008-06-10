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
import org.apache.commons.functor.BinaryPredicate;
import org.apache.commons.functor.core.Constant;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestBinaryFunctionBinaryPredicate extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestBinaryFunctionBinaryPredicate(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestBinaryFunctionBinaryPredicate.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new BinaryFunctionBinaryPredicate<Object, Object>(Constant.TRUE);
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

    public void testTestWhenTrue() throws Exception {
        BinaryPredicate<Object, Object> p = new BinaryFunctionBinaryPredicate<Object, Object>(Constant.TRUE);
        assertTrue(p.test(null,null));
    }

    public void testTestWhenFalse() throws Exception {
        BinaryPredicate<Object, Object> p = new BinaryFunctionBinaryPredicate<Object, Object>(Constant.FALSE);
        assertFalse(p.test(null,null));
    }

    public void testEquals() throws Exception {
        BinaryPredicate<Object, Object> p = new BinaryFunctionBinaryPredicate<Object, Object>(Constant.TRUE);
        assertEquals(p,p);
        assertObjectsAreEqual(p,new BinaryFunctionBinaryPredicate<Object, Object>(Constant.TRUE));
        assertObjectsAreNotEqual(p,Constant.truePredicate());
        assertObjectsAreNotEqual(p,new BinaryFunctionBinaryPredicate<Object, Object>(Constant.FALSE));
    }

    public void testAdaptNull() throws Exception {
        assertNull(BinaryFunctionBinaryPredicate.adapt(null));
    }

    public void testAdapt() throws Exception {
        assertNotNull(BinaryFunctionBinaryPredicate.adapt(Constant.TRUE));
    }
}
