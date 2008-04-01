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

import org.apache.commons.functor.BinaryPredicate;
import org.apache.commons.functor.UnaryPredicate;

/**
 * Adapts a
 * {@link UnaryPredicate UnaryPredicate} 
 * to the 
 * {@link BinaryPredicate BinaryPredicate} interface 
 * by ignoring the first binary argument.
 * <p/>
 * Note that although this class implements 
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if the
 * underlying functor is.  Attempts to serialize
 * an instance whose delegate is not 
 * <code>Serializable</code> will result in an exception.
 * 
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class IgnoreLeftPredicate implements BinaryPredicate, Serializable {
    public IgnoreLeftPredicate(UnaryPredicate predicate) {
        this.predicate = predicate;
    }
 
    public boolean test(Object left, Object right) {
        return predicate.test(right);
    }   

    public boolean equals(Object that) {
        if(that instanceof IgnoreLeftPredicate) {
            return equals((IgnoreLeftPredicate)that);
        } else {
            return false;
        }
    }
        
    public boolean equals(IgnoreLeftPredicate that) {
        return that == this || (null != that && (null == predicate ? null == that.predicate : predicate.equals(that.predicate)));
    }
    
    public int hashCode() {
        int hash = "IgnoreLeftPredicate".hashCode();
        if(null != predicate) {
            hash ^= predicate.hashCode();
        }
        return hash;
    }
    
    public String toString() {
        return "IgnoreLeftPredicate<" + predicate + ">";
    }

    public static IgnoreLeftPredicate adapt(UnaryPredicate predicate) {
        return null == predicate ? null : new IgnoreLeftPredicate(predicate);
    }

    /** The {@link UnaryPredicate UnaryPredicate} I'm wrapping. */
    private UnaryPredicate predicate = null;
}
