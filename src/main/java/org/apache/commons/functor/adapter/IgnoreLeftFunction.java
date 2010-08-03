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
import org.apache.commons.functor.UnaryFunction;

/**
 * Adapts a
 * {@link UnaryFunction UnaryFunction}
 * to the
 * {@link BinaryFunction BinaryFunction} interface
 * by ignoring the first binary argument.
 * <p/>
 * Note that although this class implements
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if the
 * underlying functor is.  Attempts to serialize
 * an instance whose delegate is not
 * <code>Serializable</code> will result in an exception.
 *
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class IgnoreLeftFunction<L, R, T> implements BinaryFunction<L, R, T>, Serializable {
    /** The {@link UnaryFunction UnaryFunction} I'm wrapping. */
    private UnaryFunction<? super R, ? extends T> function = null;

    /**
     * Create a new IgnoreLeftFunction.
     * @param function UnaryFunction for right argument
     */
    public IgnoreLeftFunction(UnaryFunction<? super R, ? extends T> function) {
        if (function == null) {
            throw new IllegalArgumentException("UnaryFunction argument was null");
        }
        this.function = function;
    }

    /**
     * {@inheritDoc}
     */
    public T evaluate(L left, R right) {
        return function.evaluate(right);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof IgnoreLeftFunction<?, ?, ?>
                && equals((IgnoreLeftFunction<?, ?, ?>) that));
    }

    /**
     * Learn whether another IgnoreLeftFunction is equal to this.
     * @param that IgnoreLeftFunction to test
     * @return boolean
     */
    public boolean equals(IgnoreLeftFunction<?, ?, ?> that) {
        return null != that && (null == function ? null == that.function : function.equals(that.function));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "IgnoreLeftFunction".hashCode();
        if (null != function) {
            hash ^= function.hashCode();
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "IgnoreLeftFunction<" + function + ">";
    }

    /**
     * Adapt a UnaryFunction to the BinaryFunction interface.
     * @param <L> left type
     * @param <R> right type
     * @param <T> result type
     * @param function to adapt
     * @return IgnoreLeftFunction
     */
    public static <L, R, T> IgnoreLeftFunction<L, R, T> adapt(UnaryFunction<? super R, ? extends T> function) {
        return null == function ? null : new IgnoreLeftFunction<L, R, T>(function);
    }

}
