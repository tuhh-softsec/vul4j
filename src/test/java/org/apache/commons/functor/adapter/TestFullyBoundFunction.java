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
import org.apache.commons.functor.core.Constant;
import org.apache.commons.functor.core.LeftIdentity;
import org.apache.commons.functor.core.RightIdentity;

/**
 * @version $Revision$ $Date$
 * @author Matt Benson
 */
public class TestFullyBoundFunction extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestFullyBoundFunction(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestFullyBoundFunction.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new FullyBoundFunction<Object, Object, Object>(RightIdentity.FUNCTION, null, "xyzzy");
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
        Function<Object> f = new FullyBoundFunction<String, Object, Object>(RightIdentity.FUNCTION, null, "foo");
        assertEquals("foo", f.evaluate());
    }

    public void testEquals() throws Exception {
        Function<Object> f = new FullyBoundFunction<Object, Object, Object>(RightIdentity.FUNCTION, null, "xyzzy");
        assertEquals(f, f);
        assertObjectsAreEqual(f, new FullyBoundFunction<Object, Object, Object>(RightIdentity.FUNCTION, null, "xyzzy"));
        assertObjectsAreNotEqual(f, Constant.of("xyzzy"));
        assertObjectsAreNotEqual(f, new FullyBoundFunction<Object, Object, Object>(LeftIdentity.FUNCTION, null, "xyzzy"));
        assertObjectsAreNotEqual(f, new FullyBoundFunction<Object, Object, Object>(RightIdentity.FUNCTION, null, "bar"));
    }

    public void testAdaptNull() throws Exception {
        assertNull(FullyBoundFunction.bind(null, null, "xyzzy"));
    }

    public void testAdapt() throws Exception {
        assertNotNull(FullyBoundFunction.bind(RightIdentity.FUNCTION, "xyzzy", "foobar"));
        assertNotNull(FullyBoundFunction.bind(RightIdentity.FUNCTION, null, null));
    }
}
