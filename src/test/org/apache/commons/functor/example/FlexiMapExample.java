/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/example/FlexiMapExample.java,v 1.1 2003/03/04 21:33:56 rwaldhoff Exp $
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.BinaryProcedure;
import org.apache.commons.functor.Procedure;
import org.apache.commons.functor.UnaryProcedure;
import org.apache.commons.functor.adapter.BinaryProcedureBinaryFunction;
import org.apache.commons.functor.adapter.IgnoreLeftFunction;
import org.apache.commons.functor.adapter.IgnoreLeftPredicate;
import org.apache.commons.functor.adapter.IgnoreRightPredicate;
import org.apache.commons.functor.adapter.UnaryProcedureUnaryFunction;
import org.apache.commons.functor.core.ConstantFunction;
import org.apache.commons.functor.core.IdentityFunction;
import org.apache.commons.functor.core.IsNull;
import org.apache.commons.functor.core.RightIdentityFunction;
import org.apache.commons.functor.core.composite.BinaryOr;
import org.apache.commons.functor.core.composite.ConditionalBinaryFunction;
import org.apache.commons.functor.core.composite.ConditionalUnaryFunction;

/**
 * @version $Revision: 1.1 $ $Date: 2003/03/04 21:33:56 $
 * @author Rodney Waldhoff
 */
public class FlexiMapExample extends TestCase {

    public FlexiMapExample(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(FlexiMapExample.class);
    }

    public void testBasicMap() {
        Map map = makeBasicMap();
        Object key = "key";
        Object value = new Integer(3);
        map.put(key,value);
        assertEquals(value, map.get(key) );
    }    

    public void testBasicMapReturnsNullForMissingKey() {
        Map map = makeBasicMap();
        assertNull( map.get("key") );
    }    

    public void testBasicMapAllowsNull() {
        Map map = makeBasicMap();
        Object key = "key";
        Object value = null;
        map.put(key,value);
        assertNull( map.get(key) );
    }    

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

    public void testBasicMapStoresOnlyOneValuePerKey() {
        Map map = makeBasicMap();

        assertNull( map.put("key","value-1") );
        assertEquals("value-1", map.get("key") );
        assertEquals("value-1", map.put("key","value-2"));
        assertEquals("value-2", map.get("key") );
    }    
    
    
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

    public void testNullDefaultsToZero() {
        Map map = makeNullAsZeroMap();        
        map.put("key", null);
        assertEquals( new Integer(0), map.get("key") );
    }

    static class FlexiMap implements Map {

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
        
        public Object put(Object key, Object value) {
            Object oldvalue = get(key);
            proxiedMap.put(key, onPut.evaluate(oldvalue, value));
            return oldvalue;
        }

        public Object get(Object key) {
            return onGet.evaluate( key, proxiedMap.get(key) );
        }

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

    private Map makeBasicMap() {
        return new HashMap();
    }
    
    private Map makeNullForbiddenMap() {
        return new FlexiMap(
            IgnoreLeftFunction.adapt(                        
                new ConditionalUnaryFunction(
                    IsNull.getIsNullPredicate(),
                    UnaryProcedureUnaryFunction.adapt(throwNPE),
                    IdentityFunction.getIdentityFunction()
                )
            ),
            null
        );
    }

    private Map makeNullAsZeroMap() {
        return new FlexiMap(
            IgnoreLeftFunction.adapt(                        
                new ConditionalUnaryFunction(
                    IsNull.getIsNullPredicate(),
                    new ConstantFunction(new Integer(0)),
                    IdentityFunction.getIdentityFunction()
                )
            ),
            null
        );
    }

    private interface UniversalProcedure extends Procedure, UnaryProcedure, BinaryProcedure { }

    private UniversalProcedure throwNPE = new UniversalProcedure() {
        public void run() {
            throw new NullPointerException();
        }
        public void run(Object obj) {
            run();
        }
        public void run(Object left, Object right) {
            run();
        }
    };
    
}
