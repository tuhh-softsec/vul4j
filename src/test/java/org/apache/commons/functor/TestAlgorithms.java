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
package org.apache.commons.functor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.functor.adapter.LeftBoundPredicate;
import org.apache.commons.functor.core.Identity;
import org.apache.commons.functor.core.IsEqual;
import org.apache.commons.functor.core.Limit;
import org.apache.commons.functor.core.Offset;
import org.apache.commons.functor.generator.Generator;
import org.apache.commons.functor.generator.IteratorToGeneratorAdapter;
import org.apache.commons.functor.generator.util.IntegerRange;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestAlgorithms extends TestCase {

    // Conventional
    // ------------------------------------------------------------------------

    public TestAlgorithms(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestAlgorithms.class);
    }

    // Lifecycle
    // ------------------------------------------------------------------------

    public void setUp() throws Exception {
        super.setUp();
        list = new ArrayList();
        evens = new ArrayList();
        doubled = new ArrayList();
        listWithDuplicates = new ArrayList();
        sum = 0;
        for (int i=0;i<10;i++) {
            list.add(new Integer(i));
            doubled.add(new Integer(i*2));
            listWithDuplicates.add(new Integer(i));
            listWithDuplicates.add(new Integer(i));
            sum += i;
            if (i%2 == 0) {
                evens.add(new Integer(i));
            }
        }
    }

    public void tearDown() throws Exception {
        super.tearDown();
        list = null;
        evens = null;
        listWithDuplicates = null;
        sum = 0;
    }

    // Tests
    // ------------------------------------------------------------------------

    public void testCollect() {
        Collection result = Algorithms.collect(list.iterator());
        assertNotNull(result);
        assertEquals(list.size(),result.size());
        assertEquals(list,result);
    }

    public void testCollect2() {
        Set set = new HashSet();
        assertSame(set,Algorithms.collect(list.iterator(),set));
        assertEquals(list.size(),set.size());
        for (Iterator iter = list.iterator(); iter.hasNext(); ) {
            assertTrue(set.contains(iter.next()));
        }
    }

    public void testCollect3() {
        Set set = new HashSet();
        assertSame(set,Algorithms.collect(listWithDuplicates.iterator(),set));
        assertTrue(listWithDuplicates.size() > set.size());
        for (Iterator iter = listWithDuplicates.iterator(); iter.hasNext(); ) {
            assertTrue(set.contains(iter.next()));
        }
    }

    public void testDetect() {
        assertEquals(new Integer(3),Algorithms.detect(IteratorToGeneratorAdapter.adapt(list.iterator()),equalsThree));
        try {
            Algorithms.detect(IteratorToGeneratorAdapter.adapt(list.iterator()),equalsTwentyThree);
            fail("Expected NoSuchElementException");
        } catch(NoSuchElementException e) {
            // expected
        }
    }

    public void testDetectIfNone() {
        assertEquals(new Integer(3),Algorithms.detect(IteratorToGeneratorAdapter.adapt(list.iterator()),equalsThree,"Xyzzy"));
        assertEquals("Xyzzy",Algorithms.detect(IteratorToGeneratorAdapter.adapt(list.iterator()),equalsTwentyThree,"Xyzzy"));
    }

    public void testForEach() {
        Summer summer = new Summer();
        Algorithms.foreach(IteratorToGeneratorAdapter.adapt(list.iterator()),summer);
        assertEquals(sum,summer.sum);
    }

    public void testSelect1() {
        Collection result = Algorithms.select(IteratorToGeneratorAdapter.adapt(list.iterator()),isEven).toCollection();
        assertNotNull(result);
        assertEquals(evens,result);
    }

    public void testSelect2() {
        ArrayList result = new ArrayList();
        assertSame(result,Algorithms.select(IteratorToGeneratorAdapter.adapt(list.iterator()),isEven).to(result));
        assertEquals(evens,result);
    }

    public void testReject1() {
        Collection result = Algorithms.reject(IteratorToGeneratorAdapter.adapt(list.iterator()),isOdd).toCollection();
        assertNotNull(result);
        assertEquals(evens,result);
    }

    public void testReject2() {
        ArrayList result = new ArrayList();
        assertSame(result,Algorithms.reject(IteratorToGeneratorAdapter.adapt(list.iterator()),isOdd).to(result));
        assertEquals(evens,result);
    }

    public void testRetain() {
        Algorithms.retain(list.iterator(),isEven);
        assertEquals(evens,list);
    }

    public void testRemove() {
        Algorithms.remove(list.iterator(),isOdd);
        assertEquals(evens,list);
    }

    public void testTransform() {
        Algorithms.transform(
            list.listIterator(),
            new UnaryFunction() {
                public Object evaluate(Object obj) {
                    return new Integer(((Number) obj).intValue()*2);
                }
            }
        );
        assertEquals(doubled,list);
    }

    public void testHasPublicConstructor() {
        // some frameworks work best with instantiable classes
        assertNotNull(new Algorithms());
    }

    public void testApplyToGenerator() {
        Generator gen = new IntegerRange(1,5);
        Summer summer = new Summer();

        Algorithms.apply(gen,new Doubler()).run(summer);

        assertEquals(2*(1+2+3+4),summer.sum);
    }

    public void testApply() {
        Collection result = Algorithms.apply(IteratorToGeneratorAdapter.adapt(list.iterator()), new Doubler())
                .toCollection();
        assertNotNull(result);
        assertEquals(doubled,result);
    }

    public void testApply2() {
        Set set = new HashSet();
        assertSame(set, Algorithms.apply(IteratorToGeneratorAdapter.adapt(list.iterator()), Identity.instance())
                .to(set));
        assertEquals(list.size(),set.size());
        for (Iterator iter = list.iterator(); iter.hasNext(); ) {
            assertTrue(set.contains(iter.next()));
        }
    }

    public void testApply3() {
        Set set = new HashSet();
        assertSame(set, Algorithms.apply(IteratorToGeneratorAdapter.adapt(listWithDuplicates.iterator()),
                Identity.instance()).to(set));
        assertTrue(listWithDuplicates.size() > set.size());
        for (Iterator iter = listWithDuplicates.iterator(); iter.hasNext(); ) {
            assertTrue(set.contains(iter.next()));
        }
    }

    public void testContains() {
        assertTrue(Algorithms.contains(IteratorToGeneratorAdapter.adapt(list.iterator()),equalsThree));
        assertTrue(!Algorithms.contains(IteratorToGeneratorAdapter.adapt(list.iterator()),equalsTwentyThree));
    }

    public void testInject() {
        Object result = Algorithms.inject(IteratorToGeneratorAdapter.adapt(
            list.iterator()),
            new Integer(0),
            new BinaryFunction() {
                public Object evaluate(Object a, Object b) {
                    return new Integer(((Number) a).intValue() + ((Number) b).intValue());
                }
            });
        assertEquals(new Integer(sum),result);
    }

    public void testLimit() {
        Collection col = Algorithms.until(IteratorToGeneratorAdapter.adapt(list.iterator()), new Offset(2)).toCollection();
        assertEquals("[0, 1]", col.toString());
    }

    public void testDoUntil() {
        for (int i=0;i<3;i++){
            Counter counter = new Counter();
            Algorithms.dountil(counter,new Offset(i));
            assertEquals(i+1,counter.count);
        }
    }

    public void testDoWhile() {
        for (int i=0;i<3;i++){
            Counter counter = new Counter();
            Algorithms.dowhile(counter,new Limit(i));
            assertEquals(i+1,counter.count);
        }
    }

    public void testUntilDo() {
        for (int i=0;i<3;i++){
            Counter counter = new Counter();
            Algorithms.untildo(new Offset(i),counter);
            assertEquals(i,counter.count);
        }
    }

    public void testWhileDo() {
        for (int i=0;i<3;i++){
            Counter counter = new Counter();
            Algorithms.whiledo(new Limit(i),counter);
            assertEquals(i,counter.count);
        }
    }
    public void testRecurse() {
        assertEquals(new Integer(5), Algorithms.recurse(new RecFunc(0, false)));

        // this version will return a function. since it is not the same type
        // as RecFunc recursion will end.
        Function func = (Function) Algorithms.recurse(new RecFunc(0, true));
        assertEquals(new Integer(5), func.evaluate());
    }

    /** Recursive function for test. */
    class RecFunc implements Function {
        int times = 0; boolean returnFunc = false;

        public RecFunc(int times, boolean returnFunc) {
            this.times = times;
            this.returnFunc = returnFunc;
        }

        public Object evaluate() {
            if (times < 5) {
                return new RecFunc(++times, returnFunc);
            } else {
                if (returnFunc) {
                    return new Function() {
                        public Object evaluate() {
                            return new Integer(times);
                        }
                    };
                } else {
                    return new Integer(times);
                }
            }
        }
    }

    // Attributes
    // ------------------------------------------------------------------------
    private List list = null;
    private List doubled = null;
    private List evens = null;
    private List listWithDuplicates = null;
    private int sum = 0;
    private UnaryPredicate equalsThree = LeftBoundPredicate.bind(IsEqual.instance(),new Integer(3));
    private UnaryPredicate equalsTwentyThree = LeftBoundPredicate.bind(IsEqual.instance(),new Integer(23));
    private UnaryPredicate isEven = new UnaryPredicate() {
        public boolean test(Object obj) {
            return ((Number) obj).intValue() % 2 == 0;
        }
    };
    private UnaryPredicate isOdd = new UnaryPredicate() {
        public boolean test(Object obj) {
            return ((Number) obj).intValue() % 2 != 0;
        }
    };

    // Classes
    // ------------------------------------------------------------------------

    static class Counter implements Procedure {
        public void run() {
            count++;
        }
        public int count = 0;
    }

    static class Summer implements UnaryProcedure {
        public void run(Object that) {
            sum += ((Number) that).intValue();
        }
        public int sum = 0;
    }

    static class Doubler implements UnaryFunction {
        public Object evaluate(Object obj) {
            return new Integer(2*((Number) obj).intValue());
        }
    }
}
