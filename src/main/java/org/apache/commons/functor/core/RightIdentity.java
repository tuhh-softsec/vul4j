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

import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.BinaryPredicate;
import org.apache.commons.functor.adapter.BinaryFunctionBinaryPredicate;
import org.apache.commons.functor.adapter.IgnoreLeftFunction;

/**
 * Holder class for a right-identity <code>BinaryFunction</code> (evaluates to the right argument) and a right-identity
 * <code>BinaryPredicate</code> (tests whether right <code>Boolean</code> argument equals <code>Boolean.TRUE</code>).
 *
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 * @author Matt Benson
 */
public final class RightIdentity {

    // static attributes
    // ------------------------------------------------------------------------
    /**
     * Right-identity function.
     */
    public static final BinaryFunction<Object, Object, Object> FUNCTION = RightIdentity.<Object, Object>function();

    /**
     * Right-identity predicate.
     */
    public static final BinaryPredicate<Object, Boolean> PREDICATE = BinaryFunctionBinaryPredicate.adapt(IgnoreLeftFunction.adapt(new Identity<Boolean>()));

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new RightIdentity.
     */
    public RightIdentity() {
    }

    // static methods
    // ------------------------------------------------------------------------

    /**
     * Get a typed right-identity BinaryFunction.
     * @param <L>
     * @param <R>
     * @return BinaryFunction<L, R, R>
     */
    public static <L, R> BinaryFunction<L, R, R> function() {
        return IgnoreLeftFunction.adapt(new Identity<R>());
    }

    /**
     * Get a typed right-identity BinaryPredicate. 
     * @param <L>
     * @return BinaryPredicate<L, Boolean>
     */
    public static <L> BinaryPredicate<L, Boolean> predicate() {
        return BinaryFunctionBinaryPredicate.adapt(IgnoreLeftFunction.<L, Boolean, Boolean>adapt(new Identity<Boolean>()));
    }
}
