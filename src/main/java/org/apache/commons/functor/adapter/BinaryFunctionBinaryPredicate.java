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
 * Adapts a <code>Boolean</code>-valued {@link BinaryFunction BinaryFunction} to
 * the {@link BinaryPredicate BinaryPredicate} interface. <p> Note that although
 * this class implements {@link Serializable}, a given instance will only be
 * truly <code>Serializable</code> if the underlying functor is. Attempts to
 * serialize an instance whose delegate is not <code>Serializable</code> will
 * result in an exception. </p>
 * @version $Revision$ $Date: 2008-06-10 10:31:53 -0500 (Tue, 10 Jun
 * 2008) $
 * @author Rodney Waldhoff
 */
public final class BinaryFunctionBinaryPredicate<L, R> implements BinaryPredicate<L, R>, Serializable {
    /** The {@link BinaryFunction BinaryFunction} I'm wrapping. */
    private BinaryFunction<? super L, ? super R, Boolean> function;

    /**
     * Create an {@link BinaryPredicate BinaryPredicate} wrapping the given
     * {@link BinaryFunction BinaryFunction}.
     * @param function the {@link BinaryFunction BinaryFunction} to wrap
     */
    public BinaryFunctionBinaryPredicate(BinaryFunction<? super L, ? super R, Boolean> function) {
        if (function == null) {
            throw new IllegalArgumentException("BinaryFunction argument must not be null");
        }
        this.function = function;
    }

    /**
     * {@inheritDoc} Returns the <code>boolean</code> value of the
     * non-<code>null</code> <code>Boolean</code> returned by the
     * {@link BinaryFunction#evaluate evaluate} method of my underlying
     * function.
     * 
     * @throws NullPointerException if my underlying function returns
     * <code>null</code>
     * @throws ClassCastException if my underlying function returns a
     * non-<code>Boolean</code>
     */
    public boolean test(L left, R right) {
        return function.evaluate(left, right);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this
                || (that instanceof BinaryFunctionBinaryPredicate<?, ?>
                && equals((BinaryFunctionBinaryPredicate<?, ?>) that));
    }

    /**
     * Learn whether another BinaryFunctionBinaryPredicate is equal to this.
     * @param that BinaryFunctionBinaryPredicate to test
     * @return boolean
     */
    public boolean equals(BinaryFunctionBinaryPredicate<?, ?> that) {
        return null != that && (null == function ? null == that.function : function.equals(that.function));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "BinaryFunctionBinaryPredicate".hashCode();
        if (null != function) {
            hash ^= function.hashCode();
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "BinaryFunctionBinaryPredicate<" + function + ">";
    }

    /**
     * Adapt the given, possibly-<code>null</code>, {@link BinaryFunction
     * BinaryFunction} to the {@link BinaryPredicate BinaryPredicate} interface.
     * When the given <code>BinaryFunction</code> is <code>null</code>, returns
     * <code>null</code>.
     * 
     * @param <L> left type
     * @param <R> right type
     * @param <T> result type
     * @param function the possibly-<code>null</code> {@link BinaryFunction
     * BinaryFunction} to adapt
     * @return a <code>BinaryFunctionBinaryPredicate</code> wrapping the given
     * {@link BinaryFunction BinaryFunction}, or <code>null</code> if the given
     * <code>BinaryFunction</code> is <code>null</code>
     */
    public static <L, R, T> BinaryFunctionBinaryPredicate<L, R> adapt(
            BinaryFunction<? super L, ? super R, Boolean> function) {
        return null == function ? null : new BinaryFunctionBinaryPredicate<L, R>(function);
    }

}
