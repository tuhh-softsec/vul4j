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
package org.apache.commons.functor.core.composite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.functor.UnaryFunction;

/**
 * A {@link UnaryFunction UnaryFunction} 
 * representing the composition of 
 * {@link UnaryFunction UnaryFunctions},
 * "chaining" the output of one to the input
 * of another.  For example, 
 * <pre>new CompositeUnaryFunction(f).of(g)</code>
 * {@link #evaluate evaluates} to 
 * <code>f.evaluate(g.evaluate(obj))</code>, and
 * <pre>new CompositeUnaryFunction(f).of(g).of(h)</pre>
 * {@link #evaluate evaluates} to 
 * <code>f.evaluate(g.evaluate(h.evaluate(obj)))</code>.
 * <p>
 * When the collection is empty, this function is 
 * an identity function.
 * </p>
 * <p>
 * Note that although this class implements 
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if all the
 * underlying functors are.  Attempts to serialize
 * an instance whose delegates are not all 
 * <code>Serializable</code> will result in an exception.
 * </p>
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class CompositeUnaryFunction implements UnaryFunction, Serializable {

    // constructor
    // ------------------------------------------------------------------------
    public CompositeUnaryFunction() {
    }

    public CompositeUnaryFunction(UnaryFunction f) {
        of(f);
    }

    public CompositeUnaryFunction(UnaryFunction f, UnaryFunction g) {
        of(f);
        of(g);
    }

    // modifiers
    // ------------------------------------------------------------------------ 
    public CompositeUnaryFunction of(UnaryFunction f) {
        list.add(f);
        return this;
    }
 
    // predicate interface
    // ------------------------------------------------------------------------
    public Object evaluate(Object obj) {        
        Object result = obj;
        for(ListIterator iter = list.listIterator(list.size()); iter.hasPrevious();) {
            result = ((UnaryFunction)iter.previous()).evaluate(result);
        }
        return result;
    }

    public boolean equals(Object that) {
        if(that instanceof CompositeUnaryFunction) {
            return equals((CompositeUnaryFunction)that);
        } else {
            return false;
        }
    }
    
    public boolean equals(CompositeUnaryFunction that) {
        // by construction, list is never null
        return null != that && list.equals(that.list);
    }
    
    public int hashCode() {
        // by construction, list is never null
        return "CompositeUnaryFunction".hashCode() ^ list.hashCode();
    }
    
    public String toString() {
        return "CompositeUnaryFunction<" + list + ">";
    }
    
    
    // attributes
    // ------------------------------------------------------------------------
    private List list = new ArrayList();

}
