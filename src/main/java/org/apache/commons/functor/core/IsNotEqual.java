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
package org.apache.commons.functor.core;

import java.io.Serializable;

import org.apache.commons.functor.BinaryPredicate;

/**
 * {@link #test Tests} 
 * <code>true</code> iff its arguments are 
 * not {@link Object#equals equal} or both 
 * <code>null</code>.
 * <p>
 * This relation is symmetric but irreflexive 
 * and not transitive.
 * </p>
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class IsNotEqual implements BinaryPredicate, Serializable {

    // constructor
    // ------------------------------------------------------------------------
    public IsNotEqual() {
    }
 
    // predicate interface
    // ------------------------------------------------------------------------

    public boolean test(Object left, Object right) {
        return (null == left ? null != right : !left.equals(right));
    }

    public boolean equals(Object that) {
        return that instanceof IsNotEqual;
    }
    
    public int hashCode() {
        return "IsNotEqual".hashCode();
    }
    
    public String toString() {
        return "IsNotEqual";
    }
        
    // static methods
    // ------------------------------------------------------------------------
    public static IsNotEqual instance() {
        return INSTANCE;
    }
    
    // static attributes
    // ------------------------------------------------------------------------
    private static final IsNotEqual INSTANCE = new IsNotEqual();

}
