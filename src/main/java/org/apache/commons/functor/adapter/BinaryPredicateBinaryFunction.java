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

import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.BinaryPredicate;

/**
 * Adapts a
 * {@link BinaryPredicate BinaryPredicate}
 * to the
 * {@link BinaryFunction BinaryFunction} interface.
 * <p/>
 * Note that although this class implements
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if the
 * underlying predicate is.  Attempts to serialize
 * an instance whose delegate is not
 * <code>Serializable</code> will result in an exception.
 *
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class BinaryPredicateBinaryFunction<L, R> implements BinaryFunction<L, R, Boolean>, Serializable {
    /** The {@link BinaryPredicate BinaryPredicate} I'm wrapping. */
    private BinaryPredicate<? super L, ? super R> predicate;

    /**
     * Create a new BinaryPredicateBinaryFunction.
     * @param predicate to adapt
     */
    public BinaryPredicateBinaryFunction(BinaryPredicate<? super L, ? super R> predicate) {
        this.predicate = predicate;
    }

    /**
     * {@inheritDoc}
     * Returns <code>Boolean.TRUE</code> (<code>Boolean.FALSE</code>)
     * when the {@link BinaryPredicate#test test} method of my underlying
     * predicate returns <code>true</code> (<code>false</code>).
     *
     * @return a non-<code>null</code> <code>Boolean</code> instance
     */
    public Boolean evaluate(L left, R right) {
        return predicate.test(left, right) ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this
                || (that instanceof BinaryPredicateBinaryFunction<?, ?>
                && equals((BinaryPredicateBinaryFunction<?, ?>) that));
    }

    /**
     * Learn whether another BinaryPredicateBinaryFunction is equal to this.
     * @param that BinaryPredicateBinaryFunction to test
     * @return boolean
     */
    public boolean equals(BinaryPredicateBinaryFunction<?, ?> that) {
        return null != that && (null == predicate ? null == that.predicate : predicate.equals(that.predicate));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "BinaryPredicateBinaryFunction".hashCode();
        if (null != predicate) {
            hash ^= predicate.hashCode();
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "BinaryPredicateBinaryFunction<" + predicate + ">";
    }

    /**
     * Adapt the given, possibly-<code>null</code>,
     * {@link BinaryPredicate BinaryPredicate} to the
     * {@link BinaryFunction BinaryFunction} interface.
     * When the given <code>BinaryPredicate</code> is <code>null</code>,
     * returns <code>null</code>.
     *
     * @param <L> left type
     * @param <R> right type
     * @param predicate the possibly-<code>null</code>
     *        {@link BinaryPredicate BinaryPredicate} to adapt
     * @return a <code>BinaryPredicateBinaryFunction</code> wrapping the given
     *         {@link BinaryPredicate BinaryPredicate}, or <code>null</code>
     *         if the given <code>BinaryPredicate</code> is <code>null</code>
     */
    public static <L, R> BinaryPredicateBinaryFunction<L, R> adapt(BinaryPredicate<? super L, ? super R> predicate) {
        return null == predicate ? null : new BinaryPredicateBinaryFunction<L, R>(predicate);
    }

}
