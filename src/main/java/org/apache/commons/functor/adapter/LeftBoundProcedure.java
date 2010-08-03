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

import org.apache.commons.functor.BinaryProcedure;
import org.apache.commons.functor.UnaryProcedure;

/**
 * Adapts a
 * {@link BinaryProcedure BinaryProcedure}
 * to the
 * {@link UnaryProcedure UnaryProcedure} interface
 * using a constant left-side argument.
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
public final class LeftBoundProcedure<A> implements UnaryProcedure<A>, Serializable {
    /** The {@link BinaryProcedure BinaryProcedure} I'm wrapping. */
    private BinaryProcedure<Object, ? super A> procedure;
    /** The parameter to pass to that procedure. */
    private Object param;

    /**
     * Create a new LeftBoundProcedure.
     * @param procedure the procedure to adapt
     * @param arg the constant argument to use
     */
    @SuppressWarnings("unchecked")
    public <L> LeftBoundProcedure(BinaryProcedure<? super L, ? super A> procedure, L arg) {
        if (procedure == null) {
            throw new IllegalArgumentException("BinaryProcedure argument was null");
        }
        this.procedure = (BinaryProcedure<Object, ? super A>) procedure;
        this.param = arg;
    }

    /**
     * {@inheritDoc}
     */
    public void run(A obj) {
        procedure.run(param, obj);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof LeftBoundProcedure<?> && equals((LeftBoundProcedure<?>) that));
    }

    /**
     * Learn whether another LeftBoundProcedure is equal to this.
     * @param that LeftBoundProcedure to test
     * @return boolean
     */
    public boolean equals(LeftBoundProcedure<?> that) {
        return null != that
                && (null == procedure ? null == that.procedure : procedure.equals(that.procedure))
                && (null == param ? null == that.param : param.equals(that.param));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "LeftBoundProcedure".hashCode();
        if (null != procedure) {
            hash <<= 2;
            hash ^= procedure.hashCode();
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
        return "LeftBoundProcedure<" + procedure + "(" + param + ",?)>";
    }

    /**
     * Get a UnaryProcedure from <code>procedure</code>.
     * @param <L> left type
     * @param <R> right type
     * @param procedure to adapt
     * @param arg left side argument
     * @return LeftBoundProcedure<R>
     */
    public static <L, R> LeftBoundProcedure<R> bind(BinaryProcedure<? super L, ? super R> procedure, L arg) {
        return null == procedure ? null : new LeftBoundProcedure<R>(procedure, arg);
    }

}
