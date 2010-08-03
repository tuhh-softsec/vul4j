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
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.core.Constant;
import org.apache.commons.functor.core.RightIdentity;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestLeftBoundPredicate extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestLeftBoundPredicate(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestLeftBoundPredicate.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new LeftBoundPredicate<Object>(Constant.TRUE,"xyzzy");
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

    public void testTest() throws Exception {
        UnaryPredicate<Boolean> p = new LeftBoundPredicate<Boolean>(
                new BinaryFunctionBinaryPredicate<Object, Boolean>(RightIdentity.<Object, Boolean> function()), "foo");
        assertEquals(true,p.test(Boolean.TRUE));
        assertEquals(false,p.test(Boolean.FALSE));
    }

    public void testEquals() throws Exception {
        UnaryPredicate<Boolean> p = new LeftBoundPredicate<Boolean>(Constant.TRUE,"xyzzy");
        assertEquals(p,p);
        assertObjectsAreEqual(p,new LeftBoundPredicate<Boolean>(Constant.TRUE,"xyzzy"));
        assertObjectsAreNotEqual(p,Constant.TRUE);
        assertObjectsAreNotEqual(p,new LeftBoundPredicate<Boolean>(Constant.FALSE,"xyzzy"));
        assertObjectsAreNotEqual(p,new LeftBoundPredicate<Boolean>(Constant.TRUE,"foo"));
        assertObjectsAreNotEqual(p,new LeftBoundPredicate<Boolean>(Constant.TRUE,null));
    }

    public void testAdaptNull() throws Exception {
        assertNull(LeftBoundPredicate.bind(null,"xyzzy"));
    }

    public void testAdapt() throws Exception {
        assertNotNull(LeftBoundPredicate.bind(Constant.FALSE,"xyzzy"));
        assertNotNull(LeftBoundPredicate.bind(Constant.FALSE,null));
    }
}
