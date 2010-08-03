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

/**
 * Adapts a {@link Comparator Comparator} to the
 * {@link BinaryFunction} interface.
 *
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class ComparatorFunction<T> implements BinaryFunction<T, T, Integer>, Serializable {
    /**
     * Basic ComparatorFunction instance.
     */
    public static final ComparatorFunction<Comparable<?>> INSTANCE = ComparatorFunction.<Comparable<?>>instance();

    private Comparator<? super T> comparator;

    /**
     * Create a new ComparatorFunction.
     * @param comparator to wrap
     */
    public ComparatorFunction(Comparator<? super T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("Comparator must not be null");
        }
        this.comparator = comparator;
    }

    /**
     * {@inheritDoc}
     */
    public Integer evaluate(T left, T right) {
        return new Integer(comparator.compare(left, right));
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof ComparatorFunction<?> && equals((ComparatorFunction<?>) that));
    }

    /**
     * Learn whether a specified ComparatorFunction is equal to this.
     * @param that the ComparatorFunction to test
     * @return boolean
     */
    public boolean equals(ComparatorFunction<?> that) {
        return null != that && comparator.equals(that.comparator);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return "ComparatorFunction".hashCode() ^ comparator.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "ComparatorFunction<" + comparator + ">";
    }

    /**
     * Get a basic ComparatorFunction instance.
     * @return ComparatorFunction
     */
    @SuppressWarnings("unchecked")
    public static <T> ComparatorFunction<T> instance() {
        return new ComparatorFunction<T>(ComparableComparator.INSTANCE);
    }
}
