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
public class TestComparatorFunction extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestComparatorFunction(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestComparatorFunction.class);
    }

    // Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return ComparatorFunction.INSTANCE;
    }

    // Tests
    // ------------------------------------------------------------------------

    public void testEvaluate() {
        ComparatorFunction<Integer> f = ComparatorFunction.<Integer>instance();

        assertTrue(((Integer)(f.evaluate(new Integer(Integer.MAX_VALUE),new Integer(Integer.MAX_VALUE)))).intValue() == 0);
        assertTrue(((Integer)(f.evaluate(new Integer(Integer.MAX_VALUE),new Integer(1)))).intValue() > 0);
        assertTrue(((Integer)(f.evaluate(new Integer(Integer.MAX_VALUE),new Integer(0)))).intValue() > 0);
        assertTrue(((Integer)(f.evaluate(new Integer(Integer.MAX_VALUE),new Integer(-1)))).intValue() > 0);
        assertTrue(((Integer)(f.evaluate(new Integer(Integer.MAX_VALUE),new Integer(Integer.MIN_VALUE)))).intValue() > 0);

        assertTrue(((Integer)(f.evaluate(new Integer(1),new Integer(Integer.MAX_VALUE)))).intValue() < 0);
        assertTrue(((Integer)(f.evaluate(new Integer(1),new Integer(1)))).intValue() == 0);
        assertTrue(((Integer)(f.evaluate(new Integer(1),new Integer(0)))).intValue() > 0);
        assertTrue(((Integer)(f.evaluate(new Integer(1),new Integer(-1)))).intValue() > 0);
        assertTrue(((Integer)(f.evaluate(new Integer(1),new Integer(Integer.MIN_VALUE)))).intValue() > 0);

        assertTrue(((Integer)(f.evaluate(new Integer(0),new Integer(Integer.MAX_VALUE)))).intValue() < 0);
        assertTrue(((Integer)(f.evaluate(new Integer(0),new Integer(1)))).intValue() < 0);
        assertTrue(((Integer)(f.evaluate(new Integer(0),new Integer(0)))).intValue() == 0);
        assertTrue(((Integer)(f.evaluate(new Integer(0),new Integer(-1)))).intValue() > 0);
        assertTrue(((Integer)(f.evaluate(new Integer(0),new Integer(Integer.MIN_VALUE)))).intValue() > 0);

        assertTrue(((Integer)(f.evaluate(new Integer(-1),new Integer(Integer.MAX_VALUE)))).intValue() < 0);
        assertTrue(((Integer)(f.evaluate(new Integer(-1),new Integer(1)))).intValue() < 0);
        assertTrue(((Integer)(f.evaluate(new Integer(-1),new Integer(0)))).intValue() < 0);
        assertTrue(((Integer)(f.evaluate(new Integer(-1),new Integer(-1)))).intValue() == 0);
        assertTrue(((Integer)(f.evaluate(new Integer(-1),new Integer(Integer.MIN_VALUE)))).intValue() > 0);

        assertTrue(((Integer)(f.evaluate(new Integer(Integer.MIN_VALUE),new Integer(Integer.MAX_VALUE)))).intValue() < 0);
        assertTrue(((Integer)(f.evaluate(new Integer(Integer.MIN_VALUE),new Integer(1)))).intValue() < 0);
        assertTrue(((Integer)(f.evaluate(new Integer(Integer.MIN_VALUE),new Integer(0)))).intValue() < 0);
        assertTrue(((Integer)(f.evaluate(new Integer(Integer.MIN_VALUE),new Integer(-1)))).intValue() < 0);
        assertTrue(((Integer)(f.evaluate(new Integer(Integer.MIN_VALUE),new Integer(Integer.MIN_VALUE)))).intValue() == 0);
    }

    @SuppressWarnings("unchecked")
    public void testEquals() {
        ComparatorFunction<Comparable<?>> f = ComparatorFunction.instance();
        assertObjectsAreEqual(f,f);
        assertObjectsAreEqual(f,new ComparatorFunction<Comparable<?>>(ComparableComparator.instance()));
        assertObjectsAreNotEqual(f,new ComparatorFunction(Collections.reverseOrder()));
    }
}
