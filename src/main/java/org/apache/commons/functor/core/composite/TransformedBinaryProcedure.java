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
import org.apache.commons.functor.BinaryProcedure;
import org.apache.commons.functor.UnaryProcedure;

/**
 * A BinaryProcedure composed of a BinaryFunction whose result is then run through a UnaryProcedure.
 * @version $Revision$ $Date$
 * @author Matt Benson
 */
public class TransformedBinaryProcedure<L, R> implements BinaryProcedure<L, R>, Serializable {
    /**
     * Type-remembering helper
     * @param <X>
     */
    private class Helper<X> implements BinaryProcedure<L, R>, Serializable {
        private BinaryFunction<? super L, ? super R, ? extends X> function;
        private UnaryProcedure<? super X> procedure;

        /**
         * Create a new Helper.
         * @param function BinaryFunction
         * @param procedure UnaryFunction
         */
        private Helper(BinaryFunction<? super L, ? super R, ? extends X> function, UnaryProcedure<? super X> procedure) {
            this.function = function;
            this.procedure = procedure;
        }

        /**
         * {@inheritDoc}
         */
        public void run(L left, R right) {
            procedure.run(function.evaluate(left, right));
        }
    }

    private Helper<?> helper;

    /**
     * Create a new TransformedBinaryProcedure.
     * @param <X>
     * @param function BinaryFunction
     * @param procedure UnaryProcedure
     */
    public <X> TransformedBinaryProcedure(BinaryFunction<? super L, ? super R, ? extends X> function,
            UnaryProcedure<? super X> procedure) {
        this.helper = new Helper<X>(function, procedure);
    }

    /**
     * {@inheritDoc}
     */
    public void run(L left, R right) {
        helper.run(left, right);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof TransformedBinaryProcedure<?, ?>
                && equals((TransformedBinaryProcedure<?, ?>) obj);
    }

    /**
     * Learn whether another TransformedBinaryProcedure is equal to <code>this</code>.
     * @param that instance to test
     * @return whether equal
     */
    public boolean equals(TransformedBinaryProcedure<?, ?> that) {
        return that != null && that.helper.function.equals(this.helper.function)
                && that.helper.procedure.equals(this.helper.procedure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = "TransformedBinaryProcedure".hashCode();
        result <<= 2;
        result |= helper.procedure.hashCode();
        result <<= 2;
        result |= helper.function.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "TransformedBinaryProcedure<" + helper.function + "; " + helper.procedure + ">";
    }
}
