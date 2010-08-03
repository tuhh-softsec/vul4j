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

import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.UnaryFunction;

/**
 * A BinaryFunction whose result is then run through a UnaryFunction.
 * @version $Revision$ $Date$
 * @author Matt Benson
 */
public class TransformedBinaryFunction<L, R, T> implements BinaryFunction<L, R, T>, Serializable {
    /**
     * Type-remembering helper
     * @param <X>
     */
    private class Helper<X> implements BinaryFunction<L, R, T>, Serializable {
        private BinaryFunction<? super L, ? super R, ? extends X> preceding;
        private UnaryFunction<? super X, ? extends T> following;

        /**
         * Create a new Helper.
         * @param preceding BinaryFunction
         * @param following UnaryFunction
         */
        private Helper(BinaryFunction<? super L, ? super R, ? extends X> preceding, UnaryFunction<? super X, ? extends T> following) {
            this.preceding = preceding;
            this.following = following;
        }

        /**
         * {@inheritDoc}
         */
        public T evaluate(L left, R right) {
            return following.evaluate(preceding.evaluate(left, right));
        }
    }

    private Helper<?> helper;

    /**
     * Create a new TransformedBinaryFunction.
     * @param <X>
     * @param preceding BinaryFunction
     * @param following UnaryFunction
     */
    public <X> TransformedBinaryFunction(BinaryFunction<? super L, ? super R, ? extends X> preceding,
            UnaryFunction<? super X, ? extends T> following) {
        this.helper = new Helper<X>(preceding, following);
    }

    /**
     * {@inheritDoc}
     */
    public T evaluate(L left, R right) {
        return helper.evaluate(left, right);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof TransformedBinaryFunction<?, ?, ?>
                && equals((TransformedBinaryFunction<?, ?, ?>) obj);
    }

    /**
     * Learn whether another TransformedBinaryFunction is equal to <code>this</code>.
     * @param that instance to test
     * @return whether equal
     */
    public boolean equals(TransformedBinaryFunction<?, ?, ?> that) {
        return that != null && that.helper.preceding.equals(this.helper.preceding)
                && that.helper.following.equals(this.helper.following);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = "TransformedBinaryFunction".hashCode();
        result <<= 2;
        result |= helper.following.hashCode();
        result <<= 2;
        result |= helper.preceding.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "TransformedBinaryFunction<" + helper.preceding + "; " + helper.following + ">";
    }
}
