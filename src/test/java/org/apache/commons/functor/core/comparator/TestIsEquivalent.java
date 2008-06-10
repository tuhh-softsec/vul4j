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
package org.apache.commons.functor.core.comparator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.functor.core.Constant;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestIsEquivalent extends BaseComparisonPredicateTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestIsEquivalent(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestIsEquivalent.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return IsEquivalent.INSTANCE;
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
        IsEquivalent<Integer> p = IsEquivalent.<Integer>instance();
        assertTrue(!p.test(new Integer(2),new Integer(4)));
        assertTrue(!p.test(new Integer(3),new Integer(4)));
        assertTrue(p.test(new Integer(4),new Integer(4)));
        assertTrue(!p.test(new Integer(5),new Integer(4)));
        assertTrue(!p.test(new Integer(6),new Integer(4)));
    }

    public void testInstance() {
        assertTrue(IsEquivalent.instance("Xyzzy").test("Xyzzy"));
        assertTrue(!IsEquivalent.instance("Xyzzy").test("z"));
    }

    @SuppressWarnings("unchecked")
    public void testEquals() throws Exception {
        IsEquivalent<Comparable<?>> p = IsEquivalent.INSTANCE;
        assertEquals(p,p);

        assertObjectsAreEqual(p,IsEquivalent.instance());
        assertObjectsAreEqual(p,new IsEquivalent<Comparable<?>>(new ComparableComparator()));
        assertObjectsAreNotEqual(p,Constant.FALSE);
    }

}
