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
 * {@link #test Tests} the reference (!=) inequality of its arguments.
 *
 * @version $Revision$ $Date$
 * @author Matt Benson
 */
public final class IsNotSame<L, R> implements BinaryPredicate<L, R>, Serializable {
    // static attributes
    // ------------------------------------------------------------------------
    /**
     * Basic IsNotSame<Object, Object> instance.
     */
    public static final IsNotSame<Object, Object> INSTANCE = IsNotSame.<Object, Object>instance();

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new IsNotSame.
     */
    public IsNotSame() {
    }

    // predicate interface
    // ------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public boolean test(L left, R right) {
        return left != right;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that instanceof IsNotSame;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return "IsNotSame".hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "IsNotSame";
    }

    // static methods
    // ------------------------------------------------------------------------
    /**
     * Get an IsNotSame instance.
     * @return IsNotSame
     */
    public static <L, R> IsNotSame<L, R> instance() {
        return new IsNotSame<L, R>();
    }

    /**
     * Get an IsNotSame UnaryPredicate.
     * @param <L>
     * @param <R>
     * @param object bound comparison object
     * @return UnaryPredicate<L>
     */
    public static <L, R> UnaryPredicate<L> to(R object) {
        return new RightBoundPredicate<L, R>(new IsNotSame<L, R>(), object);
    }
}
