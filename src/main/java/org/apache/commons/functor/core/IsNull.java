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
package org.apache.commons.functor.core;

import java.io.Serializable;

import org.apache.commons.functor.BinaryPredicate;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.adapter.IgnoreLeftPredicate;
import org.apache.commons.functor.adapter.IgnoreRightPredicate;

/**
 * {@link #test Tests} 
 * <code>true</code> iff its argument 
 * is <code>null</code>.
 * 
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class IsNull implements UnaryPredicate, Serializable {

    // constructor
    // ------------------------------------------------------------------------
    public IsNull() {
    }
 
    // predicate interface
    // ------------------------------------------------------------------------

    public boolean test(Object obj) {
        return (null == obj);
    }

    public boolean equals(Object that) {
        return that instanceof IsNull;
    }
    
    public int hashCode() {
        return "IsNull".hashCode();
    }
    
    public String toString() {
        return "IsNull";
    }
        
    // static methods
    // ------------------------------------------------------------------------
    public static IsNull instance() {
        return INSTANCE;
    }
    
    public static BinaryPredicate left() {
        return LEFT;
    }

    public static BinaryPredicate right() {
        return RIGHT;
    }
    
    // static attributes
    // ------------------------------------------------------------------------
    private static final IsNull INSTANCE = new IsNull();
    private static final BinaryPredicate LEFT = IgnoreRightPredicate.adapt(instance());
    private static final BinaryPredicate RIGHT = IgnoreLeftPredicate.adapt(instance());

}
