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
import org.apache.commons.functor.BinaryProcedure;

/**
 * Adapts a {@link BinaryFunction BinaryFunction}
 * to the {@link BinaryProcedure BinaryProcedure}
 * interface by ignoring the value returned
 * by the function.
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
public final class BinaryFunctionBinaryProcedure<L, R> implements BinaryProcedure<L, R>, Serializable {

    /** The {@link BinaryFunction BinaryFunction} I'm wrapping. */
    private BinaryFunction<? super L, ? super R, ?> function;

    /**
     * Create an {@link BinaryProcedure BinaryProcedure} wrapping
     * the given {@link BinaryFunction BinaryFunction}.
     * @param function the {@link BinaryFunction BinaryFunction} to wrap
     */
    public BinaryFunctionBinaryProcedure(BinaryFunction<? super L, ? super R, ?> function) {
        this.function = function;
    }

    /**
     * {@link BinaryFunction#evaluate Evaluate} my function, but
     * ignore its returned value.
     * {@inheritDoc}
     */
    public void run(L left, R right) {
        function.evaluate(left, right);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this
                || (that instanceof BinaryFunctionBinaryProcedure<?, ?>
                && equals((BinaryFunctionBinaryProcedure<?, ?>) that));
    }

    /**
     * Learn whether a given BinaryFunctionBinaryPredicate is equal to this.
     * @param that BinaryFunctionBinaryPredicate to compare
     * @return boolean
     */
    public boolean equals(BinaryFunctionBinaryProcedure<?, ?> that) {
        return null != that && (null == function ? null == that.function : function.equals(that.function));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "BinaryFunctionBinaryProcedure".hashCode();
        if (null != function) {
            hash ^= function.hashCode();
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "BinaryFunctionBinaryProcedure<" + function + ">";
    }

    /**
     * Adapt the given, possibly-<code>null</code>,
     * {@link BinaryFunction BinaryFunction} to the
     * {@link BinaryProcedure BinaryProcedure} interface.
     * When the given <code>BinaryFunction</code> is <code>null</code>,
     * returns <code>null</code>.
     *
     * @param <L> left type
     * @param <R> right type
     * @param function the possibly-<code>null</code>
     *        {@link BinaryFunction BinaryFunction} to adapt
     * @return a <code>BinaryFunctionBinaryProcedure</code> wrapping the given
     *         {@link BinaryFunction BinaryFunction}, or <code>null</code>
     *         if the given <code>BinaryFunction</code> is <code>null</code>
     */
    public static <L, R> BinaryFunctionBinaryProcedure<L, R> adapt(BinaryFunction<? super L, ? super R, ?> function) {
        return null == function ? null : new BinaryFunctionBinaryProcedure<L, R>(function);
    }

}
