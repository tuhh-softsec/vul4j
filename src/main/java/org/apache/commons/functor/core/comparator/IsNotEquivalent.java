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
 * <code>true</code> iff the left argument is not equal to the
 * right argument under the specified {@link Comparator}.
 * When no (or a <code>null</code> <code>Comparator</code> is specified,
 * a {@link Comparable Comparable} <code>Comparator</code> is used.
 *
 * @see org.apache.commons.functor.core.IsEqual
 *
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 *
 */
public final class IsNotEquivalent<T> implements BinaryPredicate<T, T>, Serializable {
    /**
     * Basic IsNotEquivalent instance.
     */
    public static final IsNotEquivalent<Comparable<?>> INSTANCE = IsNotEquivalent.<Comparable<?>>instance();

    private final Comparator<? super T> comparator;

    /**
     * Create a new IsNotEquivalent.
     */
    @SuppressWarnings("unchecked")
    public IsNotEquivalent() {
        this(ComparableComparator.INSTANCE);
    }

    /**
     * Construct a <code>IsNotEquivalent</code> {@link BinaryPredicate predicate}
     * for the given {@link Comparator Comparator}.
     *
     * @param comparator the {@link Comparator Comparator}, when <code>null</code>,
     *        a <code>Comparator</code> for {@link Comparable Comparable}s will
     *        be used.
     */
    public IsNotEquivalent(Comparator<? super T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("Comparator must not be null");
        }
        this.comparator = comparator;
    }

    /**
     * {@inheritDoc}
     * Return <code>true</code> iff the <i>left</i> parameter is
     * not equal to the <i>right</i> parameter under my current
     * {@link Comparator Comparator}.
     */
    public boolean test(T left, T right) {
        return comparator.compare(left, right) != 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof IsNotEquivalent<?> && equals((IsNotEquivalent<?>) that));
    }

    /**
     * Learn whether another IsNotEquivalent is equal to this.
     * @param that IsNotEquivalent to test
     * @return boolean
     */
    public boolean equals(IsNotEquivalent<?> that) {
        return null != that && null == comparator ? null == that.comparator : comparator.equals(that.comparator);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "IsNotEquivalent".hashCode();
        // by construction, comparator is never null
        hash ^= comparator.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "IsNotEquivalent<" + comparator + ">";
    }

    /**
     * Get an IsNotEquivalent instance.
     * @return IsNotEquivalent
     */
    @SuppressWarnings("unchecked")
    public static final <T extends Comparable<?>> IsNotEquivalent<T> instance() {
        return new IsNotEquivalent<T>(ComparableComparator.INSTANCE);
    }

    /**
     * Get an IsNotEquivalent UnaryPredicate.
     * @param right Comparable against which UnaryPredicate arguments will be compared.
     * @return UnaryPredicate
     */
    public static final <T extends Comparable<?>> UnaryPredicate<T> instance(T right) {
        return RightBoundPredicate.bind(instance(), right);
    }

}
