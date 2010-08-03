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

import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.UnaryFunction;

/**
 * Adapts a BinaryFunction as a UnaryFunction by sending the same argument to both sides of the BinaryFunction.
 * It sounds nonsensical, but using Composite functions, can be made to do something useful.
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class BinaryFunctionUnaryFunction<A, T> implements UnaryFunction<A, T> {
    private BinaryFunction<? super A, ? super A, ? extends T> function;

    /**``
     * Create a new BinaryFunctionUnaryFunction.
     * @param function to adapt
     */
    public BinaryFunctionUnaryFunction(BinaryFunction<? super A, ? super A, ? extends T> function) {
        if (null == function) {
            throw new IllegalArgumentException("BinaryFunction argument was null");
        }
        this.function = function;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public T evaluate(A obj) {
        return function.evaluate(obj, obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof BinaryFunctionUnaryFunction<?, ?>
                && equals((BinaryFunctionUnaryFunction<?, ?>) obj);
    }

    /**
     * Learn whether another BinaryFunctionUnaryFunction is equal to <code>this</code>.
     * @param that BinaryFunctionUnaryFunction to check
     * @return whether equal
     */
    public boolean equals(BinaryFunctionUnaryFunction<?, ?> that) {
        return that != null && that.function.equals(this.function);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return ("BinaryFunctionUnaryFunction".hashCode() << 2) | function.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "BinaryFunctionUnaryFunction<" + function + ">";
    }

    /**
     * Adapt a BinaryFunction as a UnaryFunction.
     * @param <A> input type
     * @param <T> result type
     * @param function to adapt
     * @return UnaryFunction<A, T>
     */
    public static <A, T> UnaryFunction<A, T> adapt(BinaryFunction<? super A, ? super A, ? extends T> function) {
        return null == function ? null : new BinaryFunctionUnaryFunction<A, T>(function);
    }

}
