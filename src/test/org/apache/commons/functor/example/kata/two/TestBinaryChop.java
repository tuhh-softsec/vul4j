/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/example/kata/two/TestBinaryChop.java,v 1.6 2003/12/01 20:27:20 rwaldhoff Exp $
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
package org.apache.commons.functor.example.kata.two;

import java.util.Collections;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.functor.Algorithms;
import org.apache.commons.functor.Function;
import org.apache.commons.functor.Predicate;
import org.apache.commons.functor.Procedure;
import org.apache.commons.functor.generator.util.IntegerRange;

/**
 * Examples of binary search implementations.
 * 
 * A binary search algorithm is the same strategy used in
 * that number guessing game, where one player picks a number
 * between 1 and 100, and the second player tries to guess it.
 * Each time the second player guesses a number, the first player
 * tells whether the chosen number is higher, lower or equal to
 * the guess.
 * 
 * An effective strategy for this sort of game is always guess
 * the midpoint between what you know to be the lowest and 
 * highest possible number.  This will find the number in
 * log2(N) guesses (when N = 100, this is at most 7 guesses).
 * 
 * For example, suppose the first player (secretly) picks the
 * number 63.  The guessing goes like this:
 * 
 * P1> I'm thinking of a number between 1 and 100. 
 * P2> Is it 50?
 * P1> Higher. 
 * P2> 75?
 * P1> Lower.
 * P2> 62?
 * P1> Higher.
 * P2> 68?
 * P1> Lower.
 * P2> 65?
 * P1> Lower.
 * P2> 63?
 * P1> That's it.
 *
 * Dave Thomas's Kata Two asks us to implement a binary search algorithm
 * in several ways.  Here we'll use this as an opportunity to 
 * consider some common approaches and explore 
 * some functor based approaches as well. 
 *  
 * See http://pragprog.com/pragdave/Practices/Kata/KataTwo.rdoc,v
 * for more information on this Kata.
 * 
 * @version $Revision: 1.6 $ $Date: 2003/12/01 20:27:20 $
 * @author Rodney Waldhoff
 */
public class TestBinaryChop extends TestCase {
    public TestBinaryChop(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestBinaryChop.class);
    }

    /**
     * This is Dave's test case, plus 
     * a quick check of searching a fairly large
     * list, just to make sure the time and space 
     * requirements are reasonable. 
     */
    private void chopTest(BinaryChop chopper) {
        assertEquals(-1, chopper.find(3, new int[0]));
        assertEquals(-1, chopper.find(3, new int[] { 1 }));
        assertEquals(0, chopper.find(1, new int[] { 1 }));

        assertEquals(0, chopper.find(1, new int[] { 1, 3, 5 }));
        assertEquals(1, chopper.find(3, new int[] { 1, 3, 5 }));
        assertEquals(2, chopper.find(5, new int[] { 1, 3, 5 }));
        assertEquals(-1, chopper.find(0, new int[] { 1, 3, 5 }));
        assertEquals(-1, chopper.find(2, new int[] { 1, 3, 5 }));
        assertEquals(-1, chopper.find(4, new int[] { 1, 3, 5 }));
        assertEquals(-1, chopper.find(6, new int[] { 1, 3, 5 }));

        assertEquals(0, chopper.find(1, new int[] { 1, 3, 5, 7 }));
        assertEquals(1, chopper.find(3, new int[] { 1, 3, 5, 7 }));
        assertEquals(2, chopper.find(5, new int[] { 1, 3, 5, 7 }));
        assertEquals(3, chopper.find(7, new int[] { 1, 3, 5, 7 }));
        assertEquals(-1, chopper.find(0, new int[] { 1, 3, 5, 7 }));
        assertEquals(-1, chopper.find(2, new int[] { 1, 3, 5, 7 }));
        assertEquals(-1, chopper.find(4, new int[] { 1, 3, 5, 7 }));
        assertEquals(-1, chopper.find(6, new int[] { 1, 3, 5, 7 }));
        assertEquals(-1, chopper.find(8, new int[] { 1, 3, 5, 7 }));

        List largeList = (List) (new IntegerRange(0, 100001).toCollection());
        assertEquals(-1, chopper.find(new Integer(-5), largeList));
        assertEquals(100000, chopper.find(new Integer(100000), largeList));
        assertEquals(0, chopper.find(new Integer(0), largeList));
        assertEquals(50000, chopper.find(new Integer(50000), largeList));

    }

    /**
     * In practice, one would most likely use the 
     * binary search method already available in 
     * java.util.Collections, but that's not 
     * really the point of this exercise.
     */
    public void testBuiltIn() {
        chopTest(new BaseBinaryChop() {
            public int find(Object seeking, List list) {
                int result = Collections.binarySearch(list,seeking);
                //
                // Collections.binarySearch is a bit smarter than our
                // "find". It returns 
                //  (-(insertionPoint) - 1)
                // when the value is not found, rather than
                // simply -1.
                //
                return result >= 0 ? result : -1; 
            }
        });
    }

    /**
     * Here's a basic iterative approach.
     * 
     * We set the lower or upper bound to the midpoint
     * until there's only one element between the lower
     * and upper bound.  Then the lower bound is where
     * the element would be found if it existed in the
     * list.
     * 
     * We add an additional comparision at the end so
     * that we can return -1 if the element is not yet
     * in the list.
     */
    public void testIterative() {
        chopTest(new BaseBinaryChop() {
            public int find(Object seeking, List list) {
                int high = list.size();
                int low = 0;
                while (high - low > 1) {
                    int mid = (high + low) / 2;
                    if(greaterThan(list,mid,seeking)) {
                        high = mid;
                    } else {
                        low = mid;
                    }
                }
                return list.isEmpty() ? -1 : (equals(list,low,seeking) ? low : -1);
            }
        });
    }

    /*
     * At http://onestepback.org/index.cgi/Tech/Programming/Kata/KataTwoVariation1.rdoc,
     * Jim Weirich discusses Kata Two from the perspective of loop invariants.
     * 
     * Loop invariants provide a way of deductive reasoning about loops.
     *   
     * Let P, Q. and R be predicates and A and B be
     * procedures.  Note that if:
     *   assert(P.test()); 
     *   A.run(); 
     *   assert(Q.test());
     * and 
     *   assert(Q.test()); 
     *   B.run(); 
     *   assert(R.test());
     * are both valid, then:
     *   assert(P.test()); 
     *   A.run(); 
     *   B.run(); 
     *   assert(R.test());
     * is valid as well.
     * 
     * Similiarly, if INV and TERM are predicates and BODY is a procedure,
     * then if:
     *   assert(INV.test()); 
     *   BODY.run(); 
     *   assert(INV.test());
     * is valid, then so is:
     *   assert(INV.test()); 
     *   do { BODY.run(); } while(! TERM.test() ); 
     *   assert(INV.test());
     *   assert(TERM.test());
     * 
     * Here INV is an "loop invariant", a statement that is true for every
     * single iteration through the loop.  TERM is a terminating condition,
     * a statement that is true (by construction) when the loop exits.
     * 
     * We can use loop invariants to reason about our iterative binary 
     * search loop.  In particular, note that:
     * 
     * // assert that the list is empty, or 
     * // the result index is between 
     * // low (inclusive) and high (exclusive),
     * // or high is 0 (the list is empty)
     * Predicate INV = new Predicate() {
     *   public boolean test() {
     *    return high == 0 || 
     *          (low <= result && result < high);
     *   }
     * };
     * 
     * is a valid invariant in our binary search, and that:
     *
     * Predicate TERM = new Predicate() {
     *   public boolean test() {
     *    return (high - low) <= 1;
     *   }
     * };
     * 
     * is a valid terminating condition.
     * 
     * The BODY in our case simply moves the endpoints 
     * closer together, without violating  
     * our invariant:
     * 
     * Procedure BODY = new Procedure() {
     *   public void run() {
     *     int mid = (high + low) / 2;
     *     if(greaterThan(list,mid,seeking)) {
     *       high = mid;
     *     } else {
     *       low = mid;
     *     }
     *   }
     * };
     * 
     * One could assert our invariant before and after
     * the execution of BODY, and the terminating condition
     * at the end of the loop, but we can tell by construction
     * that these assertions will hold.
     * 
     * Using the functor framework, we can make these notions
     * explict.  Specifically, the construction above is:
     * 
     *   Algorithms.dountil(BODY,TERM); 
     * 
     * Since we'll want to share state among the TERM and BODY,
     * let's declare a single interface for the TERM Predicate and
     * the BODY Procedure.  We'll be calculating a result within
     * the loop, so let's add a Function implementation as well,
     * as a way of retrieving that result: 
     */
    interface Loop extends Predicate, Procedure, Function {
        /** The terminating condition. */
        boolean test();
        /** The loop body. */
        void run();
        /** The result of executing the loop. */
        Object evaluate();
    };

    /*
     * Now we can use the Algorithms.dountil method to 
     * execute that loop: 
     */
    public void testIterative2() {
        chopTest(new BaseBinaryChop() {
            
            public int find(final Object seeking, final List list) {
                Loop loop = new Loop() {
                    int high = list.size();
                    int low = 0;

                    /** Our terminating condition. */
                    public boolean test() {
                        return (high - low) <= 1;                    
                    }
                    
                    /** Our loop body. */
                    public void run() {
                        {
                            int mid = (high + low) / 2;
                            if(greaterThan(list,mid,seeking)) {
                                high = mid;
                            } else {
                                low = mid;
                            }
                        }
                    }
                    
                    /** 
                     * A way of returning the result 
                     * at the end of the loop. 
                     */
                    public Object evaluate() {
                        return new Integer(
                            list.isEmpty() ? 
                            -1 : 
                            (BaseBinaryChop.equals(list,low,seeking) ? low : -1));                        
                    }
                    
                };
                Algorithms.untildo(loop,loop);
                return ((Number)loop.evaluate()).intValue();
            }
        });
    }

    /**
     * A recursive version of that implementation uses 
     * method parameters to track the upper and 
     * lower bounds. 
     */
    public void testRecursive() {
        chopTest(new BaseBinaryChop() {
            public int find(Object seeking, List list) {
                return find(seeking, list, 0, list.size());
            }
            
            private int find(Object seeking, List list, int low, int high) {
                if(high - low > 1) {
                    int mid = (high + low) / 2;
                    if(greaterThan(list,mid,seeking)) {
                        return find(seeking,list,low,mid);
                    } else {
                        return find(seeking,list,mid,high);
                    }
                } else {
                    return list.isEmpty() ? -1 : (equals(list,low,seeking) ? low : -1);
                }
            }
        });
    }

    /**
     * We can use the Algorithms.recuse method 
     * to implement that as tail recursion.
     * 
     * Here the anonymous Function implemenation
     * holds this itermediate state, rather than
     * the VM's call stack.
     * 
     * Arguably this is more like a continuation than
     * tail recursion, since there is a bit of state
     * to be tracked.
     */
    public void testTailRecursive() {
        chopTest(new BaseBinaryChop() {
            public int find(final Object seeking, final List list) {
                return ((Number)Algorithms.recurse(new Function() {
                    public Object evaluate() {
                        if(high - low > 1) {
                            int mid = (high + low) / 2;
                            if(greaterThan(list,mid,seeking)) {
                                high = mid;
                            } else {
                                low = mid;
                            }
                            return this;
                        } else {
                            return list.isEmpty() ? 
                                BaseBinaryChop.NEGATIVE_ONE : 
                                (BaseBinaryChop.equals(list,low,seeking) ? 
                                    new Integer(low) : 
                                    BaseBinaryChop.NEGATIVE_ONE);
                        }
                    }
                    int high = list.size();
                    int low = 0;
                })).intValue();
            }
        });
    }

    /**
     * One fun functional approach is to "slice" up the 
     * list as we search,  looking at smaller and 
     * smaller slices until we've found the element 
     * we're looking for.
     *
     * Note that while any given call to this recursive 
     * function may only be looking at a sublist, we
     * need to return the index in the overall list.
     * Hence we'll split out a method so that we can
     * pass the offset in the original list as a 
     * parameter.
     *  
     * With all of the subList creation, this approach
     * is probably less efficient than either the iterative
     * or the recursive implemenations above.
     */
    public void testRecursive2() {
        chopTest(new BaseBinaryChop() {
            public int find(Object seeking, List list) {
                return find(seeking,list,0);
            }
            
            private int find(Object seeking, List list, int offset) {
                if(list.isEmpty()) {
                    return -1;
                } if(list.size() == 1) {
                    return (equals(list,0,seeking) ? offset : -1);
                } else {
                    int mid = list.size() / 2;
                    if(greaterThan(list,mid,seeking)) {
                        return find(seeking,list.subList(0,mid),offset);
                    } else {
                        return find(seeking,list.subList(mid,list.size()),offset+mid);
                    }
                }
            }
        });
    }

    /**
     * We can do that using tail recursion as well.
     * 
     * Again, the anonymous Function implemenation
     * holds the "continuation" state.
     */
    public void testTailRecursive2() {
        chopTest(new BaseBinaryChop() {
            public int find(final Object seeking, final List list) {
                return ((Number)Algorithms.recurse(new Function() {
                    public Object evaluate() {
                        if(sublist.isEmpty()) {
                            return BaseBinaryChop.NEGATIVE_ONE;
                        } if(sublist.size() == 1) {
                            return (BaseBinaryChop.equals(sublist,0,seeking) ? 
                                new Integer(offset) : 
                                BaseBinaryChop.NEGATIVE_ONE);
                        } else {
                            int mid = sublist.size() / 2;
                            if(greaterThan(sublist,mid,seeking)) {
                                sublist = sublist.subList(0,mid);
                            } else {
                                sublist = sublist.subList(mid,sublist.size());
                                offset += mid;
                            }
                            return this;
                        }
                    }
                    int offset = 0;
                    List sublist = list;
                })).intValue();
            }
        });
    }

}
