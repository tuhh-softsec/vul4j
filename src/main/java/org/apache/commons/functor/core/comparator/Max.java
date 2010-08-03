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
public final class Max<T> implements BinaryFunction<T, T, T>, Serializable {
    /**
     * Basic Max instance.
     */
    public static final Max<Comparable<?>> INSTANCE = Max.<Comparable<?>>instance();

    private Comparator<T> comparator = null;

    /**
     * Create a new Max.
     */
    @SuppressWarnings("unchecked")
    public Max() { 
        this(ComparableComparator.instance());
    }

    /**
     * Create a new Max.
     * @param comparator Comparator to use
     */
    public Max(Comparator<T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("Comparator argument must not be null");
        }
        this.comparator = comparator;
    }

    /**
     * {@inheritDoc}
     */
    public T evaluate(T left, T right) {
        return (comparator.compare(left, right) >= 0) ? left : right;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof Max<?> && equals((Max<?>) that));
    }

    /**
     * Learn whether another Max is equal to this.
     * @param that Max to test
     * @return boolean
     */
    public boolean equals(Max<?> that) {
        return null != that && comparator.equals(that.comparator);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return "Max".hashCode() ^ comparator.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "Max<" + comparator + ">";
    }

    /**
     * Get a Max instance.
     * @return Max
     */
    public static <T extends Comparable<?>> Max<T> instance() {
        return new Max<T>();
    }

    /**
     * Get a Max UnaryFunction.
     * @param right the right side argument of the Max function
     * @return UnaryFunction<T, T>
     */
    public static final <T extends Comparable<?>> UnaryFunction<T, T> instance(T right) {
        return RightBoundFunction.bind(new Max<T>(), right);
    }

}
