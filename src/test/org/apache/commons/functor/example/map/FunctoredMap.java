/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/example/map/FunctoredMap.java,v 1.1 2003/11/26 01:18:28 rwaldhoff Exp $
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
package org.apache.commons.functor.example.map;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.BinaryProcedure;
import org.apache.commons.functor.Procedure;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.UnaryProcedure;

/**
 * @version $Revision: 1.1 $ $Date: 2003/11/26 01:18:28 $
 * @author Rodney Waldhoff
 */
public class FunctoredMap implements Map {
    public FunctoredMap(Map map) {
        this.map = map;
    }

    public int hashCode() {
        return map.hashCode();
    }

    public String toString() {
        return map.toString();
    }

    public Collection values() {
        return map.values();
    }

    public Set keySet() {
        return map.keySet();
    }

    public Object get(Object key) {
        return onget.evaluate(map,key);
    }

    public void clear() {
        onclear.run(map);
    }

    public int size() {
        return map.size();
    }

    public Object put(Object key, Object value) {
        return onput.evaluate(map, new Object[] { key, value });
    }

    public void putAll(Map src) {
        onputall.run(map, src);
    }

    public Set entrySet() {
        return map.entrySet();
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public Object remove(Object key) {
        return onremove.evaluate(map,key);
    }

    public boolean equals(Object obj) {
        return map.equals(obj);
    }

    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }
    
    // protected
    
    protected void setOnClear(UnaryProcedure procedure) {
        onclear = procedure;
    }

    protected void setOnPut(BinaryFunction function) {
        onput = function;
    }

    protected void setOnGet(BinaryFunction function) {
        onget = function;
    }

    protected void setOnPutAll(BinaryProcedure procedure) {
        onputall = procedure;
    }

    protected void setOnRemove(BinaryFunction function) {
        onremove = function;
    }

    // attributes
    
    protected static final BinaryFunction DEFAULT_ON_PUT = new BinaryFunction() {
        public Object evaluate(Object a, Object b) {
            Map map = (Map)a;
            Object key = Array.get(b,0);
            Object value = Array.get(b,1);
            return map.put(key,value);
        }
    };
    
    private BinaryFunction onput = DEFAULT_ON_PUT;

    protected static final BinaryFunction DEFAULT_ON_GET = new BinaryFunction() {
        public Object evaluate(Object map, Object key) {
            return ((Map)map).get(key);
        }
    };
    
    private BinaryFunction onget = DEFAULT_ON_GET;
    
    protected static final BinaryProcedure DEFAULT_ON_PUT_ALL = new BinaryProcedure() {
        public void run(Object a, Object b) {
            Map dest = (Map)a;
            Map src = (Map)b;
            dest.putAll(src);
        }
    };

    private BinaryProcedure onputall = DEFAULT_ON_PUT_ALL;    
    
    protected static final BinaryFunction DEFAULT_ON_REMOVE = new BinaryFunction() {
        public Object evaluate(Object a, Object key) {
            Map map = (Map)a;
            return map.remove(key);
        }
    };

    private BinaryFunction onremove = DEFAULT_ON_REMOVE;

    protected static final UnaryProcedure DEFAULT_ON_CLEAR = new UnaryProcedure() {
        public void run(Object map) {
            ((Map)map).clear();
        }
    };

    private UnaryProcedure onclear = DEFAULT_ON_CLEAR;
    
    private Map map = null;

    // inner classes
    
    protected static class ContainsKey implements UnaryPredicate {
        ContainsKey(Map map) {
            this.map = map;
        }
        
        public boolean test(Object obj) {
            return map.containsKey(obj);
        }
        
        private Map map = null;
    }

    protected static class Throw implements Procedure, UnaryProcedure, BinaryProcedure {
        Throw(RuntimeException e) {
            this.klass = e.getClass();
        }

        public void run() {
            try {
                throw (RuntimeException)(klass.newInstance());
            } catch(IllegalAccessException e) {
                throw new RuntimeException();
            } catch (InstantiationException e) {
                throw new RuntimeException();
            }
        }

        public void run(Object obj) {
            run();
        }

        public void run(Object a, Object b) {
            run();
        }
        
        private Class klass = null;
    }
}
