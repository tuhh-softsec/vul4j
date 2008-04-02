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

import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.UnaryPredicate;

/**
 * {@link #evaluate Evaluates} to its input argument.
 * 
 * {@link #test Tests} to the <code>boolean</code>
 * value of the <code>Boolean</code>-valued parameter.
 * The {@link #test} method throws an exception if 
 * the parameter isn't a non-<code>null</code> 
 * <code>Boolean</code>.
 * 
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class Identity implements UnaryFunction, UnaryPredicate, Serializable {

    // constructor
    // ------------------------------------------------------------------------
    public Identity() {
    }
 
    // function interface
    // ------------------------------------------------------------------------
    public Object evaluate(Object obj) {
        return obj;
    }

    public boolean test(Object obj) {
        return test((Boolean)obj);
    }

    public boolean test(Boolean bool) {
        return bool.booleanValue();
    }

    public boolean equals(Object that) {
        return (that instanceof Identity);
    }
    
    public int hashCode() {
        return "Identity".hashCode();
    }
    
    public String toString() {
        return "Identity";
    }
    
    // static methods
    // ------------------------------------------------------------------------
    public static Identity instance() {
        return INSTANCE;
    }
    
    // static attributes
    // ------------------------------------------------------------------------
    private static final Identity INSTANCE = new Identity();
}
