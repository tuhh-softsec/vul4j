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

import org.apache.commons.functor.Function;
import org.apache.commons.functor.Procedure;
import org.apache.commons.functor.UnaryProcedure;

/**
 * A Procedure composed of a Function whose result is then run through a UnaryProcedure.
 * @version $Revision$ $Date$
 * @author Matt Benson
 */
public class TransformedProcedure implements Procedure, Serializable {
    /**
     * Type-remembering helper
     * @param <X>
     */
    private class Helper<X> implements Procedure, Serializable {
        private Function<? extends X> function;
        private UnaryProcedure<? super X> procedure;

        /**
         * Create a new Helper.
         * @param function Function
         * @param procedure UnaryFunction
         */
        private Helper(Function<? extends X> function, UnaryProcedure<? super X> procedure) {
            this.function = function;
            this.procedure = procedure;
        }

        /**
         * {@inheritDoc}
         */
        public void run() {
            procedure.run(function.evaluate());
        }
    }

    private Helper<?> helper;

    /**
     * Create a new TransformedProcedure.
     * @param <X>
     * @param function Function
     * @param procedure UnaryProcedure
     */
    public <X> TransformedProcedure(Function<? extends X> function, UnaryProcedure<? super X> procedure) {
        this.helper = new Helper<X>(function, procedure);
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        helper.run();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof TransformedProcedure
                && equals((TransformedProcedure) obj);
    }

    /**
     * Learn whether another TransformedProcedure is equal to <code>this</code>.
     * @param that instance to test
     * @return whether equal
     */
    public boolean equals(TransformedProcedure that) {
        return that != null && that.helper.function.equals(this.helper.function)
                && that.helper.procedure.equals(this.helper.procedure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = "TransformedProcedure".hashCode();
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
        return "TransformedProcedure<" + helper.function + "; " + helper.procedure + ">";
    }
}
