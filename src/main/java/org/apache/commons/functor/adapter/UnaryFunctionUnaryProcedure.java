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
import org.apache.commons.functor.UnaryProcedure;

/**
 * Adapts a {@link UnaryFunction UnaryFunction}
 * to the {@link UnaryProcedure UnaryProcedure}
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
public final class UnaryFunctionUnaryProcedure<A> implements UnaryProcedure<A>, Serializable {

    /** The {@link UnaryFunction UnaryFunction} I'm wrapping. */
    private UnaryFunction<? super A, ?> function;

    /**
     * Create an {@link UnaryProcedure UnaryProcedure} wrapping
     * the given {@link UnaryFunction UnaryFunction}.
     * @param function the {@link UnaryFunction UnaryFunction} to wrap
     */
    public UnaryFunctionUnaryProcedure(UnaryFunction<? super A, ?> function) {
        if (function == null) {
            throw new IllegalArgumentException("UnaryFunction argument was null");
        }
        this.function = function;
    }

    /**
     * {@link UnaryFunction#evaluate Evaluate} my function, but
     * ignore its returned value.
     * {@inheritDoc}
     */
    public void run(A obj) {
        function.evaluate(obj);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this
                || (that instanceof UnaryFunctionUnaryProcedure<?> && equals((UnaryFunctionUnaryProcedure<?>) that));
    }

    /**
     * Learn whether a specified UnaryFunctionUnaryPredicate is equal to this.
     * @param that the UnaryFunctionUnaryPredicate to test
     * @return boolean
     */
    public boolean equals(UnaryFunctionUnaryProcedure<?> that) {
        return null != that && (null == function ? null == that.function : function.equals(that.function));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "UnaryFunctionUnaryProcedure".hashCode();
        if (null != function) {
            hash ^= function.hashCode();
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "UnaryFunctionUnaryProcedure<" + function + ">";
    }

    /**
     * Adapt the given, possibly-<code>null</code>,
     * {@link UnaryFunction UnaryFunction} to the
     * {@link UnaryProcedure UnaryProcedure} interface.
     * When the given <code>UnaryFunction</code> is <code>null</code>,
     * returns <code>null</code>.
     *
     * @param function the possibly-<code>null</code>
     *        {@link UnaryFunction UnaryFunction} to adapt
     * @return a {@link UnaryProcedure UnaryProcedure} wrapping the given
     *         {@link UnaryFunction UnaryFunction}, or <code>null</code>
     *         if the given <code>UnaryFunction</code> is <code>null</code>
     */
    public static <A> UnaryFunctionUnaryProcedure<A> adapt(UnaryFunction<? super A, ?> function) {
        return null == function ? null : new UnaryFunctionUnaryProcedure<A>(function);
    }

}
