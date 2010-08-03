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

import org.apache.commons.functor.Procedure;
import org.apache.commons.functor.UnaryProcedure;

/**
 * Adapts a
 * {@link UnaryProcedure UnaryProcedure}
 * to the
 * {@link Procedure Procedure} interface
 * using a constant unary argument.
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
public final class BoundProcedure implements Procedure, Serializable {
    /** The {@link UnaryProcedure UnaryProcedure} I'm wrapping. */
    private UnaryProcedure<Object> procedure;
    /** The parameter to pass to that procedure. */
    private Object param;

    /**
     * Create a new BoundProcedure instance.
     * @param <A> arg type
     * @param procedure the procedure to adapt
     * @param arg the constant argument to use
     */
    @SuppressWarnings("unchecked")
    public <A> BoundProcedure(UnaryProcedure<? super A> procedure, A arg) {
        if (procedure == null) {
            throw new IllegalArgumentException("UnaryProcedure argument was null");
        }
        this.procedure = (UnaryProcedure<Object>) procedure;
        this.param = arg;
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        procedure.run(param);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof BoundProcedure && equals((BoundProcedure) that));
    }

    /**
     * Learn whether a given BoundProcedure is equal to this.
     * @param that the BoundProcedure to test
     * @return boolean
     */
    public boolean equals(BoundProcedure that) {
        return null != that
                && (null == procedure ? null == that.procedure : procedure.equals(that.procedure))
                && (null == param ? null == that.param : param.equals(that.param));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "BoundProcedure".hashCode();
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
        return "BoundProcedure<" + procedure + "(" + param + ")>";
    }

    /**
     * Adapt the given, possibly-<code>null</code>,
     * {@link UnaryProcedure UnaryProcedure} to the
     * {@link Procedure Procedure} interface by binding
     * the specified <code>Object</code> as a constant
     * argument.
     * When the given <code>UnaryProcedure</code> is <code>null</code>,
     * returns <code>null</code>.
     *
     * @param <A> arg type
     * @param procedure the possibly-<code>null</code>
     *        {@link UnaryProcedure UnaryProcedure} to adapt
     * @param arg the object to bind as a constant argument
     * @return a <code>BoundProcedure</code> wrapping the given
     *         {@link UnaryProcedure UnaryProcedure}, or <code>null</code>
     *         if the given <code>UnaryProcedure</code> is <code>null</code>
     */
    public static <A> BoundProcedure bind(UnaryProcedure<? super A> procedure, A arg) {
        return null == procedure ? null : new BoundProcedure(procedure, arg);
    }

}
