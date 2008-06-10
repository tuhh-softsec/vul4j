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
package org.apache.commons.functor.core;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.functor.BaseFunctorTest;
import org.apache.commons.functor.BinaryPredicate;
import org.apache.commons.functor.Predicate;
import org.apache.commons.functor.UnaryPredicate;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestLimit extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestLimit(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestLimit.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new Limit(3);
    }

    // Lifecycle
    // ------------------------------------------------------------------------

    // Tests
    // ------------------------------------------------------------------------

    public void testZero() throws Exception {
        Predicate p = new Limit(0);
        assertTrue(! p.test());
        assertTrue(! p.test());
        assertTrue(! p.test());
    }

    public void testBadArgs() throws Exception {
        try {
            new Limit(-1);
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // expected
        }
    }

    public void testTestNullary() throws Exception {
        Predicate p = new Limit(3);
        assertTrue(p.test());
        assertTrue(p.test());
        assertTrue(p.test());
        assertTrue(! p.test());
    }

    public void testTestUnary() throws Exception {
        UnaryPredicate<Object> p = new Limit(3);
        assertTrue(p.test(null));
        assertTrue(p.test(null));
        assertTrue(p.test(null));
        assertTrue(! p.test(null));
    }

    public void testTestBinary() throws Exception {
        BinaryPredicate<Object, Object> p = new Limit(3);
        assertTrue(p.test(null,null));
        assertTrue(p.test(null,null));
        assertTrue(p.test(null,null));
        assertTrue(! p.test(null,null));
    }
}
