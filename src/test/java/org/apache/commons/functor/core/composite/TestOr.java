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
        return new Or(Constant.FALSE, Constant.TRUE);
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
        assertTrue((new Or(Constant.TRUE)).test());
        assertTrue((new Or(Constant.FALSE, Constant.TRUE)).test());
        assertTrue((new Or(Constant.FALSE, Constant.FALSE, Constant.TRUE)).test());

        Or p = new Or(Constant.TRUE);
        assertTrue(p.test());
        for (int i=0;i<10;i++) {
            p.or(Constant.of(i%2==0));
            assertTrue(p.test());
        }

        Or q = new Or(Constant.TRUE);
        assertTrue(q.test());
        for (int i=0;i<10;i++) {
            q.or(Constant.of(i%2==0));
            assertTrue(q.test());
        }

        Or r = new Or(p,q);
        assertTrue(r.test());
    }

    public void testFalse() throws Exception {
        assertFalse(new Or().test());
        assertFalse(new Or(Constant.FALSE).test());
        assertFalse(new Or(Constant.FALSE,Constant.FALSE).test());
        assertFalse(new Or(Constant.FALSE,Constant.FALSE,Constant.FALSE).test());

        Or p = new Or(Constant.FALSE);
        assertFalse(p.test());
        for (int i=0;i<10;i++) {
            p.or(Constant.FALSE);
            assertFalse(p.test());
        }

        Or q = new Or(Constant.FALSE);
        assertFalse(q.test());
        for (int i=0;i<10;i++) {
            q.or(Constant.FALSE);
            assertFalse(q.test());
        }

        Or r = new Or(p,q);
        assertTrue(!r.test());
    }

    public void testDuplicateAdd() throws Exception {
        Predicate p = Constant.TRUE;
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
            p.or(Constant.TRUE);
            assertObjectsAreNotEqual(p,q);
            q.or(Constant.TRUE);
            assertObjectsAreEqual(p,q);
            r.and(Constant.TRUE);
            assertObjectsAreNotEqual(p,r);

            p.or(new Or(Constant.TRUE,Constant.FALSE));
            assertObjectsAreNotEqual(p,q);
            q.or(new Or(Constant.TRUE,Constant.FALSE));
            assertObjectsAreEqual(p,q);
            r.and(new Or(Constant.TRUE,Constant.FALSE));
            assertObjectsAreNotEqual(p,r);
        }

        assertObjectsAreNotEqual(p,Constant.TRUE);
    }

}
