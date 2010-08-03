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

import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.UnaryPredicate;

/**
 * Adapts a <code>Boolean</code>-valued
 * {@link UnaryFunction UnaryFunction}
 * to the {@link UnaryPredicate UnaryPredicate}
 * interface.
 * <p/>
 * Note that although this class implements
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if the
 * underlying function is.  Attempts to serialize
 * an instance whose delegate is not
 * <code>Serializable</code> will result in an exception.
 *
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class UnaryFunctionUnaryPredicate<A> implements UnaryPredicate<A>, Serializable {
    /** The {@link UnaryFunction UnaryFunction} I'm wrapping. */
    private UnaryFunction<? super A, Boolean> function;

    /**
     * Create an {@link UnaryPredicate UnaryPredicate} wrapping
     * the given {@link UnaryFunction UnaryFunction}.
     * @param function the {@link UnaryFunction UnaryFunction} to wrap
     */
    public UnaryFunctionUnaryPredicate(UnaryFunction<? super A, Boolean> function) {
        if (function == null) {
            throw new IllegalArgumentException("UnaryFunction argument was null");
        }
        this.function = function;
    }

    /**
     * {@inheritDoc}
     * Returns the <code>boolean</code> value of the non-<code>null</code>
     * <code>Boolean</code> returned by the {@link UnaryFunction#evaluate evaluate}
     * method of my underlying function.
     *
     * @throws NullPointerException if my underlying function returns <code>null</code>
     * @throws ClassCastException if my underlying function returns a non-<code>Boolean</code>
     */
    public boolean test(A obj) {
        return function.evaluate(obj);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this
                || (that instanceof UnaryFunctionUnaryPredicate<?> && equals((UnaryFunctionUnaryPredicate<?>) that));
    }

    /**
     * Learn whether another UnaryFunctionUnaryPredicate is equal to this.
     * @param that UnaryFunctionUnaryPredicate to test
     * @return boolean
     */
    public boolean equals(UnaryFunctionUnaryPredicate<?> that) {
        return null != that && (null == function ? null == that.function : function.equals(that.function));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "UnaryFunctionUnaryPredicate".hashCode();
        if (null != function) {
            hash ^= function.hashCode();
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "UnaryFunctionUnaryPredicate<" + function + ">";
    }

    /**
     * 
     * Adapt the given, possibly-<code>null</code>,
     * {@link UnaryFunction UnaryFunction} to the
     * {@link UnaryPredicate UnaryPredicate} interface.
     * When the given <code>UnaryFunction</code> is <code>null</code>,
     * returns <code>null</code>.
     *
     * @param <A>
     * @param function the possibly-<code>null</code>
     *        {@link UnaryFunction UnaryFunction} to adapt
     * @return a {@link UnaryPredicate UnaryPredicate} wrapping the given
     *         {@link UnaryFunction UnaryFunction}, or <code>null</code>
     *         if the given <code>UnaryFunction</code> is <code>null</code>
     */
    public static <A> UnaryFunctionUnaryPredicate<A> adapt(UnaryFunction<? super A, Boolean> function) {
        return null == function ? null : new UnaryFunctionUnaryPredicate<A>(function);
    }

}
