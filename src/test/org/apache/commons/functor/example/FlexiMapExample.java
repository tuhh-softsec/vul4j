/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/example/FlexiMapExample.java,v 1.5 2003/03/29 01:14:22 rwaldhoff Exp $
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.BinaryProcedure;
import org.apache.commons.functor.Procedure;
import org.apache.commons.functor.UnaryProcedure;
import org.apache.commons.functor.adapter.IgnoreLeftFunction;
import org.apache.commons.functor.adapter.UnaryProcedureUnaryFunction;
import org.apache.commons.functor.core.ConstantFunction;
import org.apache.commons.functor.core.IdentityFunction;
import org.apache.commons.functor.core.IsInstanceOf;
import org.apache.commons.functor.core.IsNull;
import org.apache.commons.functor.core.RightIdentityFunction;
import org.apache.commons.functor.core.composite.ConditionalUnaryFunction;

/*
 * ----------------------------------------------------------------------------
 * INTRODUCTION:
 * ----------------------------------------------------------------------------
 */

/*
 * In this example, we'll demonstrate how we can use "pluggable" functors
 * to create specialized Map implementations via composition.
 * 
 * All our specializations will use the same basic Map implementation.
 * Once it is built, we'll only need to define the specialized behaviors. 
 */
 
/**
 * @version $Revision: 1.5 $ $Date: 2003/03/29 01:14:22 $
 * @author Rodney Waldhoff
 */
public class FlexiMapExample extends TestCase {

    public FlexiMapExample(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(FlexiMapExample.class);
    }

    /*
     * ----------------------------------------------------------------------------
     * UNIT TESTS:
     * ----------------------------------------------------------------------------
     */

    /* 
     * In a "test first" style, let's first specify the Map behaviour we'd like
     * to implement via unit tests. 
     */
     
    /*
     * First, let's review the basic Map functionality.
     */
    
    /*
     * The basic Map interface lets one associate keys and values:
     */
    public void testBasicMap() {
        /* (We'll define these make*Map functions below.) */
        Map map = makeBasicMap(); 
        Object key = "key";
        Object value = new Integer(3);
        map.put(key,value);
        assertEquals(value, map.get(key) );
    }    

    /*
     * If there is no value associated with a key,
     * the basic Map will return null for that key:
     */
    public void testBasicMapReturnsNullForMissingKey() {
        Map map = makeBasicMap();
        assertNull( map.get("key") );
    }    

    /*
     * One can also explicitly store a null value for
     * some key: 
     */
    public void testBasicMapAllowsNull() {
        Map map = makeBasicMap();
        Object key = "key";
        Object value = null;
        map.put(key,value);
        assertNull( map.get(key) );
    }    

    /*
     * The basic Map deals with Objects--it can store keys
     * and values of multiple or differing types:
     */
    public void testBasicMapAllowsMultipleTypes() {
        Map map = makeBasicMap();
        map.put("key-1","value-1");
        map.put(new Integer(2),"value-2");
        map.put("key-3",new Integer(3));
        map.put(new Integer(4),new Integer(4));

        assertEquals("value-1", map.get("key-1") );
        assertEquals("value-2", map.get(new Integer(2)) );
        assertEquals(new Integer(3), map.get("key-3") );
        assertEquals(new Integer(4), map.get(new Integer(4)) );
    }    

    /*
     * Finally, note that putting a second value for a given
     * key will overwrite the first value--the basic Map only
     * stores the most recently put value for each key: 
     */
    public void testBasicMapStoresOnlyOneValuePerKey() {
        Map map = makeBasicMap();

        assertNull( map.put("key","value-1") );
        assertEquals("value-1", map.get("key") );
        assertEquals("value-1", map.put("key","value-2"));
        assertEquals("value-2", map.get("key") );
    }    
    
    /*
     * Now let's look at some specializations of the Map behavior. 
     */
    
    /*
     * One common specialization is to forbid null values, 
     * like our old friend Hashtable: 
     */
    public void testForbidNull() {
        Map map = makeNullForbiddenMap();
        
        map.put("key","value");
        map.put("key2", new Integer(2) );
        try {
            map.put("key3",null);
            fail("Expected NullPointerException");
        } catch(NullPointerException e) {
            // expected
        }                
    }

    /*
     * Alternatively, we may want to provide a default
     * value to return when null is associated with some 
     * key. (This might be useful, for example, when the Map
     * contains a counter--when there's no count yet, we'll 
     * want to treat it as zero.):  
     */
    public void testNullDefaultsToZero() {
        Map map = makeDefaultValueForNullMap(new Integer(0));        
        /*
         * We expect 0 when no value has been associated with "key".
         */
        assertEquals( new Integer(0), map.get("key") );
        /*
         * We also expect 0 when a null value has been associated with "key".
         */
        map.put("key", null);
        assertEquals( new Integer(0), map.get("key") );
    }

    /*
     * Another common specialization is to constrain the type of values
     * that may be stored in the Map:
     */
	public void testIntegerValuesOnly() {
		Map map = makeTypeConstrainedMap(Integer.class);
		map.put("key", new Integer(2));        
		assertEquals( new Integer(2), map.get("key") );
		try {
			map.put("key2","value");
			fail("Expected ClassCastException");
		} catch(ClassCastException e) {
			// expected
		}                		
	}

    /*
     * A more interesting specialization is that used by the 
     * Jakarta Commons Collections MultiMap class, which allows 
     * one to associate multiple values with each key.  The put
     * function still accepts a single value, but the get function
     * will return a Collection of values.  Associating multiple values
     * with a key adds to that collection, rather than overwriting the
     * previous value: 
     */
	public void testMultiMap() {
		Map map = makeMultiMap();

		map.put("key", "value 1");
		
		{
			Collection result = (Collection)(map.get("key"));
			assertEquals(1,result.size());
			assertEquals("value 1", result.iterator().next());
		}

		map.put("key", "value 2");

		{
			Collection result = (Collection)(map.get("key"));
			assertEquals(2,result.size());
			Iterator iter = result.iterator();
			assertEquals("value 1", iter.next());
			assertEquals("value 2", iter.next());
		}

		map.put("key", "value 3");

		{
			Collection result = (Collection)(map.get("key"));
			assertEquals(3,result.size());
			Iterator iter = result.iterator();
			assertEquals("value 1", iter.next());
			assertEquals("value 2", iter.next());
			assertEquals("value 3", iter.next());
		}

	}

    /*
     * Here's another variation on the MultiMap theme.  
     * Rather than adding elements to a Collection, let's 
     * concatenate String values together, delimited by commas.
     * (Such a Map might be used by the Commons Collection's
     * ExtendedProperties type.): 
     */
	public void testStringConcatMap() {
		Map map = makeStringConcatMap();
		map.put("key", "value 1");
		assertEquals("value 1",map.get("key"));
		map.put("key", "value 2");
		assertEquals("value 1, value 2",map.get("key"));
		map.put("key", "value 3");
		assertEquals("value 1, value 2, value 3",map.get("key"));
	}

    /*
     * ----------------------------------------------------------------------------
     * THE GENERIC MAP IMPLEMENTATION:
     * ----------------------------------------------------------------------------
     */

    /*
     * How can one Map implementation support all these behaviors?
     * Using functors and composition, of course.  
     * 
     * In order to keep our example small, we'll just consider the 
     * primary Map.put and Map.get methods here, although the remaining
     * Map methods could be handled similiarly.    
     */
    static class FlexiMap implements Map {

        /*
         * Our FlexiMap will accept two BinaryFunctions, one 
         * that's used to transform objects being put into the Map,
         * and one that's used to transforms objects being retrieved 
         * from the map.
         */
        public FlexiMap(BinaryFunction putfn, BinaryFunction getfn) {
            if(null == putfn) {
                onPut = new RightIdentityFunction();
            } else {
                onPut = putfn;
            }
            
            if(null == getfn) {
                onGet = new RightIdentityFunction();
            } else {
                onGet = getfn;
            }
            
            proxiedMap = new HashMap();
        }        

        
        /*
         * The arguments to our "onGet" function will be the 
         * key and the value associated with that key in the 
         * underlying Map.  We'll return whatever the function
         * returns.
         */
        public Object get(Object key) {
            return onGet.evaluate( key, proxiedMap.get(key) );
        }

        /*
         * The arguments to our "onPut" function will be the 
         * value previously associated with that key (if any),
         * as well as the new value being associated with that key.
         * 
         * Since put returns the previously associated value, 
         * we'll invoke onGet here as well. 
         */
        public Object put(Object key, Object value) {
            Object oldvalue = proxiedMap.get(key);
            proxiedMap.put(key, onPut.evaluate(oldvalue, value));
            return onGet.evaluate(key,oldvalue);
        }

       /* 
        * We'll skip the remaining Map methods for now.    
        */
        
        public void clear() {
            throw new UnsupportedOperationException("Left as an exercise for the reader.");
        }

        public boolean containsKey(Object key) {
            throw new UnsupportedOperationException("Left as an exercise for the reader.");
        }

        public boolean containsValue(Object value) {
            throw new UnsupportedOperationException("Left as an exercise for the reader.");
        }

        public Set entrySet() {
            throw new UnsupportedOperationException("Left as an exercise for the reader.");
        }

        public boolean isEmpty() {
            throw new UnsupportedOperationException("Left as an exercise for the reader.");
        }

        public Set keySet() {
            throw new UnsupportedOperationException("Left as an exercise for the reader.");
        }

        public void putAll(Map t) {
            throw new UnsupportedOperationException("Left as an exercise for the reader.");
        }

        public Object remove(Object key) {
            throw new UnsupportedOperationException("Left as an exercise for the reader.");
        }

        public int size() {
            throw new UnsupportedOperationException("Left as an exercise for the reader.");
        }

        public Collection values() {
            throw new UnsupportedOperationException("Left as an exercise for the reader.");
        }

        private BinaryFunction onPut = null;
        private BinaryFunction onGet = null;
        private Map proxiedMap = null;
    }

    /*
     * ----------------------------------------------------------------------------
     * MAP SPECIALIZATIONS:
     * ----------------------------------------------------------------------------
     */

    /*
     * For the "basic" Map, we'll simply create a HashMap.
     * Note that using a RightIdentityFunction for onPut and onGet
     * would yield the same behavior. 
     */
    private Map makeBasicMap() {
        return new HashMap();
    }
    
    /*
     * To prohibit null values, we'll only need to 
     * provide an onPut function.
     */
    private Map makeNullForbiddenMap() {
        return new FlexiMap(
            /*
             * We simply ignore the left-hand argument,
             */
            IgnoreLeftFunction.adapt(                  
                /*
                 * and for the right-hand,
                 */      
                new ConditionalUnaryFunction(
                    /*
                     * we'll test for null,
                     */      
                    IsNull.getIsNullPredicate(),
                    /*
                     * throwing a NullPointerException when the value is null,
                     */      
                    UnaryProcedureUnaryFunction.adapt(throwNPE),
                    /*
                     * and passing through all non-null values.
                     */      
                    IdentityFunction.getIdentityFunction()
                )
            ),
            null
        );
    }

    /*
     * To provide a default for null values, we'll only need to 
     * provide an onGet function, simliar to the onPut method used
     * above.
     */
	private Map makeDefaultValueForNullMap(Object defaultValue) {
		return new FlexiMap(
            null,
            /*
             * We ignore the left-hand argument,
             */
			IgnoreLeftFunction.adapt(                        
                /*
                 * and for the right-hand,
                 */      
				new ConditionalUnaryFunction(
                    /*
                     * we'll test for null,
                     */      
					IsNull.getIsNullPredicate(),
                    /*
                     * returning our default when the value is otherwise null,
                     */      
					new ConstantFunction(defaultValue),
                    /*
                     * and passing through all non-null values.
                     */      
					IdentityFunction.getIdentityFunction()
				)
			)
		);
	}

    /*
     * To constrain the value types, we'll 
     * provide an onPut function,
     */
	private Map makeTypeConstrainedMap(Class clazz) {
		return new FlexiMap(
            /*
             * ignore the left-hand argument,
             */
			IgnoreLeftFunction.adapt(                        
				new ConditionalUnaryFunction(
                    /*
                     * we'll test the type of the right-hand argument,
                     */      
					new IsInstanceOf(clazz),
                    /*
                     * and either pass the given value through,
                     */      
					IdentityFunction.getIdentityFunction(),
                    /*
                     * or throw a ClassCastException.
                     */      
					UnaryProcedureUnaryFunction.adapt(throwCCE)
				)
			),
			null
		);
	}

    /*
     * The MultiMap is a bit more interesting, since we'll
     * need to consider both the old and new values during
     * onPut:
     */
	private Map makeMultiMap() {
		return new FlexiMap(
			new BinaryFunction() {
				public Object evaluate(Object oldval, Object newval) {
					List list = null;
					if(null == oldval) {
						list = new ArrayList();
					} else {
						list = (List)oldval;
					}
					list.add(newval);
					return list;
				}
			},
			null
		);
	}

    /*
     * The StringConcatMap is more interesting still.
     */
	private Map makeStringConcatMap() {
		return new FlexiMap(
            /*
             * The onPut function looks similiar to the MultiMap
             * method:
             */
			new BinaryFunction() {
				public Object evaluate(Object oldval, Object newval) {
					StringBuffer buf = null;
					if(null == oldval) {
						buf = new StringBuffer();
					} else {
						buf = (StringBuffer)oldval;
						buf.append(", ");
					}
					buf.append(newval);
					return buf;
				}
			},
            /*
             * but we'll also need an onGet functor to convert
             * the StringBuffer to a String:
             */
			new BinaryFunction() {
				public Object evaluate(Object key, Object val) {
					if(null == val) {
						return null;
					} else {
						return ((StringBuffer)val).toString();
					}
				}
			}
		);
	}

    /*
     * (This "UniversalProcedure" type provides a procedure 
     * that takes the same action regardless of the number of
     * parameters. We used it above to throw Exceptions when 
     * needed.)
     */
     
    private abstract class UniversalProcedure implements Procedure, UnaryProcedure, BinaryProcedure {
        public abstract void run();

        public void run(Object obj) {
            run();
        }
        public void run(Object left, Object right) {
            run();
        }
    }

	private UniversalProcedure throwNPE = new UniversalProcedure() {
		public void run() {
			throw new NullPointerException();
		}
	};
    
	private UniversalProcedure throwCCE = new UniversalProcedure() {
		public void run() {
			throw new ClassCastException();
		}
	};
    
}
