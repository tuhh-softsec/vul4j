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
import org.apache.commons.functor.adapter.IgnoreRightFunction;

/**
 * Holder class for a left-identity <code>BinaryFunction</code> (evaluates to the left argument) and a left-identity
 * <code>BinaryPredicate</code> (tests whether left <code>Boolean</code> argument equals <code>Boolean.TRUE</code>).
 *
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 * @author Matt Benson
 */
public final class LeftIdentity {

    // static attributes
    // ------------------------------------------------------------------------
    /**
     * Left-identity function.
     */
    public static final BinaryFunction<Object, Object, Object> FUNCTION = LeftIdentity.<Object, Object>function();

    /**
     * Left-identity predicate.
     */
    public static final BinaryPredicate<Boolean, Object> PREDICATE = LeftIdentity.<Object>predicate();

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new LeftIdentity (for clients that require an object).
     */
    public LeftIdentity() {
    }

    // static methods
    // ------------------------------------------------------------------------

    /**
     * Get a Left-identity BinaryFunction.
     * @param <L>
     * @param <R>
     * @return BinaryFunction<L, R, L>
     */
    public static <L, R> BinaryFunction<L, R, L> function() {
        return IgnoreRightFunction.adapt(new Identity<L>());
    }

    /**
     * Get a left-identity BinaryPredicate.
     * @param <R>
     * @return BinaryPredicate<Boolean, R>
     */
    public static <R> BinaryPredicate<Boolean, R> predicate() {
        return BinaryFunctionBinaryPredicate.adapt(IgnoreRightFunction.<Boolean, R, Boolean>adapt(new Identity<Boolean>()));
    }
}
