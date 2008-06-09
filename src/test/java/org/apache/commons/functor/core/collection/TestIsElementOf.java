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
package org.apache.commons.functor.core.collection;

import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.functor.BaseFunctorTest;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.core.Constant;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 * @author Jason Horman
 */
@SuppressWarnings("unchecked")
public class TestIsElementOf extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestIsElementOf(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestIsElementOf.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new IsElementOf();
    }

    // Lifecycle
    // ------------------------------------------------------------------------

    // Tests
    // ------------------------------------------------------------------------

    public void testTestCollection() throws Exception {
        ArrayList list = new ArrayList();
        list.add(new Integer(5));
        list.add(new Integer(10));
        list.add(new Integer(15));

        UnaryPredicate p = IsElementOf.instance(list);
        assertTrue(p.test(new Integer(5)));
        assertTrue(p.test(new Integer(10)));
        assertTrue(p.test(new Integer(15)));

        assertTrue(!p.test(new Integer(4)));
        assertTrue(!p.test(new Integer(11)));

    }

    public void testTestArray() throws Exception {
        int[] list = new int[] { 5, 10, 15 };

        UnaryPredicate p = IsElementOf.instance(list);
        assertTrue(p.test(new Integer(5)));
        assertTrue(p.test(new Integer(10)));
        assertTrue(p.test(new Integer(15)));

        assertTrue(!p.test(new Integer(4)));
        assertTrue(!p.test(new Integer(11)));
    }

    public void testTestArrayWithNull() throws Exception {
        assertTrue(! IsElementOf.instance().test(null,new int[] { 5, 10, 15 }));
        assertTrue(IsElementOf.instance().test(null,new Integer[] { new Integer(5), null, new Integer(15) }));
        assertTrue(IsElementOf.instance().test(new Integer(15),new Integer[] { new Integer(5), null, new Integer(15) }));
    }

    public void testWrapNull() {
        try {
            IsElementOf.instance(null);
            fail("expected NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    public void testWrapNonCollection() {
        try {
            IsElementOf.instance(new Integer(3));
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testTestNull() {
        try {
            IsElementOf.instance().test(new Integer(5),null);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testTestNonCollection() {
        try {
            IsElementOf.instance().test(new Integer(5),new Long(5));
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testEquals() throws Exception {
        IsElementOf p1 = new IsElementOf();
        assertObjectsAreEqual(p1, p1);
        assertObjectsAreEqual(p1, new IsElementOf());
        assertObjectsAreEqual(p1, IsElementOf.instance());
        assertSame(IsElementOf.instance(), IsElementOf.instance());
        assertObjectsAreNotEqual(p1, Constant.falsePredicate());
    }
}
