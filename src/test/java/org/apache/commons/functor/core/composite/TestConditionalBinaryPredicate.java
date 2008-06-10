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
import org.apache.commons.functor.core.Constant;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestConditionalBinaryPredicate extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestConditionalBinaryPredicate(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestConditionalBinaryPredicate.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new ConditionalBinaryPredicate<Object, Object>(
            Constant.TRUE,
            Constant.FALSE,
            Constant.TRUE);
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
        {
            ConditionalBinaryPredicate<Object, Object> p = new ConditionalBinaryPredicate<Object, Object>(
                Constant.TRUE,
                Constant.TRUE,
                Constant.FALSE);
            assertTrue(p.test(null,null));
        }
        {
            ConditionalBinaryPredicate<Object, Object> p = new ConditionalBinaryPredicate<Object, Object>(
                Constant.FALSE,
                Constant.TRUE,
                Constant.FALSE);
            assertFalse(p.test(null,null));
        }
    }

    public void testEquals() throws Exception {
        ConditionalBinaryPredicate<Object, Object> p = new ConditionalBinaryPredicate<Object, Object>(
            Constant.TRUE,
            Constant.TRUE,
            Constant.FALSE);
        assertEquals(p,p);
        assertObjectsAreEqual(p,new ConditionalBinaryPredicate<Object, Object>(
            Constant.TRUE,
            Constant.TRUE,
            Constant.FALSE));
        assertObjectsAreNotEqual(p,new ConditionalBinaryPredicate<Object, Object>(
            Constant.TRUE,
            Constant.FALSE,
            Constant.TRUE));
        assertObjectsAreNotEqual(p,new ConditionalBinaryPredicate<Object, Object>(
            Constant.TRUE,
            Constant.TRUE,
            Constant.TRUE));
    }
}
