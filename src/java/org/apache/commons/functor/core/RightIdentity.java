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

import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.BinaryPredicate;

/**
 * {@link #evaluate Evaluates} to its second argument.
 * 
 * {@link #test Tests} to the <code>boolean</code>
 * value of the <code>Boolean</code>-valued second
 * argument. The {@link #test test} method 
 * throws an exception if the parameter isn't a 
 * non-<code>null</code> <code>Boolean</code>.
 * 
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class RightIdentity implements BinaryPredicate, BinaryFunction, Serializable {
    
    // constructor
    // ------------------------------------------------------------------------
    public RightIdentity() {
    }
 
    // functor interface
    // ------------------------------------------------------------------------

    public Object evaluate(Object left, Object right) {
        return right;
    }

    public boolean test(Object left, Object right) {
        return test((Boolean)right);
    }

    private boolean test(Boolean bool) {
        return bool.booleanValue();
    }

    public boolean equals(Object that) {
        return (that instanceof RightIdentity);
    }
    
    public int hashCode() {
        return "RightIdentity".hashCode();
    }
    
    public String toString() {
        return "RightIdentity";
    }
    
    // static methods
    // ------------------------------------------------------------------------
    public static RightIdentity instance() {
        return INSTANCE;
    }
    
    // static attributes
    // ------------------------------------------------------------------------
    private static final RightIdentity INSTANCE = new RightIdentity();
}
