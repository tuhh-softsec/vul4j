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
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
@SuppressWarnings("unchecked")
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
            Map map = (Map) a;
            Object key = Array.get(b,0);
            Object value = Array.get(b,1);
            return map.put(key,value);
        }
    };

    private BinaryFunction onput = DEFAULT_ON_PUT;

    protected static final BinaryFunction DEFAULT_ON_GET = new BinaryFunction() {
        public Object evaluate(Object map, Object key) {
            return ((Map) map).get(key);
        }
    };

    private BinaryFunction onget = DEFAULT_ON_GET;

    protected static final BinaryProcedure DEFAULT_ON_PUT_ALL = new BinaryProcedure() {
        public void run(Object a, Object b) {
            Map dest = (Map) a;
            Map src = (Map) b;
            dest.putAll(src);
        }
    };

    private BinaryProcedure onputall = DEFAULT_ON_PUT_ALL;

    protected static final BinaryFunction DEFAULT_ON_REMOVE = new BinaryFunction() {
        public Object evaluate(Object a, Object key) {
            Map map = (Map) a;
            return map.remove(key);
        }
    };

    private BinaryFunction onremove = DEFAULT_ON_REMOVE;

    protected static final UnaryProcedure DEFAULT_ON_CLEAR = new UnaryProcedure() {
        public void run(Object map) {
            ((Map) map).clear();
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
