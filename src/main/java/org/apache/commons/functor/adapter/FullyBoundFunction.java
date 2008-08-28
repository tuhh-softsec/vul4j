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
import org.apache.commons.functor.Function;

/**
 * Adapts a
 * {@link BinaryFunction BinaryFunction}
 * to the
 * {@link Function Function} interface
 * using constant arguments.
 * <p/>
 * Note that although this class implements
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if the
 * underlying objects are.  Attempts to serialize
 * an instance whose delegates are not
 * <code>Serializable</code> will result in an exception.
 *
 * @version $Revision$ $Date$
 * @author Matt Benson
 */
public final class FullyBoundFunction<L, R, T> implements Function<T>, Serializable {
    /** The {@link BinaryFunction BinaryFunction} I'm wrapping. */
    private BinaryFunction<? super L, ? super R, ? extends T> function;
    /** The left parameter to pass to that function. */
    private L left;
    /** The right parameter to pass to that function. */
    private R right;

    /**
     * Create a new FullyBoundFunction.
     * @param function the function to adapt
     * @param left the left side argument to use
     * @param right the right side argument to use
     */
    public FullyBoundFunction(BinaryFunction<? super L, ? super R, ? extends T> function, L left, R right) {
        if (function == null) {
            throw new IllegalArgumentException("BinaryFunction argument was null");
        }
        this.function = function;
        this.left = left;
        this.right = right;
    }

    /**
     * {@inheritDoc}
     */
    public T evaluate() {
        return function.evaluate(left, right);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof FullyBoundFunction && equals((FullyBoundFunction<?, ?, ?>) that));
    }

    /**
     * Learn whether another FullyBoundFunction is equal to this.
     * @param that FullyBoundFunction to test
     * @return boolean
     */
    public boolean equals(FullyBoundFunction<?, ?, ?> that) {
        return null != that && (null == function ? null == that.function : function.equals(that.function))
                && (null == left ? null == that.left : left.equals(that.left))
                && (null == right ? null == that.right : right.equals(that.right));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "FullyBoundFunction".hashCode();
        if (null != function) {
            hash <<= 2;
            hash ^= function.hashCode();
        }
        hash <<= 2;
        if (null != left) {
            hash ^= left.hashCode();
        }
        hash <<= 2;
        if (null != right) {
            hash ^= right.hashCode();
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "FullyBoundFunction<" + function + "(" + left + ", " + right + ")>";
    }

    /**
     * Adapt a BinaryFunction as a Function.
     * @param function to adapt
     * @param left left side argument
     * @param right right side argument
     * @return FullyBoundFunction
     */
    public static <L, R, T> FullyBoundFunction<L, R, T> bind(
            BinaryFunction<? super L, ? super R, ? extends T> function, L left, R right) {
        return null == function ? null : new FullyBoundFunction<L, R, T>(function, left, right);
    }

}
