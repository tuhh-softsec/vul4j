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
 * <code>false</code> iff its argument
 * is <code>null</code>.
 *
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class IsNotNull<T> implements UnaryPredicate<T>, Serializable {
    // static attributes
    // ------------------------------------------------------------------------
    /**
     * Basic IsNotNull instance.
     */
    public static final IsNotNull<Object> INSTANCE = IsNotNull.<Object>instance();

    /**
     * Left-handed BinaryPredicate.
     */
    public static final BinaryPredicate<Object, Object> LEFT = IsNotNull.<Object>left();

    /**
     * Right-handed BinaryPredicate.
     */
    public static final BinaryPredicate<Object, Object> RIGHT = IsNotNull.<Object>right();

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new IsNotNull.
     */
    public IsNotNull() {
    }

    // predicate interface
    // ------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public boolean test(Object obj) {
        return (null != obj);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that instanceof IsNotNull<?>;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return "IsNotNull".hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "IsNotNull";
    }

    // static methods
    // ------------------------------------------------------------------------
    /**
     * Get an IsNotNull instance.
     * @return IsNotNull
     */
    public static <T> IsNotNull<T> instance() {
        return new IsNotNull<T>();
    }

    /**
     * Get a BinaryPredicate that matches if the left argument is not null.
     * @return BinaryPredicate<A, Object>
     */
    public static <A> BinaryPredicate<A, Object> left() {
        return IgnoreRightPredicate.adapt(new IsNotNull<A>());
    }

    /**
     * Get a BinaryPredicate that matches if the right argument is null.
     * @return BinaryPredicate<Object, A>
     */
    public static <A> BinaryPredicate<Object, A> right() {
        return IgnoreLeftPredicate.adapt(new IsNotNull<A>());
    }

}
