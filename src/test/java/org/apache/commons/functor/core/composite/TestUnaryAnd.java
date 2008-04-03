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
public class TestUnaryAnd extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestUnaryAnd(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestUnaryAnd.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new UnaryAnd(new Constant(true),new Constant(true));
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

    public void testTrue() throws Exception {
        assertTrue((new UnaryAnd()).test("xyzzy"));
        assertTrue((new UnaryAnd(new Constant(true))).test("xyzzy"));
        assertTrue((new UnaryAnd(new Constant(true),new Constant(true))).test("xyzzy"));
        assertTrue((new UnaryAnd(new Constant(true),new Constant(true),new Constant(true))).test("xyzzy"));

        UnaryAnd p = new UnaryAnd(new Constant(true));
        assertTrue(p.test("xyzzy"));
        for(int i=0;i<10;i++) {
            p.and(new Constant(true));
            assertTrue(p.test("xyzzy"));
        }

        UnaryAnd q = new UnaryAnd(new Constant(true));
        assertTrue(q.test("xyzzy"));
        for(int i=0;i<10;i++) {
            q.and(new Constant(true));
            assertTrue(q.test("xyzzy"));
        }

        UnaryAnd r = new UnaryAnd(p,q);
        assertTrue(r.test("xyzzy"));
    }

    public void testFalse() throws Exception {
        assertTrue(!(new UnaryAnd(new Constant(false))).test("xyzzy"));
        assertTrue(!(new UnaryAnd(new Constant(true),new Constant(false))).test("xyzzy"));
        assertTrue(!(new UnaryAnd(new Constant(true),new Constant(true),new Constant(false))).test("xyzzy"));

        UnaryAnd p = new UnaryAnd(new Constant(false));
        assertTrue(!p.test("xyzzy"));
        for(int i=0;i<10;i++) {
            p.and(new Constant(false));
            assertTrue(!p.test("xyzzy"));
        }

        UnaryAnd q = new UnaryAnd(new Constant(true));
        assertTrue(q.test("xyzzy"));
        for(int i=0;i<10;i++) {
            q.and(new Constant(true));
            assertTrue(q.test("xyzzy"));
        }

        UnaryAnd r = new UnaryAnd(p,q);
        assertTrue(!r.test("xyzzy"));
    }

    public void testDuplicateAdd() throws Exception {
        UnaryPredicate p = new Constant(true);
        UnaryAnd q = new UnaryAnd(p,p);
        assertTrue(q.test("xyzzy"));
        for(int i=0;i<10;i++) {
            q.and(p);
            assertTrue(q.test("xyzzy"));
        }
    }

    public void testEquals() throws Exception {
        UnaryAnd p = new UnaryAnd();
        assertEquals(p,p);
        UnaryAnd q = new UnaryAnd();
        assertObjectsAreEqual(p,q);

        for(int i=0;i<3;i++) {
            p.and(Constant.truePredicate());
            assertObjectsAreNotEqual(p,q);
            q.and(Constant.truePredicate());
            assertObjectsAreEqual(p,q);
            p.and(new UnaryAnd(Constant.truePredicate(),Constant.falsePredicate()));
            assertObjectsAreNotEqual(p,q);
            q.and(new UnaryAnd(Constant.truePredicate(),Constant.falsePredicate()));
            assertObjectsAreEqual(p,q);
        }

        assertObjectsAreNotEqual(p,Constant.truePredicate());
    }

}
