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
import org.apache.commons.functor.Predicate;
import org.apache.commons.functor.core.Constant;
import org.apache.commons.functor.core.RightIdentity;

/**
 * @version $Revision$ $Date$
 * @author Matt Benson
 */
public class TestFullyBoundPredicate extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestFullyBoundPredicate(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestFullyBoundPredicate.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new FullyBoundPredicate<Object, Object>(Constant.TRUE, null, "xyzzy");
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
        Predicate p = new FullyBoundPredicate<Object, Boolean>(
                new BinaryFunctionBinaryPredicate<Object, Boolean>(RightIdentity.<Object, Boolean> function()), "foo", Boolean.TRUE);
        assertEquals(true, p.test());
    }

    public void testEquals() throws Exception {
        Predicate p = new FullyBoundPredicate<Object, Boolean>(Constant.TRUE, "xyzzy", null);
        assertEquals(p, p);
        assertObjectsAreEqual(p, new FullyBoundPredicate<Object, Boolean>(Constant.TRUE, "xyzzy", null));
        assertObjectsAreNotEqual(p, Constant.TRUE);
        assertObjectsAreNotEqual(p, new FullyBoundPredicate<Object, Boolean>(Constant.FALSE, "xyzzy", null));
        assertObjectsAreNotEqual(p, new FullyBoundPredicate<Object, Boolean>(Constant.TRUE, "foo", null));
        assertObjectsAreNotEqual(p, new FullyBoundPredicate<Object, String>(Constant.TRUE, null, "xyzzy"));
    }

    public void testAdaptNull() throws Exception {
        assertNull(FullyBoundPredicate.bind(null, "xyzzy", null));
    }

    public void testAdapt() throws Exception {
        assertNotNull(FullyBoundPredicate.bind(Constant.FALSE, "xyzzy", "foobar"));
        assertNotNull(FullyBoundPredicate.bind(Constant.FALSE, null, null));
    }
}
