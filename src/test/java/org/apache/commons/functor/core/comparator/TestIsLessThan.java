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
public class TestIsLessThan extends BaseComparisonPredicateTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestIsLessThan(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestIsLessThan.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new IsLessThan();
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
        IsLessThan p = new IsLessThan();
        assertTrue(p.test(new Integer(2),new Integer(4)));
        assertTrue(p.test(new Integer(3),new Integer(4)));
        assertTrue(!p.test(new Integer(4),new Integer(4)));
        assertTrue(!p.test(new Integer(5),new Integer(4)));
        assertTrue(!p.test(new Integer(6),new Integer(4)));
    }

    public void testInstance() {
        assertTrue(! IsLessThan.instance(new Integer(7)).test(new Integer(8)));
        assertTrue(IsLessThan.instance(new Integer(7)).test(new Integer(6)));
    }

    public void testEquals() throws Exception {
        IsLessThan p = new IsLessThan();
        assertEquals(p,p);

        assertObjectsAreEqual(p,new IsLessThan());
        assertObjectsAreEqual(p,new IsLessThan(new ComparableComparator()));
        assertObjectsAreEqual(p,IsLessThan.instance());
        assertObjectsAreNotEqual(p,new Constant(false));
    }

}
