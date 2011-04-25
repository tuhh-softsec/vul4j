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
package org.apache.commons.functor.core.comparator;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.functor.BinaryPredicate;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.adapter.RightBoundPredicate;

/**
 * A {@link BinaryPredicate BinaryPredicate} that {@link #test tests}
 * <code>true</code> iff the left argument is greater than the
 * right argument under the specified {@link Comparator}.
 * When no (or a <code>null</code> <code>Comparator</code> is specified,
 * a {@link Comparable Comparable} <code>Comparator</code> is used.
 *
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class IsLessThan<T> implements BinaryPredicate<T, T>, Serializable {
    /**
     * Basic IsLessThan instance.
     */
    public static final IsLessThan<Comparable<?>> INSTANCE = IsLessThan.<Comparable<?>>instance();

    private final Comparator<? super T> comparator;

    /**
     * Construct a <code>IsLessThan</code> {@link BinaryPredicate predicate}
     * for {@link Comparable Comparable}s.
     */
    @SuppressWarnings("unchecked")
    public IsLessThan() {
        this(ComparableComparator.INSTANCE);
    }

    /**
     * Construct a <code>IsLessThan</code> {@link BinaryPredicate predicate}
     * for the given {@link Comparator Comparator}.
     *
     * @param comparator the {@link Comparator Comparator}, when <code>null</code>,
     *        a <code>Comparator</code> for {@link Comparable Comparable}s will
     *        be used.
     */
    public IsLessThan(Comparator<? super T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("Comparator argument must not be null");
        }
        this.comparator = comparator;
    }

    /**
     * Return <code>true</code> iff the <i>left</i> parameter is
     * less than the <i>right</i> parameter under my current
     * {@link Comparator Comparator}.
     * {@inheritDoc}
     */
    public boolean test(T left, T right) {
        return comparator.compare(left, right) < 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof IsLessThan<?> && equals((IsLessThan<?>) that));
    }

    /**
     * Learn whether a given IsLessThan is equal to this.
     * @param that IsLessThan to test
     * @return boolean
     */
    public boolean equals(IsLessThan<?> that) {
        return null != that && null == comparator ? null == that.comparator : comparator.equals(that.comparator);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "IsLessThan".hashCode();
        // by construction, comparator is never null
        hash ^= comparator.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "IsLessThan<" + comparator + ">";
    }

    /**
     * Get a typed IsLessThan instance.
     * @param <T>
     * @return IsLessThan<T>
     */
    public static final <T extends Comparable<?>> IsLessThan<T> instance() {
        return new IsLessThan<T>();
    }

    /**
     * Get an IsLessThan UnaryPredicate.
     * @param right the right side object of the comparison.
     * @return UnaryPredicate
     */
    public static final <T extends Comparable<?>> UnaryPredicate<T> instance(T right) {
        return RightBoundPredicate.bind(new IsLessThan<T>(), right);
    }

}
