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
public class TestAnd extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestAnd(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestAnd.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new And(Constant.TRUE, Constant.TRUE);
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
        assertTrue((new And()).test());
        assertTrue((new And(Constant.TRUE)).test());
        assertTrue((new And(Constant.TRUE,Constant.TRUE)).test());
        assertTrue((new And(Constant.TRUE,Constant.TRUE,Constant.TRUE)).test());

        And p = new And(Constant.TRUE);
        assertTrue(p.test());
        for (int i=0;i<10;i++) {
            p.and(Constant.TRUE);
            assertTrue(p.test());
        }

        And q = new And(Constant.TRUE);
        assertTrue(q.test());
        for (int i=0;i<10;i++) {
            q.and(Constant.TRUE);
            assertTrue(q.test());
        }

        And r = new And(p,q);
        assertTrue(r.test());
    }

    public void testFalse() throws Exception {
        assertTrue(!(new And(Constant.FALSE)).test());
        assertTrue(!(new And(Constant.TRUE,Constant.FALSE)).test());
        assertTrue(!(new And(Constant.TRUE,Constant.TRUE,Constant.FALSE)).test());

        And p = new And(Constant.FALSE);
        assertTrue(!p.test());
        for (int i=0;i<10;i++) {
            p.and(Constant.FALSE);
            assertTrue(!p.test());
        }

        And q = new And(Constant.TRUE);
        assertTrue(q.test());
        for (int i=0;i<10;i++) {
            q.and(Constant.TRUE);
            assertTrue(q.test());
        }

        And r = new And(p,q);
        assertTrue(!r.test());
    }

    public void testDuplicateAdd() throws Exception {
        Predicate p = Constant.TRUE;
        And q = new And(p,p);
        assertTrue(q.test());
        for (int i=0;i<10;i++) {
            q.and(p);
            assertTrue(q.test());
        }
    }

    public void testEquals() throws Exception {
        And p = new And();
        assertEquals(p,p);
        And q = new And();
        assertObjectsAreEqual(p,q);

        for (int i=0;i<3;i++) {
            p.and(Constant.TRUE);
            assertObjectsAreNotEqual(p,q);
            q.and(Constant.truePredicate());
            assertObjectsAreEqual(p,q);
            p.and(new And(Constant.TRUE,Constant.FALSE));
            assertObjectsAreNotEqual(p,q);
            q.and(new And(Constant.TRUE,Constant.FALSE));
            assertObjectsAreEqual(p,q);
        }

        assertObjectsAreNotEqual(p,Constant.TRUE);
    }

}
