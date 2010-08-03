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
import org.apache.commons.functor.adapter.RightBoundPredicate;

/**
 * {@link #test Tests}
 * <code>true</code> iff its arguments are
 * {@link Object#equals equal} or both
 * <code>null</code>.
 * <p>
 * This relation is
 * an equivalence relation on
 * the set of objects that adhere to the
 * <code>Object.equals</code> contract.
 * </p>
 *
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class IsEqual<L, R> implements BinaryPredicate<L, R>, Serializable {
    // static attributes
    // ------------------------------------------------------------------------
    /**
     * Basic IsEqual<Object, Object> instance.
     */
    public static final IsEqual<Object, Object> INSTANCE = IsEqual.<Object, Object>instance();

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new IsEqual.
     */
    public IsEqual() {
    }

    // predicate interface
    // ------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public boolean test(L left, R right) {
        return left == right || left != null && left.equals(right); 
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that instanceof IsEqual<?, ?>;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return "IsEqual".hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "IsEqual";
    }

    // static methods
    // ------------------------------------------------------------------------
    /**
     * Get an IsEqual instance.
     * @return IsEqual
     */
    public static <L, R> IsEqual<L, R> instance() {
        return new IsEqual<L, R>();
    }

    /**
     * Get an IsEqual UnaryPredicate.
     * @param <L>
     * @param <R>
     * @param object bound comparison object
     * @return UnaryPredicate<L>
     */
    public static <L, R> UnaryPredicate<L> to(R object) {
        return new RightBoundPredicate<L, R>(new IsEqual<L, R>(), object);
    }
}
