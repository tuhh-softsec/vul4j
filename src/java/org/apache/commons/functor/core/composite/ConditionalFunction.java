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

import org.apache.commons.functor.Function;
import org.apache.commons.functor.Predicate;

/**
 * A {@link Function Function} 
 * similiar to Java's "ternary" 
 * or "conditional" operator (<code>&#x3F; &#x3A;</code>).
 * Given a {@link Predicate predicate}
 * <i>p</i> and {@link Function functions}
 * <i>f</i> and <i>g</i>, {@link #evaluate evalautes}
 * to 
 * <code>p.test() ? f.evaluate() : g.evaluate()</code>.
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
public final class ConditionalFunction implements Function, Serializable {

    // constructor
    // ------------------------------------------------------------------------

    public ConditionalFunction(Predicate ifPred, Function thenPred, Function elsePred) {
        this.ifPred = ifPred;
        this.thenFunc = thenPred;
        this.elseFunc = elsePred;
    }
    
    // predicate interface
    // ------------------------------------------------------------------------
    public Object evaluate() {
        return ifPred.test() ? thenFunc.evaluate() : elseFunc.evaluate();
    }

    public boolean equals(Object that) {
        if(that instanceof ConditionalFunction) {
            return equals((ConditionalFunction)that);
        } else {
            return false;
        }
    }
    
    public boolean equals(ConditionalFunction that) {
        return null != that && 
                (null == ifPred ? null == that.ifPred : ifPred.equals(that.ifPred)) &&
                (null == thenFunc ? null == that.thenFunc : thenFunc.equals(that.thenFunc)) &&
                (null == elseFunc ? null == that.elseFunc : elseFunc.equals(that.elseFunc));
    }
    
    public int hashCode() {
        int hash = "ConditionalFunction".hashCode();
        if(null != ifPred) {
            hash <<= 4;
            hash ^= ifPred.hashCode();            
        }
        if(null != thenFunc) {
            hash <<= 4;
            hash ^= thenFunc.hashCode();            
        }
        if(null != elseFunc) {
            hash <<= 4;
            hash ^= elseFunc.hashCode();            
        }
        return hash;
    }
    
    public String toString() {
        return "ConditionalFunction<" + ifPred + "?" + thenFunc + ":" + elseFunc + ">";
    }

    // attributes
    // ------------------------------------------------------------------------
    private Predicate ifPred = null;
    private Function thenFunc = null;
    private Function elseFunc = null;
}
