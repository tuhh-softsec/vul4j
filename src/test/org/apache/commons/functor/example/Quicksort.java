/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/example/Attic/Quicksort.java,v 1.6 2003/03/04 17:59:29 rwaldhoff Exp $
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
package org.apache.commons.functor.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.BinaryPredicate;
import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.adapter.RightBoundPredicate;
import org.apache.commons.functor.core.ConstantFunction;
import org.apache.commons.functor.core.collection.CollectionAlgorithms;
import org.apache.commons.functor.core.collection.IsEmpty;
import org.apache.commons.functor.core.comparator.IsGreaterThanOrEqual;
import org.apache.commons.functor.core.comparator.IsLessThan;
import org.apache.commons.functor.core.composite.ConditionalUnaryFunction;

/*
 * ----------------------------------------------------------------------------
 * INTRODUCTION:
 * ----------------------------------------------------------------------------
 */

/* 
 * Here's an example of the Quicksort sorting algorithm, implemented using
 * commons-functor.
 * 
 * We'll write this quicksort implementation in a literate programming style,
 * (in other words, with descriptive prose mixed right in with the source).
 * 
 * For convenience, and to make sure this example stays up to date, 
 * we'll implement our quicksort example as a JUnit TestCase.
 */

/**
 * An example of implementing the quicksort sorting algorithm 
 * using commons-functor.
 * <p> 
 * See the extensive in line comments for details.
 * 
 * @version $Revision: 1.6 $ $Date: 2003/03/04 17:59:29 $
 * @author Rodney Waldhoff
 */
public class Quicksort extends TestCase {

/*
 * Let's declare the constructor and suite() methods we need
 * to ensure this test suite can be executed along with all the
 * others:
 */

    public Quicksort(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(Quicksort.class);
    }

 
/*
 * If you're not familiar with JUnit, don't worry. An understanding of JUnit 
 * isn't important for an understanding of this example, and we'll walk you 
 * through the relevant bits anyway.
 * 
 * Two things you'll want to know about JUnit are (a) all the methods
 * whose names start with "test" will be executed automatically by the 
 * test suite and (b) there are various "assert" methods that can be used
 * to make assertions about the Objects being tested.  If any assertion
 * fails, the JUnit framework will count this as a test failure.
 */

/*
 * ----------------------------------------------------------------------------
 * UNIT TESTS:
 * ----------------------------------------------------------------------------
 */
 
/*
 * In "test first" style, let's start with the some functional descriptions
 * of what'd we'd like our Quicksort to do, expressed as unit tests.
 * 
 * In our tests, we'll use a "quicksort" method which takes a List and
 * returns a (new) sorted List.  We'll define that method a bit later.
 *
 * 
 * First, let's get some trivial cases out of the way.
 * 
 * Sorting an empty List should produce an empty list:
 */

    public void testSortEmpty() {
        List empty = Collections.EMPTY_LIST;
        List result = quicksort(empty);
        assertTrue(
            "Sorting an empty list should produce an empty list.",
            result.isEmpty()
        );
    }    

/*
 * Similarly, sorting a List composed of a single element
 * should produce an equivalent list:
 */

    public void testSortSingleElementList() {
        List list = new ArrayList();
        list.add("element");
        
        List sorted = quicksort(list);
        
        assertTrue(
            "The quicksort() method should return a distinct list.",
            list != sorted
        );
        
        assertEquals(
            "Sorting a single-element list should produce an equivalent list",
            list, 
            sorted
        );
    }    

/*
 * Finally, sorting a List composed of multiple copies
 * of a single value should produce an equivalent list:
 */

    public void testSortSingleValueList() {
        List list = new ArrayList();
        for(int i=0;i<10;i++) {
            list.add("element");
        }
        List sorted = quicksort(list);
        
        assertTrue(
            "The quicksort() method should return a distinct list.",
            list != sorted
        );
        
        assertEquals(list, sorted);
    }    
 
/*
 * So far so good.
 * 
 * Next, let's take some slightly more complicated cases.
 * 
 * Sorting an already sorted list:
 */
    public void testSortSorted() {
        List list = new ArrayList();
        for(int i=0;i<10;i++) {
            list.add(new Integer(i));
        }
        
        List sorted = quicksort(list);
        
        assertTrue(
            "The quicksort() method should return a distinct list.",
            list != sorted
        );
        
        assertEquals(
            "Sorting an already sorted list should produce an equivalent list",
            list, 
            sorted
        );
    }    

/*
 * Sorting a reverse-order list (finally, a test case that requires something 
 * more than an identity function):
 */
    public void testSortReversed() {
        List expected = new ArrayList();
        List tosort = new ArrayList();
        for(int i=0;i<10;i++) {
            /*
             * The "expected" list contains the integers in order.
             */
            expected.add(new Integer(i));
            /*
             * The "tosort" list contains the integers in reverse order.
             */
            tosort.add(new Integer(9 - i));
        }
        
        assertEquals(expected, quicksort(tosort));
    }    


/*
 * Just for fun, let's add some randomness to the tests, first by shuffling:
 */
    public void testSortShuffled() {
        List expected = new ArrayList();
        for(int i=0;i<10;i++) {
            expected.add(new Integer(i));
        }
        List tosort = new ArrayList(expected);
        Collections.shuffle(tosort);
        
        assertEquals(expected, quicksort(tosort));
    }    
    
/*
 * and then using random values:
 */
    public void testSortRandom() {
        Random random = new Random();
        /*
         * populate a list with random integers
         */        
        List tosort = new ArrayList();
        for(int i=0;i<10;i++) {
            tosort.add(new Integer(random.nextInt(10)));
        }
        /*
         * and use java.util.Collections.sort to
         * give us a sorted version to compare to
         */
        List expected = new ArrayList(tosort);
        Collections.sort(expected);

        assertEquals(expected, quicksort(tosort));
    }    

/*
 * Finally, while this quicksort implementation is intended to 
 * illustrate the use of Commons Functor, not for performance,
 * let's output some timings just to demonstrate that the 
 * performance is adequate.
 */
 
    private static final int SIZE = 1000;
    private static final int COUNT = 100;
    
    public void testTimings() {
        /*
         * We'll need the total elapsed time:
         */
        long elapsed = 0L;
        
        /*
         * and a source for random integers:
         */
        Random random = new Random();
        
        /*
         * Repeat this COUNT times:
         */
        for(int i=0;i<COUNT;i++) {
            /*
             * Create a List of size SIZE, and
             * populate it with random integers:
             */
            List tosort = new ArrayList(SIZE);
            for(int j=0;j<SIZE;j++) {
                tosort.add(new Integer(random.nextInt(SIZE)));
            }
            
            /*
             * Start the timer.
             */
            long start = System.currentTimeMillis();        
            
            /*
             * Sort the list.
             * (We'll ignore the returned value here.)
             */
            quicksort(tosort);
            
            /*
             * Stop the timer.
             */
             long stop = System.currentTimeMillis();
             
             /*
              * Add the elapsed time to our total.
              */
              elapsed += stop - start;
        }
        

        /*
         * Whew, that was a lot of processing.  Now figure out
         * how long it took on average (per list):
         */
        double avgmillis = ((double)elapsed)/((double)COUNT);
         
        /* 
         * and print a simple summary.
         */
        System.out.println();        
        System.out.println(
            "Quicksort Example: Sorted " + COUNT + 
            " lists of " + SIZE + 
            " elements in " + elapsed + " millis (" + 
            avgmillis + 
            " millis, or " +
            (avgmillis/1000D) + 
            " seconds on average).");
        System.out.println();        
    }    
     
/*
 * BUILDING BLOCKS:
 * ----------------------------------------------------------------------------
 * 
 * Let's save ourselves some casting and error checking by defining
 * functor subclasses that deal with java.util.List.
 *
 * Let ListFunction be a UnaryFunction that operates on Lists :
 */ 
 
    public abstract class ListFunction implements UnaryFunction {
        public abstract Object evaluate(List list);
        
        public Object evaluate(Object obj) {
            if(null == obj) {
                throw new NullPointerException("The argument must not be null.");
            } else if(!(obj instanceof List)) {
                throw new ClassCastException(
                    "The argument must be a List, found " + 
                    obj.getClass().getName());
            } else { 
                return evaluate((List)obj);
            }
        }
    }

/*
 * THE QUICKSORT ALGORITHM:
 * ----------------------------------------------------------------------------
 * 
 * The quicksort sorting algorithm can be summarized as follows:
 * 
 * Given a list of elements to be sorted:
 * 
 * A) If the list is empty, consider it already sorted.
 * 
 * B) If the list is non-empty, we can sort it by first splitting it into 
 *   three lists:
 *     1) one list containing only the first element in the list (the "head")
 *     2) one (possibly empty) list containing every element in the remaining
 *        list that is less than the head
 *     3) one (possibly empty) list containing every element in the remaining
 *        list that is greater than or equal to the head
 *    applying the quicksort algorithm recursively to the second and third lists,
 *    and joining the results back together as (2) + (1) + (3).
 * 
 */

/* 
 * Let's define functors for the operations we'll need.
 * 
 * Given a List, we want to be able to break it into its head:
 */

    private UnaryFunction head = new ListFunction() {
        public Object evaluate(List list) {
            return list.get(0);
        }        
    };

/* 
 * and its tail:
 */
    private UnaryFunction tail = new ListFunction() {
        public Object evaluate(List list) {
            return list.size() < 2 ? Collections.EMPTY_LIST : list.subList(1,list.size());
        }        
    };
            
/* 
 * Given a List in head/tail form, we should be able to find
 * the List of elements in the tail less than the head:
 */
    private BinaryFunction lesserTail = new BinaryFunction() {
        public Object evaluate(Object head, Object tail) {
            return CollectionAlgorithms.select(
                ((List)tail).iterator(),
                RightBoundPredicate.bind(
                    IsLessThan.getIsLessThan(),
                    head));            
        }
    };

/* 
 * and we should be able to find the List of elements in 
 * the tail greater than the head:
 */
    private BinaryFunction greaterTail = new BinaryFunction() {
        public Object evaluate(Object head, Object tail) {
            return CollectionAlgorithms.select(
                ((List)tail).iterator(),
                RightBoundPredicate.bind(
                    IsGreaterThanOrEqual.getIsGreaterThanOrEqual(),
                    head));            
        }
    };

/* 
 * With these building blocks, our quicksort function is a 
 * straightfoward application of the description above:
 */
    private UnaryFunction quicksort = new ConditionalUnaryFunction(        
        IsEmpty.getIsEmpty(),                         /* If the list is empty, */
        new ConstantFunction(Collections.EMPTY_LIST), /*  then return an empty list, */
        new ListFunction() {                          /*  else, split and recurse */
            public Object evaluate(List list) {
                List result = new ArrayList(list.size());
                Object h = head.evaluate(list);
                Object t = tail.evaluate(list);
                result.addAll((List)quicksort.evaluate(lesserTail.evaluate(h,t)));
                result.add(h);
                result.addAll((List)quicksort.evaluate(greaterTail.evaluate(h,t)));
                return result;
            }
        }
    );
    

    public List quicksort(List list) {
        return (List)(quicksort.evaluate(list));
    }
}
