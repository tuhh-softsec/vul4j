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
package org.apache.commons.functor.adapter;

import java.io.Serializable;

import org.apache.commons.functor.Function;
import org.apache.commons.functor.UnaryFunction;

/**
 * Adapts a
 * {@link UnaryFunction UnaryFunction} 
 * to the 
 * {@link Function Function} interface 
 * using a constant unary argument.
 * <p/>
 * Note that although this class implements 
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if the
 * underlying objects are.  Attempts to serialize
 * an instance whose delegates are not 
 * <code>Serializable</code> will result in an exception.
 * 
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class BoundFunction implements Function, Serializable {
    /**
     * @param function the function to adapt
     * @param arg the constant argument to use
     */
    public BoundFunction(UnaryFunction function, Object arg) {
        this.function = function;
        this.param = arg;
    }
 
    public Object evaluate() {
        return function.evaluate(param);
    }   

    public boolean equals(Object that) {
        if(that instanceof BoundFunction) {
            return equals((BoundFunction)that);
        } else {
            return false;
        }
    }
        
    public boolean equals(BoundFunction that) {
        return that == this || ( 
                (null != that) && 
                (null == function ? null == that.function : function.equals(that.function)) &&
                (null == param ? null == that.param : param.equals(that.param)) );
                
    }
    
    public int hashCode() {
        int hash = "BoundFunction".hashCode();
        if(null != function) {
            hash <<= 2;
            hash ^= function.hashCode();
        }
        if(null != param) {
            hash <<= 2;
            hash ^= param.hashCode();
        }
        return hash;
    }
    
    public String toString() {
        return "BoundFunction<" + function + "(" + param + ")>";
    }

    /**
     * Adapt the given, possibly-<code>null</code>, 
     * {@link UnaryFunction UnaryFunction} to the
     * {@link Function Function} interface by binding
     * the specified <code>Object</code> as a constant
     * argument.
     * When the given <code>UnaryFunction</code> is <code>null</code>,
     * returns <code>null</code>.
     * 
     * @param function the possibly-<code>null</code> 
     *        {@link UnaryFunction UnaryFunction} to adapt
     * @param arg the object to bind as a constant argument
     * @return a <code>BoundFunction</code> wrapping the given
     *         {@link UnaryFunction UnaryFunction}, or <code>null</code>
     *         if the given <code>UnaryFunction</code> is <code>null</code>
     */
    public static BoundFunction bind(UnaryFunction function, Object arg) {
        return null == function ? null : new BoundFunction(function,arg);
    }

    /** The {@link UnaryFunction UnaryFunction} I'm wrapping. */
    private UnaryFunction function = null;
    /** The parameter to pass to that function. */
    private Object param = null;
}
