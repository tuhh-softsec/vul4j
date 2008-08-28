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
package org.apache.commons.functor.adapter;

import java.io.Serializable;

import org.apache.commons.functor.BinaryPredicate;
import org.apache.commons.functor.Predicate;
import org.apache.commons.functor.UnaryPredicate;

/**
 * Adapts a
 * {@link BinaryPredicate BinaryPredicate}
 * to the
 * {@link UnaryPredicate UnaryPredicate} interface
 * using a constant left-side argument.
 * <p/>
 * Note that although this class implements
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if the
 * underlying objects are.  Attempts to serialize
 * an instance whose delegates are not
 * <code>Serializable</code> will result in an exception.
 *
 * @version $Revision$ $Date$
 * @author Matt Benson
 */
public final class FullyBoundPredicate<L, R> implements Predicate, Serializable {

    /** The {@link BinaryPredicate BinaryPredicate} I'm wrapping. */
    private BinaryPredicate<? super L, ? super R> predicate;
    /** The left parameter to pass to that predicate. */
    private L left;
    /** The right parameter to pass to that predicate. */
    private R right;

    /**
     * Create a new FullyBoundPredicate.
     * @param predicate the predicate to adapt
     * @param left the left argument to use
     * @param right the right argument to use
     */
    public FullyBoundPredicate(BinaryPredicate<? super L, ? super R> predicate, L left, R right) {
        if (predicate == null) {
            throw new IllegalArgumentException("BinaryPredicate argument was null");
        }
        this.predicate = predicate;
        this.left = left;
        this.right = right;
    }

    /**
     * {@inheritDoc}
     */
    public boolean test() {
        return predicate.test(left, right);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof FullyBoundPredicate && equals((FullyBoundPredicate<?, ?>) that));
    }

    /**
     * Learn whether another FullyBoundPredicate is equal to this.
     * @param that FullyBoundPredicate to test
     * @return boolean
     */
    public boolean equals(FullyBoundPredicate<?, ?> that) {
        return null != that && (null == predicate ? null == that.predicate : predicate.equals(that.predicate))
                && (null == left ? null == that.left : left.equals(that.left))
                && (null == right ? null == that.right : right.equals(that.right));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "FullyBoundPredicate".hashCode();
        if (null != predicate) {
            hash <<= 2;
            hash ^= predicate.hashCode();
        }
        hash <<= 2;
        if (null != left) {
            hash ^= left.hashCode();
        }
        hash <<= 2;
        if (null != right) {
            hash ^= right.hashCode();
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "FullyBoundPredicate<" + predicate + "(" + left + ", " + right + ")>";
    }

    /**
     * Adapt a BinaryPredicate to the Predicate interface.
     * @param predicate to adapt
     * @param left L argument to always send as the left operand to the wrapped function
     * @param right R argument to always send as the right operand to the wrapped function
     * @return FullyBoundPredicate
     */
    public static <L, R> FullyBoundPredicate<L, R> bind(BinaryPredicate<? super L, ? super R> predicate, L left, R right) {
        return null == predicate ? null : new FullyBoundPredicate<L, R>(predicate, left, right);
    }
}
