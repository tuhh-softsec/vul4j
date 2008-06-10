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

import org.apache.commons.functor.Function;
import org.apache.commons.functor.UnaryFunction;

/**
 * Adapts a
 * {@link UnaryFunction UnaryFunction}
 * to the
 * {@link Function Function} interface
 * using a constant unary argument.
 * <p/>
 * Note that although this class implements
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if the
 * underlying objects are.  Attempts to serialize
 * an instance whose delegates are not
 * <code>Serializable</code> will result in an exception.
 *
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class BoundFunction<T> implements Function<T>, Serializable {
    private class Helper<A> implements Function<T>, Serializable {
        /** The {@link UnaryFunction UnaryFunction} I'm wrapping. */
        private UnaryFunction<? super A, ? extends T> function;

        /** The parameter to pass to that function. */
        private A arg = null;

        private Helper(UnaryFunction<? super A, ? extends T> function, A arg) {
            this.function = function;
            this.arg = arg;
        }

        /**
         * {@inheritDoc}
         */
        public T evaluate() {
            return function.evaluate(arg);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            return obj == this || obj instanceof Helper && equals((Helper<?>) obj);
        }

        private boolean equals(Helper<?> that) {
            if (that != null && that.function.equals(this.function)) {
                return that.arg == this.arg || that.arg != null && that.arg.equals(this.arg);
            }
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            int result = "BoundFunction$Helper".hashCode();
            result <<= 2;
            result |= function.hashCode();
            result <<= 2;
            return arg == null ? result : result | arg.hashCode();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return function.toString() + "(" + arg + ")";
        }
    }

    private Helper<?> helper;

    /**
     * Create a new BoundFunction.
     * @param function the function to adapt
     * @param arg the constant argument to use
     */
    public <A> BoundFunction(UnaryFunction<? super A, ? extends T> function, A arg) {
        if (function == null) {
            throw new IllegalArgumentException("UnaryFunction argument was null");
        }
        this.helper = new Helper<A>(function, arg);
    }

    /**
     * {@inheritDoc}
     */
    public T evaluate() {
        return helper.evaluate();
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof BoundFunction && equals((BoundFunction<?>) that));
    }

    /**
     * Learn whether another BoundFunction is equal to this.
     * @param that BoundFunction to test
     * @return boolean
     */
    public boolean equals(BoundFunction<?> that) {
        return null != that && that.helper.equals(this.helper);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return "BoundFunction".hashCode() << 8 | helper.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "BoundFunction<" + helper + ">";
    }

    /**
     * Adapt the given, possibly-<code>null</code>,
     * {@link UnaryFunction UnaryFunction} to the
     * {@link Function Function} interface by binding
     * the specified <code>Object</code> as a constant
     * argument.
     * When the given <code>UnaryFunction</code> is <code>null</code>,
     * returns <code>null</code>.
     * @param <A>
     * @param <T>
     * @param function the possibly-<code>null</code>
     *        {@link UnaryFunction UnaryFunction} to adapt
     * @param arg the object to bind as a constant argument
     * @return a <code>BoundFunction</code> wrapping the given
     *         {@link UnaryFunction UnaryFunction}, or <code>null</code>
     *         if the given <code>UnaryFunction</code> is <code>null</code>
     */
    public static <A, T> BoundFunction<T> bind(UnaryFunction<? super A, ? extends T> function, A arg) {
        return null == function ? null : new BoundFunction<T>(function, arg);
    }

}
