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

import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.adapter.RightBoundFunction;

/**
 * Adapts a {@link Comparator Comparator} to the
 * {@link BinaryFunction} interface.
 *
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class Min<T> implements BinaryFunction<T, T, T>, Serializable {
    /**
     * Basic Min instance.
     */
    public static final Min<Comparable<?>> INSTANCE = Min.<Comparable<?>>instance();

    private Comparator<T> comparator = null;

    /**
     * Create a new Min.
     */
    @SuppressWarnings("unchecked")
    public Min() {
        this(ComparableComparator.instance());
    }

    /**
     * Create a new Min.
     * @param comparator to use
     */
    public Min(Comparator<T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("Comparator argument must not be null");
        }
        this.comparator = comparator;
    }

    /**
     * {@inheritDoc}
     */
    public T evaluate(T left, T right) {
        return (comparator.compare(left, right) <= 0) ? left : right;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof Min<?> && equals((Min<?>) that));
    }

    /**
     * Learn whether another Min is equal to this.
     * @param that Min to test
     * @return boolean
     */
    public boolean equals(Min<?> that) {
        return null != that && comparator.equals(that.comparator);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return "Min".hashCode() ^ comparator.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "Min<" + comparator + ">";
    }

    /**
     * Get a basic Min instance.
     * @return Min
     */
    public static <T extends Comparable<?>> Min<T> instance() {
        return new Min<T>();
    }

    /**
     * Get a Min UnaryFunction.
     * @param right the right side argument of the Min function
     * @return UnaryFunction<T, T>
     */
    public static final <T extends Comparable<?>> UnaryFunction<T, T> instance(T right) {
        return RightBoundFunction.bind(new Min<T>(), right);
    }

}
