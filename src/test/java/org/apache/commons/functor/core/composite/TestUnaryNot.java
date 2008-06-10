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
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.core.Constant;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestUnaryNot extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestUnaryNot(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestUnaryNot.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new UnaryNot<Object>(Constant.TRUE);
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
        UnaryPredicate<Object> truePred = new UnaryNot<Object>(Constant.FALSE);
        assertTrue(truePred.test(null));
        assertTrue(truePred.test("xyzzy"));
        assertTrue(truePred.test(new Integer(3)));
    }

    public void testEquals() throws Exception {
        UnaryNot<Object> p = new UnaryNot<Object>(Constant.TRUE);
        assertEquals(p,p);
        assertObjectsAreEqual(p,new UnaryNot<Object>(Constant.TRUE));
        assertObjectsAreEqual(p,UnaryNot.not(Constant.TRUE));
        assertObjectsAreNotEqual(p,new UnaryNot<Object>(Constant.FALSE));
        assertObjectsAreNotEqual(p,Constant.TRUE);
    }

    public void testNotNull() throws Exception {
        assertNull(UnaryNot.not(null));
    }

    public void testNotNotNull() throws Exception {
        assertNotNull(UnaryNot.not(Constant.truePredicate()));
    }
}
