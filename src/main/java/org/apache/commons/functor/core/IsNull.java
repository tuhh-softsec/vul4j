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
public final class IsNull<A> implements UnaryPredicate<A>, Serializable {
    // static attributes
    // ------------------------------------------------------------------------
    /**
     * Basic IsNull instance.
     */
    public static final IsNull<Object> INSTANCE = IsNull.<Object>instance();

    /**
     * Left-handed BinaryPredicate.
     */
    public static final BinaryPredicate<Object, Object> LEFT = IsNull.<Object>left();

    /**
     * Right-handed BinaryPredicate.
     */
    public static final BinaryPredicate<Object, Object> RIGHT = IsNull.<Object>right();

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new IsNull.
     */
    public IsNull() {
    }

    // predicate interface
    // ------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public boolean test(A obj) {
        return (null == obj);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that instanceof IsNull<?>;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return "IsNull".hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "IsNull";
    }

    // static methods
    // ------------------------------------------------------------------------
    /**
     * Get an IsNull instance.
     * @return IsNull
     */
    public static <T> IsNull<T> instance() {
        return new IsNull<T>();
    }

    /**
     * Get a BinaryPredicate that matches if the left argument is null.
     * @return BinaryPredicate
     */
    public static <A> BinaryPredicate<A, Object> left() {
        return IgnoreRightPredicate.adapt(new IsNull<A>());
    }

    /**
     * Get a BinaryPredicate that matches if the right argument is null.
     * @return BinaryPredicate
     */
    public static <A> BinaryPredicate<Object, A> right() {
        return IgnoreLeftPredicate.adapt(new IsNull<A>());
    }

}
