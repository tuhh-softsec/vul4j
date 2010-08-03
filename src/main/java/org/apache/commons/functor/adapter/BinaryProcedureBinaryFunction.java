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
import org.apache.commons.functor.BinaryProcedure;

/**
 * Adapts a
 * {@link BinaryProcedure BinaryProcedure}
 * to the
 * {@link BinaryFunction BinaryFunction} interface
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
public final class BinaryProcedureBinaryFunction<L, R, T> implements BinaryFunction<L, R, T>, Serializable {
    /** The {@link BinaryProcedure BinaryProcedure} I'm wrapping. */
    private BinaryProcedure<? super L, ? super R> procedure;

    /**
     * Create a new BinaryProcedureBinaryFunction.
     * @param procedure to adapt as a BinaryFunction
     */
    public BinaryProcedureBinaryFunction(BinaryProcedure<? super L, ? super R> procedure) {
        if (procedure == null) {
            throw new IllegalArgumentException("BinaryProcedure argument was null");
        }
        this.procedure = procedure;
    }

    /**
     * {@inheritDoc}
     */
    public T evaluate(L left, R right) {
        procedure.run(left, right);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this
                || (that instanceof BinaryProcedureBinaryFunction<?, ?, ?> && equals((BinaryProcedureBinaryFunction<?, ?, ?>) that));
    }

    /**
     * Learn whether another BinaryProcedureBinaryFunction is equal to this.
     * @param that the BinaryProcedureBinaryFunction to test
     * @return boolean
     */
    public boolean equals(BinaryProcedureBinaryFunction<?, ?, ?> that) {
        return null != that && (null == procedure ? null == that.procedure : procedure.equals(that.procedure));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "BinaryProcedureBinaryFunction".hashCode();
        if (null != procedure) {
            hash ^= procedure.hashCode();
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "BinaryProcedureBinaryFunction<" + procedure + ">";
    }

    /**
     * Adapt the given, possibly-<code>null</code>,
     * {@link BinaryProcedure BinaryProcedure} to the
     * {@link BinaryFunction BinaryFunction} interface.
     * When the given <code>BinaryProcedure</code> is <code>null</code>,
     * returns <code>null</code>.
     *
     * @param procedure the possibly-<code>null</code>
     *        {@link BinaryFunction BinaryFunction} to adapt
     * @return a <code>BinaryProcedureBinaryFunction</code> wrapping the given
     *         {@link BinaryFunction BinaryFunction}, or <code>null</code>
     *         if the given <code>BinaryFunction</code> is <code>null</code>
     */
    public static <L, R, T> BinaryProcedureBinaryFunction<L, R, T> adapt(BinaryProcedure<? super L, ? super R> procedure) {
        return null == procedure ? null : new BinaryProcedureBinaryFunction<L, R, T>(procedure);
    }

}
