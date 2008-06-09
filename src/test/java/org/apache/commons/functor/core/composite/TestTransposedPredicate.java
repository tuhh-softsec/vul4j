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
import org.apache.commons.functor.BinaryPredicate;
import org.apache.commons.functor.adapter.BinaryFunctionBinaryPredicate;
import org.apache.commons.functor.core.Constant;
import org.apache.commons.functor.core.LeftIdentity;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestTransposedPredicate extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestTransposedPredicate(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestTransposedPredicate.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new TransposedPredicate(Constant.truePredicate());
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
        BinaryPredicate<Boolean, Boolean> p = new TransposedPredicate<Boolean, Boolean>(BinaryFunctionBinaryPredicate
                .adapt(LeftIdentity.<Boolean, Boolean> function()));
        assertEquals(true,p.test(Boolean.FALSE,Boolean.TRUE));
        assertEquals(false,p.test(Boolean.TRUE,Boolean.FALSE));
    }

    public void testEquals() throws Exception {
        BinaryPredicate<Object, Object> p = new TransposedPredicate<Object, Object>(Constant.TRUE);
        assertEquals(p,p);
        assertObjectsAreEqual(p,new TransposedPredicate<Object, Object>(Constant.TRUE));
        assertObjectsAreEqual(p,TransposedPredicate.transpose(Constant.TRUE));
        assertObjectsAreNotEqual(p,new TransposedPredicate<Object, Object>(Constant.FALSE));
        assertObjectsAreNotEqual(p,Constant.TRUE);
    }

    public void testTransposeNull() throws Exception {
        assertNull(TransposedPredicate.transpose(null));
    }

    public void testTranspose() throws Exception {
        assertNotNull(TransposedPredicate.transpose(new Constant(true)));
    }
}
