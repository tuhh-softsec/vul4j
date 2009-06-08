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
package org.apache.commons.functor.core.composite;

import java.io.Serializable;

import org.apache.commons.functor.UnaryFunction;

/**
 * A {@link UnaryFunction UnaryFunction}
 * representing the composition of
 * {@link UnaryFunction UnaryFunctions},
 * "chaining" the output of one to the input
 * of another.  For example,
 * <pre>new CompositeUnaryFunction(f).of(g)</code>
 * {@link #evaluate evaluates} to
 * <code>f.evaluate(g.evaluate(obj))</code>, and
 * <pre>new CompositeUnaryFunction(f).of(g).of(h)</pre>
 * {@link #evaluate evaluates} to
 * <code>f.evaluate(g.evaluate(h.evaluate(obj)))</code>.
 * <p>
 * When the collection is empty, this function is
 * an identity function.
 * </p>
 * <p>
 * Note that although this class implements
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if all the
 * underlying functors are.  Attempts to serialize
 * an instance whose delegates are not all
 * <code>Serializable</code> will result in an exception.
 * </p>
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 * @author Matt Benson
 */
public class CompositeUnaryFunction<A, T> implements UnaryFunction<A, T>, Serializable {

    /**
     * Encapsulates a double function evaluation.
     * @param <A> argument type
     * @param <X> intermediate type
     * @param <T> return type
     */
    private class Helper<X> implements UnaryFunction<A, T>, Serializable {
        private UnaryFunction<? super X, ? extends T> following;
        private UnaryFunction<? super A, ? extends X> preceding;

        /**
         * Create a new Helper.
         * @param following UnaryFunction<X, Y>
         * @param preceding UnaryFunction<Y, Z>
         */
        public Helper(UnaryFunction<? super X, ? extends T> following, UnaryFunction<? super A, ? extends X> preceding) {
            this.following = following;
            this.preceding = preceding;
        }

        /**
         * {@inheritDoc}
         */
        public T evaluate(A obj) {
            return following.evaluate(preceding.evaluate(obj));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            return obj == this || obj instanceof Helper && equals((Helper<?>) obj);
        }

        private boolean equals(Helper<?> helper) {
            return helper.following.equals(following) && helper.preceding.equals(preceding);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            int result = "CompositeUnaryFunction$Helper".hashCode();
            result <<= 2;
            result |= following.hashCode();
            result <<= 2;
            result |= preceding.hashCode();
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return following.toString() + " of " + preceding.toString();
        }
    }

    private UnaryFunction<? super A, ? extends T> function;

    /**
     * Create a new CompositeUnaryFunction.
     * @param function UnaryFunction to call
     */
    public CompositeUnaryFunction(UnaryFunction<? super A, ? extends T> function) {
        if (function == null) {
            throw new IllegalArgumentException("function must not be null");
        }
        this.function = function;
    }

    private <X> CompositeUnaryFunction(UnaryFunction<? super X, ? extends T> following, UnaryFunction<? super A, ? extends X> preceding) {
        this.function = new Helper<X>(following, preceding);
    }

    /**
     * {@inheritDoc}
     */
    public T evaluate(A obj) {
        return function.evaluate(obj);
    }

    /**
     * Fluently obtain a CompositeUnaryFunction that is "this function" applied to the specified preceding function.
     * @param <P> argument type of the resulting function.
     * @param preceding UnaryFunction
     * @return CompositeUnaryFunction<P, T>
     */
    public <P> CompositeUnaryFunction<P, T> of(UnaryFunction<? super P, ? extends A> preceding) {
        if (preceding == null) {
            throw new IllegalArgumentException("preceding function was null");
        }
        return new CompositeUnaryFunction<P, T>(function, preceding);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof CompositeUnaryFunction && equals((CompositeUnaryFunction<?, ?>) that));
    }

    /**
     * Learn whether another CompositeUnaryFunction is equal to this.
     * @param that CompositeUnaryFunction to test
     * @return boolean
     */
    public boolean equals(CompositeUnaryFunction<?, ?> that) {
        // by construction, list is never null
        return null != that && function.equals(that.function);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        // by construction, list is never null
        return ("CompositeUnaryFunction".hashCode() << 4) ^ function.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "CompositeUnaryFunction<" + function + ">";
    }

}
