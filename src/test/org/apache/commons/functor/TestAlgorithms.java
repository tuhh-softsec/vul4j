/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/TestAlgorithms.java,v 1.4 2003/11/25 17:49:35 rwaldhoff Exp $
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
import org.apache.commons.functor.core.IdentityFunction;
import org.apache.commons.functor.core.IsEqual;
import org.apache.commons.functor.core.Offset;

/**
 * @version $Revision: 1.4 $ $Date: 2003/11/25 17:49:35 $
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
        list = null;
        evens = null;
        listWithDuplicates = null;
        sum = 0;
    }

    // Tests
    // ------------------------------------------------------------------------

    public void testHasPublicConstructor() {
        // some frameworks work best with instantiable classes
        assertNotNull(new Algorithms());
    }

    public void testApply() {
        Collection result = Algorithms.apply(list.iterator(),IdentityFunction.instance()).toCollection();
        assertNotNull(result);
        assertEquals(list.size(),result.size());
        assertEquals(list,result);
    }

    public void testApply2() {
        Set set = new HashSet();
        assertSame(set,Algorithms.apply(list.iterator(),IdentityFunction.instance()).to(set));
        assertEquals(list.size(),set.size());
        for(Iterator iter = list.iterator(); iter.hasNext(); ) {
            assertTrue(set.contains(iter.next()));
        }
    }

    public void testApply3() {
        Set set = new HashSet();
        assertSame(set,Algorithms.apply(listWithDuplicates.iterator(),IdentityFunction.instance()).to(set));
        assertTrue(listWithDuplicates.size() > set.size());
        for(Iterator iter = listWithDuplicates.iterator(); iter.hasNext(); ) {
            assertTrue(set.contains(iter.next()));
        }
    }

    public void testContains() {
        assertTrue(Algorithms.contains(list.iterator(),equalsThree));
        assertTrue(!Algorithms.contains(list.iterator(),equalsTwentyThree));
    }

    public void testDetect() {
        assertEquals(new Integer(3),Algorithms.detect(list.iterator(),equalsThree));
        try {
            Algorithms.detect(list.iterator(),equalsTwentyThree);
            fail("Expected NoSuchElementException");
        } catch(NoSuchElementException e) {
            // expected
        }
    }

    public void testDetectIfNone() {
        assertEquals(new Integer(3),Algorithms.detect(list.iterator(),equalsThree,"Xyzzy"));
        assertEquals("Xyzzy",Algorithms.detect(list.iterator(),equalsTwentyThree,"Xyzzy"));
    }

    public void testForEach() {
        Summer summer = new Summer();
        Algorithms.foreach(list.iterator(),summer);
        assertEquals(sum,summer.sum);
    }

    public void testSelect1() {
        Collection result = Algorithms.select(list.iterator(),isEven).toCollection();
        assertNotNull(result);
        assertEquals(evens,result);
    }

    public void testSelect2() {
        ArrayList result = new ArrayList();
        assertSame(result,Algorithms.select(list.iterator(),isEven).to(result));
        assertEquals(evens,result);
    }

    public void testReject1() {
        Collection result = Algorithms.reject(list.iterator(),isOdd).toCollection();
        assertNotNull(result);
        assertEquals(evens,result);
    }

    public void testReject2() {
        ArrayList result = new ArrayList();
        assertSame(result,Algorithms.reject(list.iterator(),isOdd).to(result));
        assertEquals(evens,result);
    }

    public void testInject() {
        Object result = Algorithms.inject(
            list.iterator(),
            new Integer(0),
            new BinaryFunction() {
                public Object evaluate(Object a, Object b) {
                    return new Integer(((Number)a).intValue() + ((Number)b).intValue());
                }
            });
        assertEquals(new Integer(sum),result);
    }

    public void testLimit() {
        Collection col = Algorithms.until(list.iterator(), new Offset(2)).toCollection();
        System.out.println(col);
        assertEquals("[0, 1]", col.toString());
    }

    public void testRecurse() {
        assertEquals(new Integer(5), Algorithms.recurse(new RecFunc(0, false)));

        // this version will return a function. since it is not the same type
        // as RecFunc recursion will end.
        Function func = (Function)Algorithms.recurse(new RecFunc(0, true));
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
