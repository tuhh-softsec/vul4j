/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/generator/Attic/TestGenerator.java,v 1.6 2003/11/25 17:49:35 rwaldhoff Exp $
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.commons.functor.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.UnaryProcedure;
import org.apache.commons.functor.adapter.LeftBoundPredicate;
import org.apache.commons.functor.core.IdentityFunction;
import org.apache.commons.functor.core.IsEqual;
import org.apache.commons.functor.core.Offset;
import org.apache.commons.functor.generator.util.CollectionTransformer;
import org.apache.commons.functor.generator.util.EachElement;

/**
 * Tests the Base Generator class.
 * @author Jason Horman (jason@jhorman.org)
 */

public class TestGenerator extends TestCase {

    private Generator simpleGenerator = null;

    // Conventional
    // ------------------------------------------------------------------------

    public TestGenerator(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestGenerator.class);
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
        Collection result = EachElement.from(list).apply(IdentityFunction.instance()).toCollection();
        assertNotNull(result);
        assertEquals(list.size(),result.size());
        assertEquals(list,result);
    }

    public void testApply2() {
        Set set = new HashSet();
        assertSame(set,EachElement.from(list).apply(IdentityFunction.instance()).to(set));
        assertEquals(list.size(),set.size());
        for(Iterator iter = list.iterator(); iter.hasNext(); ) {
            assertTrue(set.contains(iter.next()));
        }
    }

    public void testApply3() {
        Set set = new HashSet();
        assertSame(set,EachElement.from(listWithDuplicates).apply(IdentityFunction.instance()).to(set));
        assertTrue(listWithDuplicates.size() > set.size());
        for(Iterator iter = listWithDuplicates.iterator(); iter.hasNext(); ) {
            assertTrue(set.contains(iter.next()));
        }
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

    public void testSelect1() {
        Collection result = EachElement.from(list).select(isEven).toCollection();
        assertNotNull(result);
        assertEquals(evens,result);
    }

    public void testSelect2() {
        ArrayList result = new ArrayList();
        assertSame(result,EachElement.from(list).select(isEven).to(result));
        assertEquals(evens,result);
    }

    public void testReject1() {
        Collection result = EachElement.from(list).reject(isOdd).toCollection();
        assertNotNull(result);
        assertEquals(evens,result);
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