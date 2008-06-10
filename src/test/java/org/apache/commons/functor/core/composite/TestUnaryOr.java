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
public class TestUnaryOr extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestUnaryOr(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestUnaryOr.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new UnaryOr<Object>(Constant.FALSE,Constant.TRUE);
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
        assertTrue((new UnaryOr<Object>(Constant.TRUE)).test("xyzzy"));
        assertTrue((new UnaryOr<Object>(Constant.FALSE,Constant.TRUE)).test("xyzzy"));
        assertTrue((new UnaryOr<Object>(Constant.FALSE,Constant.FALSE,Constant.TRUE)).test("xyzzy"));

        UnaryOr<Object> p = new UnaryOr<Object>(Constant.TRUE);
        assertTrue(p.test("xyzzy"));
        for (int i=0;i<10;i++) {
            p.or(Constant.of(i%2==0));
            assertTrue(p.test("xyzzy"));
        }

        UnaryOr<Object> q = new UnaryOr<Object>(Constant.TRUE);
        assertTrue(q.test("xyzzy"));
        for (int i=0;i<10;i++) {
            q.or(Constant.of(i%2==0));
            assertTrue(q.test("xyzzy"));
        }

        UnaryOr<Object> r = new UnaryOr<Object>(p,q);
        assertTrue(r.test("xyzzy"));
    }

    public void testFalse() throws Exception {
        assertTrue(!(new UnaryOr<Object>()).test("xyzzy"));
        assertTrue(!(new UnaryOr<Object>(Constant.FALSE)).test("xyzzy"));
        assertTrue(!(new UnaryOr<Object>(Constant.FALSE,Constant.FALSE)).test("xyzzy"));
        assertTrue(!(new UnaryOr<Object>(Constant.FALSE,Constant.FALSE,Constant.FALSE)).test("xyzzy"));

        UnaryOr<Object> p = new UnaryOr<Object>(Constant.FALSE);
        assertTrue(!p.test("xyzzy"));
        for (int i=0;i<10;i++) {
            p.or(Constant.FALSE);
            assertTrue(!p.test("xyzzy"));
        }

        UnaryOr<Object> q = new UnaryOr<Object>(Constant.FALSE);
        assertTrue(!q.test("xyzzy"));
        for (int i=0;i<10;i++) {
            q.or(Constant.FALSE);
            assertTrue(!q.test("xyzzy"));
        }

        UnaryOr<Object> r = new UnaryOr<Object>(p,q);
        assertTrue(!r.test("xyzzy"));
    }

    public void testDuplicateAdd() throws Exception {
        UnaryPredicate<Object> p = Constant.TRUE;
        UnaryOr<Object> q = new UnaryOr<Object>(p,p);
        assertTrue(q.test("xyzzy"));
        for (int i=0;i<10;i++) {
            q.or(p);
            assertTrue(q.test("xyzzy"));
        }
    }

    public void testEquals() throws Exception {
        UnaryOr<Object> p = new UnaryOr<Object>();
        assertEquals(p,p);

        UnaryOr<Object> q = new UnaryOr<Object>();
        assertObjectsAreEqual(p,q);

        UnaryAnd<Object> r = new UnaryAnd<Object>();
        assertObjectsAreNotEqual(p,r);

        for (int i=0;i<3;i++) {
            p.or(Constant.truePredicate());
            assertObjectsAreNotEqual(p,q);
            q.or(Constant.truePredicate());
            assertObjectsAreEqual(p,q);
            r.and(Constant.truePredicate());
            assertObjectsAreNotEqual(p,r);

            p.or(new UnaryOr<Object>(Constant.truePredicate(),Constant.falsePredicate()));
            assertObjectsAreNotEqual(p,q);
            q.or(new UnaryOr<Object>(Constant.truePredicate(),Constant.falsePredicate()));
            assertObjectsAreEqual(p,q);
            r.and(new UnaryOr<Object>(Constant.truePredicate(),Constant.falsePredicate()));
            assertObjectsAreNotEqual(p,r);
        }

        assertObjectsAreNotEqual(p,Constant.truePredicate());
    }

}
