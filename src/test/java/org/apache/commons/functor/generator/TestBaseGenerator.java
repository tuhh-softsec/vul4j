/*
 * Copyright 2003,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.functor.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.UnaryProcedure;
import org.apache.commons.functor.adapter.LeftBoundPredicate;
import org.apache.commons.functor.core.IsEqual;
import org.apache.commons.functor.core.Offset;
import org.apache.commons.functor.generator.util.CollectionTransformer;
import org.apache.commons.functor.generator.util.EachElement;
import org.apache.commons.functor.generator.util.IntegerRange;

/**
 * Tests the Base Generator class.
 * @author Jason Horman (jason@jhorman.org)
 */

public class TestBaseGenerator extends TestCase {

    private Generator simpleGenerator = null;

    // Conventional
    // ------------------------------------------------------------------------

    public TestBaseGenerator(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestBaseGenerator.class);
    }

    // Lifecycle
    // ------------------------------------------------------------------------

    public void setUp() throws Exception {
        super.setUp();

        simpleGenerator = new BaseGenerator() {
            public void run(UnaryProcedure proc) {
                for (int i=0;i<5;i++) {
                    proc.run(new Integer(i));
                    if (isStopped()) {
                        break;
                    }
                }
            }
        };

        list = new ArrayList();
        evens = new ArrayList();
        doubled = new ArrayList();
        listWithDuplicates = new ArrayList();
        sum = 0;
        for(int i=0;i<10;i++) {
            list.add(new Integer(i));
            doubled.add(new Integer(i*2));
            listWithDuplicates.add(new Integer(i));
            listWithDuplicates.add(new Integer(i));
            sum += i;
            if(i%2 == 0) {
                evens.add(new Integer(i));
            }
        }
    }

    public void tearDown() throws Exception {
        super.tearDown();
        simpleGenerator = null;
        list = null;
        evens = null;
        listWithDuplicates = null;
        sum = 0;
    }

    // Tests
    // ------------------------------------------------------------------------

    public void testSimpleGenerator() {
        final StringBuffer result = new StringBuffer();
        simpleGenerator.run(new UnaryProcedure() {
            public void run(Object obj) {
                result.append(obj);
            }
        });

        assertEquals("01234", result.toString());
    }

    public void testStop() {
        final StringBuffer result = new StringBuffer();
        simpleGenerator.run(new UnaryProcedure() {
            int i=0;
            public void run(Object obj) {
                result.append(obj);
                if (i++ > 1) {
                    simpleGenerator.stop();
                }
            }
        });

        assertEquals("012", result.toString());
    }

    public void testWrappingGenerator() {
        final StringBuffer result = new StringBuffer();
        final Generator gen = new BaseGenerator(simpleGenerator) {
            public void run(final UnaryProcedure proc) {
                Generator wrapped = getWrappedGenerator();
                assertSame(simpleGenerator, wrapped);
                wrapped.run(new UnaryProcedure() {
                    public void run(Object obj) {
                        proc.run(new Integer(((Integer)obj).intValue() + 1));
                    }
                });
            }
        };

        gen.run(new UnaryProcedure() {
            public void run(Object obj) {
                result.append(obj);
            }
        });

        assertEquals("12345", result.toString());

        // try to stop the wrapped generator
        final StringBuffer result2 = new StringBuffer();
        gen.run(new UnaryProcedure() {
            int i=0;
            public void run(Object obj) {
                result2.append(obj);
                if (i++ > 1) {
                    gen.stop();
                }
            }
        });

        assertEquals("123", result2.toString());
    }

    // Tests
    // ------------------------------------------------------------------------

    public void testApply() {
        Generator gen = new IntegerRange(1,5);
        UnaryFunction dbl = new UnaryFunction() {
            public Object evaluate(Object obj) {
                return new Integer(2*((Number)obj).intValue());
            }
        };
        Summer summer = new Summer();
                
        gen.apply(dbl).run(summer);
        
        assertEquals(2*(1+2+3+4),summer.sum);
    }

    public void testContains() {
        assertTrue(EachElement.from(list).contains(equalsThree));
        assertTrue(!EachElement.from(list).contains(equalsTwentyThree));
    }

    public void testDetect() {
        assertEquals(new Integer(3),EachElement.from(list).detect(equalsThree));
        try {
            EachElement.from(list).detect(equalsTwentyThree);
            fail("Expected NoSuchElementException");
        } catch(NoSuchElementException e) {
            // expected
        }
    }

    public void testDetectIfNone() {
        assertEquals(new Integer(3),EachElement.from(list).detect(equalsThree,"Xyzzy"));
        assertEquals("Xyzzy",EachElement.from(list).detect(equalsTwentyThree,"Xyzzy"));
    }

    public void testForEach() {
        Summer summer = new Summer();
        EachElement.from(list).foreach(summer);
        assertEquals(sum,summer.sum);
    }

    public void testWhere() {
        assertEquals(evens,EachElement.from(list).where(isEven).toCollection());
    }

    public void testSelect1() {
        assertEquals(evens,EachElement.from(list).select(isEven).toCollection());
    }

    public void testSelect2() {
        ArrayList result = new ArrayList();
        assertSame(result,EachElement.from(list).select(isEven).to(result));
        assertEquals(evens,result);
    }

    public void testReject1() {
        assertEquals(evens,EachElement.from(list).reject(isOdd).toCollection());
    }

    public void testReject2() {
        ArrayList result = new ArrayList();
        assertSame(result,EachElement.from(list).reject(isOdd).to(result));
        assertEquals(evens,result);
    }

    public void testInject() {
        Object result = EachElement.from(list).inject(
            new Integer(0),
            new BinaryFunction() {
                public Object evaluate(Object a, Object b) {
                    return new Integer(((Number)a).intValue() + ((Number)b).intValue());
                }
            });
        assertEquals(new Integer(sum),result);
    }

    public void testLimit() {
        Collection col = simpleGenerator.until(new Offset(2)).toCollection();
        assertEquals("[0, 1]", col.toString());
    }

    public void testTo() {
        Collection col = (Collection)simpleGenerator.to(new CollectionTransformer());
        assertEquals("[0, 1, 2, 3, 4]", col.toString());

        Collection fillThis = new LinkedList();
        col = (Collection)simpleGenerator.to(new CollectionTransformer(fillThis));
        assertSame(fillThis, col);
        assertEquals("[0, 1, 2, 3, 4]", col.toString());

        col = simpleGenerator.toCollection();
        assertEquals("[0, 1, 2, 3, 4]", col.toString());
        assertEquals("[0, 1, 2, 3, 4]", col.toString());

        fillThis = new LinkedList();
        col = simpleGenerator.to(fillThis);
        assertSame(fillThis, col);
        assertEquals("[0, 1, 2, 3, 4]", col.toString());
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
            return ((Number)obj).intValue() % 2 == 0;
        }
    };
    private UnaryPredicate isOdd = new UnaryPredicate() {
        public boolean test(Object obj) {
            return ((Number)obj).intValue() % 2 != 0;
        }
    };

    // Classes
    // ------------------------------------------------------------------------

    static class Summer implements UnaryProcedure {
        public void run(Object that) {
            sum += ((Number)that).intValue();
        }
        public int sum = 0;
    }
}