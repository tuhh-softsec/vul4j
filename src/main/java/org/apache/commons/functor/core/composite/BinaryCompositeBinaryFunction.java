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

/**
 * A {@link BinaryFunction BinaryFunction} composed of
 * three binary functions, <i>f</i>, <i>g</i> and <i>h</i>,
 * evaluating the ordered parameters <i>x</i>, <i>y</i>
 * to <code><i>f</i>(<i>g</i>(<i>x</i>,<i>y</i>),<i>h</i>(<i>x</i>,<i>y</i>))</code>.
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
 */
public class BinaryCompositeBinaryFunction<L, R, T> implements BinaryFunction<L, R, T>, Serializable {

    /**
     * Type-remembering Helper
     *
     * @param <G>
     * @param <H>
     */
    private class Helper<G, H> implements BinaryFunction<L, R, T>, Serializable {
        private BinaryFunction<? super G, ? super H, ? extends T> f;
        private BinaryFunction<? super L, ? super R, ? extends G> g;
        private BinaryFunction<? super L, ? super R, ? extends H> h;

        /**
         * Create a new Helper.
         * @param f final BinaryFunction to evaluate
         * @param g left preceding BinaryFunction
         * @param h right preceding BinaryFunction
         */
        public Helper(BinaryFunction<? super G, ? super H, ? extends T> f,
                BinaryFunction<? super L, ? super R, ? extends G> g, BinaryFunction<? super L, ? super R, ? extends H> h) {
            this.f = f;
            this.g = g;
            this.h = h;
        }

        /**
         * {@inheritDoc}
         */
        public T evaluate(L left, R right) {
            return f.evaluate(g.evaluate(left, right), h.evaluate(left, right));
        }
    }

    private Helper<?, ?> helper;

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new BinaryCompositeBinaryFunction.
     * @param f final BinaryFunction to evaluate
     * @param g left preceding BinaryFunction
     * @param h right preceding BinaryFunction
     */
    public <G, H> BinaryCompositeBinaryFunction(BinaryFunction<? super G, ? super H, ? extends T> f,
            BinaryFunction<? super L, ? super R, ? extends G> g, BinaryFunction<? super L, ? super R, ? extends H> h) {
        if (f == null || g == null || h == null) {
            throw new IllegalArgumentException("BinaryFunction arguments may not be null");
        }
        this.helper = new Helper<G, H>(f, g, h);
    }

    // function interface
    // ------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public T evaluate(L left, R right) {
        return helper.evaluate(left, right);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this
                || (that instanceof BinaryCompositeBinaryFunction<?, ?, ?> && equals((BinaryCompositeBinaryFunction<?, ?, ?>) that));
    }

    /**
     * Learn whether another BinaryCompositeBinaryFunction is equal to this.
     * @param that BinaryCompositeBinaryFunction to test
     * @return boolean
     */
    public boolean equals(BinaryCompositeBinaryFunction<?, ?, ?> that) {
        return null != that
                && helper.f.equals(that.helper.f)
                && helper.g.equals(that.helper.g)
                && helper.h.equals(that.helper.h);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "BinaryCompositeBinaryFunction".hashCode();
            hash <<= 4;
            hash ^= helper.f.hashCode();
            hash <<= 4;
            hash ^= helper.g.hashCode();
            hash <<= 4;
            hash ^= helper.h.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "BinaryCompositeBinaryFunction<" + helper.f + ";" + helper.g + ";" + helper.h + ">";
    }

}
