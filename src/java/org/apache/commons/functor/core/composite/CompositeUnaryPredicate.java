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

import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.UnaryPredicate;

/**
 * A {@link UnaryPredicate UnaryPredicate} 
 * representing the composition of 
 * {@link UnaryFunction UnaryFunctions},
 * "chaining" the output of one to the input
 * of another.  For example, 
 * <pre>new CompositeUnaryPredicate(p).of(f)</code>
 * {@link #test tests} to 
 * <code>p.test(f.evaluate(obj))</code>, and
 * <pre>new CompositeUnaryPredicate(p).of(f).of(g)</pre>
 * {@link #test tests} to 
 * <code>p.test(f.evaluate(g.evaluate(obj)))</code>.
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
public final class CompositeUnaryPredicate implements UnaryPredicate, Serializable {

    // constructor
    // ------------------------------------------------------------------------
    public CompositeUnaryPredicate(UnaryPredicate p) {
        if(null == p) { throw new NullPointerException(); }
        this.predicate = p;
        this.function = new CompositeUnaryFunction();
    }

    public CompositeUnaryPredicate(UnaryPredicate p, UnaryFunction f) {
        if(null == p) { throw new NullPointerException(); }
        if(null == f) { throw new NullPointerException(); }
        this.predicate = p;
        this.function = new CompositeUnaryFunction(f);
    }

    // modifiers
    // ------------------------------------------------------------------------ 
    public CompositeUnaryPredicate of(UnaryFunction f) {
        function.of(f);
        return this;
    }
 
    // predicate interface
    // ------------------------------------------------------------------------
    public boolean test(Object obj) {
        return predicate.test(function.evaluate(obj)); 
    }

    public boolean equals(Object that) {
        if(that instanceof CompositeUnaryPredicate) {
            return equals((CompositeUnaryPredicate)that);
        } else {
            return false;
        }
    }
    
    public boolean equals(CompositeUnaryPredicate that) {
        return null != that && predicate.equals(that.predicate) && function.equals(that.function);
    }
    
    public int hashCode() {
        int hash = "CompositeUnaryPredicate".hashCode();
        hash <<= 2;
        hash ^= predicate.hashCode();
        hash <<= 2;
        hash ^= function.hashCode();
        return hash;
    }
    
    public String toString() {
        return "CompositeUnaryFunction<" + predicate + ";" + function + ">";
    }
    
    // attributes
    // ------------------------------------------------------------------------
    private CompositeUnaryFunction function = null;
    private UnaryPredicate predicate = null;

}
