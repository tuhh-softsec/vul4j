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
 * Adapts a
 * {@link UnaryProcedure UnaryProcedure}
 * to the
 * {@link UnaryFunction UnaryFunction} interface
 * by always returning <code>null</code>.
 * <p/>
 * Note that although this class implements
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if the
 * underlying procedure is.  Attempts to serialize
 * an instance whose delegate is not
 * <code>Serializable</code> will result in an exception.
 *
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class UnaryProcedureUnaryFunction<A, T> implements UnaryFunction<A, T>, Serializable {
    /** The {@link UnaryProcedure UnaryProcedure} I'm wrapping. */
    private UnaryProcedure<? super A> procedure;

    /**
     * Create a new UnaryProcedureUnaryFunction.
     * @param procedure to adapt
     */
    public UnaryProcedureUnaryFunction(UnaryProcedure<? super A> procedure) {
        if (procedure == null) {
            throw new IllegalArgumentException("UnaryProcedure argument was null");
        }
        this.procedure = procedure;
    }

    /**
     * {@inheritDoc}
     */
    public T evaluate(A obj) {
        procedure.run(obj);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this
                || (that instanceof UnaryProcedureUnaryFunction<?, ?> && equals((UnaryProcedureUnaryFunction<?, ?>) that));
    }

    /**
     * Learn whether a given UnaryProcedureUnaryFunction is equal to this.
     * @param that UnaryProcedureUnaryFunction to test
     * @return boolean
     */
    public boolean equals(UnaryProcedureUnaryFunction<?, ?> that) {
        return null != that && (null == procedure ? null == that.procedure : procedure.equals(that.procedure));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "UnaryProcedureUnaryFunction".hashCode();
        if (null != procedure) {
            hash ^= procedure.hashCode();
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "UnaryProcedureUnaryFunction<" + procedure + ">";
    }

    /**
     * Adapt a UnaryProcedure to the UnaryFunction interface.
     * @param procedure to adapt
     * @return UnaryProcedureUnaryFunction
     */
    public static <A, T> UnaryProcedureUnaryFunction<A, T> adapt(UnaryProcedure<? super A> procedure) {
        return null == procedure ? null : new UnaryProcedureUnaryFunction<A, T>(procedure);
    }

}
