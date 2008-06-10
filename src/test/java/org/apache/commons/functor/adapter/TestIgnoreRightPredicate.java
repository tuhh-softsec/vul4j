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
import org.apache.commons.functor.core.Identity;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestIgnoreRightPredicate extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestIgnoreRightPredicate(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestIgnoreRightPredicate.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new IgnoreRightPredicate(new Constant(true));
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
        BinaryPredicate p = new IgnoreRightPredicate(new UnaryFunctionUnaryPredicate(new Identity()));
        assertTrue(p.test(Boolean.TRUE,null));
        assertTrue(!p.test(Boolean.FALSE,null));
    }

    public void testEquals() throws Exception {
        BinaryPredicate p = new IgnoreRightPredicate(new UnaryFunctionUnaryPredicate(new Identity()));
        assertEquals(p,p);
        assertObjectsAreEqual(p,new IgnoreRightPredicate(new UnaryFunctionUnaryPredicate(new Identity())));
        assertObjectsAreNotEqual(p,new Constant(true));
        assertObjectsAreNotEqual(p,new IgnoreRightPredicate(new Constant(false)));
        assertObjectsAreNotEqual(p,new Constant(false));
    }

    public void testAdaptNull() throws Exception {
        assertNull(IgnoreRightPredicate.adapt(null));
    }

    public void testAdapt() throws Exception {
        assertNotNull(IgnoreRightPredicate.adapt(new Constant(true)));
    }
}
