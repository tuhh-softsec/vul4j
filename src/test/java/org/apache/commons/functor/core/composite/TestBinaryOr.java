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
import org.apache.commons.functor.BinaryPredicate;
import org.apache.commons.functor.core.Constant;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestBinaryOr extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestBinaryOr(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestBinaryOr.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new BinaryOr<Object, Object>(Constant.FALSE, Constant.TRUE);
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
        assertTrue((new BinaryOr<Object, Object>(Constant.TRUE)).test("xyzzy",new Integer(3)));
        assertTrue((new BinaryOr<Object, Object>(Constant.FALSE,Constant.TRUE)).test("xyzzy",new Integer(3)));
        assertTrue((new BinaryOr<Object, Object>(Constant.FALSE, Constant.FALSE, Constant.TRUE)).test("xyzzy",new Integer(3)));

        BinaryOr<Object, Object> p = new BinaryOr<Object, Object>(Constant.TRUE);
        assertTrue(p.test("xyzzy",new Integer(3)));
        for (int i=0;i<10;i++) {
            p.or(Constant.of(i%2==0));
            assertTrue(p.test("xyzzy",new Integer(3)));
        }

        BinaryOr<Object, Object> q = new BinaryOr<Object, Object>(Constant.TRUE);
        assertTrue(q.test("xyzzy",new Integer(3)));
        for (int i=0;i<10;i++) {
            q.or(Constant.of(i%2==0));
            assertTrue(q.test("xyzzy",new Integer(3)));
        }

        BinaryOr<Object, Object> r = new BinaryOr<Object, Object>(p,q);
        assertTrue(r.test("xyzzy",new Integer(3)));
    }

    public void testFalse() throws Exception {
        assertTrue(!(new BinaryOr<Object, Object>()).test("xyzzy",new Integer(3)));
        assertTrue(!(new BinaryOr<Object, Object>(Constant.FALSE)).test("xyzzy",new Integer(3)));
        assertTrue(!(new BinaryOr<Object, Object>(Constant.FALSE,Constant.FALSE)).test("xyzzy",new Integer(3)));
        assertTrue(!(new BinaryOr<Object, Object>(Constant.FALSE,Constant.FALSE,Constant.FALSE)).test("xyzzy",new Integer(3)));

        BinaryOr<Object, Object> p = new BinaryOr<Object, Object>(Constant.FALSE);
        assertTrue(!p.test("xyzzy",new Integer(3)));
        for (int i=0;i<10;i++) {
            p.or(Constant.FALSE);
            assertTrue(!p.test("xyzzy",new Integer(3)));
        }

        BinaryOr<Object, Object> q = new BinaryOr<Object, Object>(Constant.FALSE);
        assertTrue(!q.test("xyzzy",new Integer(3)));
        for (int i=0;i<10;i++) {
            q.or(Constant.FALSE);
            assertTrue(!q.test("xyzzy",new Integer(3)));
        }

        BinaryOr<Object, Object> r = new BinaryOr<Object, Object>(p,q);
        assertTrue(!r.test("xyzzy",new Integer(3)));
    }

    public void testDuplicateAdd() throws Exception {
        BinaryPredicate<Object, Object> p = Constant.TRUE;
        BinaryOr<Object, Object> q = new BinaryOr<Object, Object>(p,p);
        assertTrue(q.test("xyzzy",new Integer(3)));
        for (int i=0;i<10;i++) {
            q.or(p);
            assertTrue(q.test("xyzzy",new Integer(3)));
        }
    }

    public void testEquals() throws Exception {
        BinaryOr<Object, Object> p = new BinaryOr<Object, Object>();
        assertEquals(p,p);

        BinaryOr<Object, Object> q = new BinaryOr<Object, Object>();
        assertObjectsAreEqual(p,q);

        BinaryAnd<Object, Object> r = new BinaryAnd<Object, Object>();
        assertObjectsAreNotEqual(p,r);

        for (int i=0;i<3;i++) {
            p.or(Constant.TRUE);
            assertObjectsAreNotEqual(p,q);
            q.or(Constant.TRUE);
            assertObjectsAreEqual(p,q);
            r.and(Constant.TRUE);
            assertObjectsAreNotEqual(p,r);

            p.or(new BinaryOr<Object, Object>(Constant.truePredicate(),Constant.FALSE));
            assertObjectsAreNotEqual(p,q);
            q.or(new BinaryOr<Object, Object>(Constant.truePredicate(),Constant.FALSE));
            assertObjectsAreEqual(p,q);
            r.and(new BinaryOr<Object, Object>(Constant.truePredicate(),Constant.FALSE));
            assertObjectsAreNotEqual(p,r);
        }

        assertObjectsAreNotEqual(p,Constant.TRUE);
    }

}
