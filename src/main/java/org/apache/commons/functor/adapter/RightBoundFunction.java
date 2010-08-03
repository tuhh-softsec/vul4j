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
 * {@link BinaryFunction BinaryFunction}
 * to the
 * {@link UnaryFunction UnaryFunction} interface
 * using a constant right-side argument.
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
public final class RightBoundFunction<L, R, T> implements UnaryFunction<L, T>, Serializable {
    /** The {@link BinaryFunction BinaryFunction} I'm wrapping. */
    private BinaryFunction<? super L, ? super R, ? extends T> function;
    /** The parameter to pass to that function. */
    private R param;

    /**
     * @param function the function to adapt
     * @param arg the constant argument to use
     */
    public RightBoundFunction(BinaryFunction<? super L, ? super R, ? extends T> function, R arg) {
        if (function == null) {
            throw new IllegalArgumentException("left-hand BinaryFunction argument was null");
        }
        this.function = function;
        this.param = arg;
    }

    /**
     * {@inheritDoc}
     */
    public T evaluate(L obj) {
        return function.evaluate(obj, param);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof RightBoundFunction<?, ?, ?> && equals((RightBoundFunction<?, ?, ?>) that));
    }

    /**
     * Learn whether another RightBoundFunction is equal to this.
     * @param that RightBoundFunction to test
     * @return boolean
     */
    public boolean equals(RightBoundFunction<?, ?, ?> that) {
        return null != that
                && (null == function ? null == that.function : function.equals(that.function))
                && (null == param ? null == that.param : param.equals(that.param));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "RightBoundFunction".hashCode();
        if (null != function) {
            hash <<= 2;
            hash ^= function.hashCode();
        }
        if (null != param) {
            hash <<= 2;
            hash ^= param.hashCode();
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "RightBoundFunction<" + function + "(?," + param + ")>";
    }

    /**
     * Adapt a BinaryFunction to the UnaryFunction interface.
     * @param function BinaryFunction to adapt
     * @param arg Object that will always be used for the right side of the BinaryFunction delegate.
     * @return RightBoundFunction
     */
    public static <L, R, T> RightBoundFunction<L, R, T> bind(BinaryFunction<? super L, ? super R, ? extends T> function, R arg) {
        return null == function ? null : new RightBoundFunction<L, R, T>(function, arg);
    }

}
