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
package org.apache.commons.functor.core.collection;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;

import org.apache.commons.functor.UnaryFunction;

/**
 * Returns the size of the specified Collection, or the length
 * of the specified array or String.
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class Size implements UnaryFunction, Serializable {

    // constructor
    // ------------------------------------------------------------------------
    
    public Size() { }
    
    public Object evaluate(Object obj) {
        if(obj instanceof Collection) {
            return evaluate((Collection)obj);
        } else if(obj instanceof String) {
            return evaluate((String)obj);
        } else if(null != obj && obj.getClass().isArray()) {
            return evaluateArray(obj);
        } else if(null == obj){
            throw new NullPointerException("Argument must not be null");
        } else {
            throw new ClassCastException("Expected Collection, String or Array, found " + obj);
        } 
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object that) {
        return that instanceof Size;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return "Size".hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Size()";
    }

    public static final Size instance() {
        return INSTANCE;
    }

    private Object evaluate(Collection col) {
        return new Integer(col.size());
    }
    
    private Object evaluate(String str) {
        return new Integer(str.length());
    }
    
    private Object evaluateArray(Object array) {
        return new Integer(Array.getLength(array));
    }
    
    private static final Size INSTANCE = new Size();
    
}
