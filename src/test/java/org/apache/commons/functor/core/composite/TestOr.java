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
import org.apache.commons.functor.Predicate;
import org.apache.commons.functor.core.Constant;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestOr extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestOr(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestOr.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new Or(new Constant(false),new Constant(true));
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
        assertTrue((new Or(new Constant(true))).test());
        assertTrue((new Or(new Constant(false),new Constant(true))).test());
        assertTrue((new Or(new Constant(false),new Constant(false),new Constant(true))).test());

        Or p = new Or(new Constant(true));
        assertTrue(p.test());
        for (int i=0;i<10;i++) {
            p.or(new Constant(i%2==0));
            assertTrue(p.test());
        }

        Or q = new Or(new Constant(true));
        assertTrue(q.test());
        for (int i=0;i<10;i++) {
            q.or(new Constant(i%2==0));
            assertTrue(q.test());
        }

        Or r = new Or(p,q);
        assertTrue(r.test());
    }

    public void testFalse() throws Exception {
        assertTrue(!(new Or()).test());
        assertTrue(!(new Or(new Constant(false))).test());
        assertTrue(!(new Or(new Constant(false),new Constant(false))).test());
        assertTrue(!(new Or(new Constant(false),new Constant(false),new Constant(false))).test());

        Or p = new Or(new Constant(false));
        assertTrue(!p.test());
        for (int i=0;i<10;i++) {
            p.or(new Constant(false));
            assertTrue(!p.test());
        }

        Or q = new Or(new Constant(false));
        assertTrue(!q.test());
        for (int i=0;i<10;i++) {
            q.or(new Constant(false));
            assertTrue(!q.test());
        }

        Or r = new Or(p,q);
        assertTrue(!r.test());
    }

    public void testDuplicateAdd() throws Exception {
        Predicate p = new Constant(true);
        Or q = new Or(p,p);
        assertTrue(q.test());
        for (int i=0;i<10;i++) {
            q.or(p);
            assertTrue(q.test());
        }
    }

    public void testEquals() throws Exception {
        Or p = new Or();
        assertEquals(p,p);

        Or q = new Or();
        assertObjectsAreEqual(p,q);

        And r = new And();
        assertObjectsAreNotEqual(p,r);

        for (int i=0;i<3;i++) {
            p.or(Constant.truePredicate());
            assertObjectsAreNotEqual(p,q);
            q.or(Constant.truePredicate());
            assertObjectsAreEqual(p,q);
            r.and(Constant.truePredicate());
            assertObjectsAreNotEqual(p,r);

            p.or(new Or(Constant.truePredicate(),Constant.falsePredicate()));
            assertObjectsAreNotEqual(p,q);
            q.or(new Or(Constant.truePredicate(),Constant.falsePredicate()));
            assertObjectsAreEqual(p,q);
            r.and(new Or(Constant.truePredicate(),Constant.falsePredicate()));
            assertObjectsAreNotEqual(p,r);
        }

        assertObjectsAreNotEqual(p,Constant.truePredicate());
    }

}
