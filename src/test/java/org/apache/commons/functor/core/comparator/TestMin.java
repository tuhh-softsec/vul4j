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

import java.util.Collections;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.functor.BaseFunctorTest;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestMin extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestMin(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestMin.class);
    }

    // Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return Min.instance();
    }

    private Integer MIN = new Integer(Integer.MIN_VALUE);
    private Integer MINUS_TWO = new Integer(-2);
    private Integer ZERO = new Integer(0);
    private Integer ONE = new Integer(1);
    private Integer MAX = new Integer(Integer.MAX_VALUE);
    // Tests
    // ------------------------------------------------------------------------

    public void testEvaluate() {
        Min<Integer> f = Min.instance();
        assertEquals(ONE,f.evaluate(ONE,ONE));
        assertEquals(ZERO,f.evaluate(ZERO,ONE));
        assertEquals(ZERO,f.evaluate(ONE,ZERO));
        assertEquals(ONE,f.evaluate(ONE,MAX));
        assertEquals(MIN,f.evaluate(MIN,MAX));
        assertEquals(MIN,f.evaluate(MIN,MINUS_TWO));
    }

    @SuppressWarnings("unchecked")
    public void testEquals() {
        Min<Comparable<?>> f = Min.instance();
        assertObjectsAreEqual(f,f);
        assertObjectsAreEqual(f,Min.instance());
        assertObjectsAreEqual(f,new Min<Comparable<?>>(ComparableComparator.INSTANCE));
        assertObjectsAreNotEqual(f,new Min<Comparable<?>>(Collections.<Comparable<?>>reverseOrder()));
    }
}
