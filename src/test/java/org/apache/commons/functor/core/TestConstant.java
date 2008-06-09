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

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestConstant extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestConstant(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestConstant.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new Constant("K");
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
        Constant f = new Constant("xyzzy");
        assertEquals("xyzzy",f.evaluate());
        assertEquals("xyzzy",f.evaluate(null));
        assertEquals("xyzzy",f.evaluate(null,null));
        assertEquals("xyzzy",f.evaluate());
        assertEquals("xyzzy",f.evaluate("foo"));
        assertEquals("xyzzy",f.evaluate("foo",new Integer(2)));
    }

    public void testEvaluateConstantNull() throws Exception {
        Constant f = new Constant(null);
        assertNull(f.evaluate());
        assertNull(f.evaluate(null));
        assertNull(f.evaluate(null,null));
        assertNull(f.evaluate());
        assertNull(f.evaluate("foo"));
        assertNull(f.evaluate("foo",new Integer(2)));
    }

    public void testConstantTrue() throws Exception {
        Constant truePred = new Constant(true);
        assertTrue(truePred.test());
        assertTrue(truePred.test(null));
        assertTrue(truePred.test(null,null));

        assertTrue(truePred.test());
        assertTrue(truePred.test("foo"));
        assertTrue(truePred.test("foo",new Integer(2)));
    }

    public void testConstantFalse() throws Exception {
        Constant falsePred = new Constant(false);
        assertTrue(!falsePred.test());
        assertTrue(!falsePred.test(null));
        assertTrue(!falsePred.test(null,null));

        assertTrue(!falsePred.test());
        assertTrue(!falsePred.test("foo"));
        assertTrue(!falsePred.test("foo",new Integer(2)));
    }

    public void testEquals() throws Exception {
        Constant f = new Constant("xyzzy");
        assertEquals(f,f);

        assertObjectsAreEqual(f,new Constant("xyzzy"));
        assertObjectsAreNotEqual(f,new Constant("abcde"));
        assertObjectsAreNotEqual(f,new Constant(null));
    }

    public void testConstants() throws Exception {
        assertEquals(Constant.predicate(true),Constant.of(Boolean.TRUE));

        assertEquals(Constant.truePredicate(),Constant.truePredicate());
        assertSame(Constant.truePredicate(),Constant.truePredicate());

        assertEquals(Constant.predicate(true),Constant.truePredicate());
        assertSame(Constant.predicate(true),Constant.truePredicate());

        assertEquals(Constant.falsePredicate(),Constant.falsePredicate());
        assertSame(Constant.falsePredicate(),Constant.falsePredicate());

        assertEquals(Constant.predicate(false),Constant.falsePredicate());
        assertSame(Constant.predicate(false),Constant.falsePredicate());
    }


}
