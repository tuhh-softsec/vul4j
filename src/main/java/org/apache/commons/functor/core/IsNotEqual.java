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
 * not {@link Object#equals equal} or both
 * <code>null</code>.
 * <p>
 * This relation is symmetric but irreflexive
 * and not transitive.
 * </p>
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class IsNotEqual<L, R> implements BinaryPredicate<L, R>, Serializable {
    // static attributes
    // ------------------------------------------------------------------------
    /**
     * Basic IsNotEqual<Object, Object> instance.
     */
    public static final IsNotEqual<Object, Object> INSTANCE = IsNotEqual.<Object, Object>instance();

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new IsNotEqual.
     */
    public IsNotEqual() {
    }

    // predicate interface
    // ------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public boolean test(L left, R right) {
        return (null == left ? null != right : !left.equals(right));
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that instanceof IsNotEqual<?, ?>;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return "IsNotEqual".hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "IsNotEqual";
    }

    // static methods
  // ------------------------------------------------------------------------
    
    /**
     * Get an IsNotEqual instance.
     * @param <L>
     * @param <R>
     * @return IsNotEqual<L, R>
     */
    public static <L, R> IsNotEqual<L, R> instance() {
        return new IsNotEqual<L, R>();
    }

    /**
     * Get an IsNotEqual UnaryPredicate.
     * @param <L>
     * @param <R>
     * @param object bound comparison object
     * @return UnaryPredicate<L>
     */
    public static <L, R> UnaryPredicate<L> to(R object) {
        return new RightBoundPredicate<L, R>(new IsNotEqual<L, R>(), object);
    }
}
