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
import org.apache.commons.functor.Procedure;

/**
 * Adapts a
 * {@link BinaryProcedure BinaryProcedure}
 * to the
 * {@link Procedure Procedure} interface
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
 * @author Matt Benson
 */
public final class FullyBoundProcedure<L, R> implements Procedure, Serializable {
    /** The {@link BinaryProcedure BinaryProcedure} I'm wrapping. */
    private BinaryProcedure<? super L, ? super R> procedure;
    /** The left parameter to pass to that procedure. */
    private L left;
    /** The right parameter to pass to that procedure. */
    private R right;

    /**
     * Create a new FullyBoundProcedure.
     * @param procedure the procedure to adapt
     * @param left the left argument to use
     * @param right the right argument to use
     */
    public FullyBoundProcedure(BinaryProcedure<? super L, ? super R> procedure, L left, R right) {
        if (procedure == null) {
            throw new IllegalArgumentException("BinaryProcedure argument was null");
        }
        this.procedure = procedure;
        this.left = left;
        this.right = right;
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        procedure.run(left, right);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof FullyBoundProcedure && equals((FullyBoundProcedure<?, ?>) that));
    }

    /**
     * Learn whether another FullyBoundProcedure is equal to this.
     * @param that FullyBoundProcedure to test
     * @return boolean
     */
    public boolean equals(FullyBoundProcedure<?, ?> that) {
        return null != that && (null == procedure ? null == that.procedure : procedure.equals(that.procedure))
                && (null == left ? null == that.left : left.equals(that.left))
                && (null == right ? null == that.right : right.equals(that.right));
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
        return "FullyBoundProcedure<" + procedure + "(" + left + ", " + right + ")>";
    }

    /**
     * Adapt a BinaryProcedure to the Procedure interface.
     * @param procedure to adapt
     * @param left left side argument
     * @param right right side argument
     * @return FullyBoundProcedure
     */
    public static <L, R> FullyBoundProcedure<L, R> bind(BinaryProcedure<? super L, ? super R> procedure, L left, R right) {
        return null == procedure ? null : new FullyBoundProcedure<L, R>(procedure, left, right);
    }

}
