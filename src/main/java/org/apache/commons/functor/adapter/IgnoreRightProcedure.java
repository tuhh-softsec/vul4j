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
 * {@link UnaryProcedure UnaryProcedure}
 * to the
 * {@link BinaryProcedure BinaryProcedure} interface
 * by ignoring the second binary argument.
 * <p/>
 * Note that although this class implements
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if the
 * underlying functor is.  Attempts to serialize
 * an instance whose delegate is not
 * <code>Serializable</code> will result in an exception.
 *
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class IgnoreRightProcedure<L, R> implements BinaryProcedure<L, R>, Serializable {
    /** The {@link UnaryProcedure UnaryProcedure} I'm wrapping. */
    private UnaryProcedure<? super L> procedure;

    /**
     * Create a new IgnoreRightProcedure.
     * @param procedure UnaryProcedure to adapt
     */
    public IgnoreRightProcedure(UnaryProcedure<? super L> procedure) {
        if (procedure == null) {
            throw new IllegalArgumentException("UnaryProcedure argument was null");
        }
        this.procedure = procedure;
    }

    /**
     * {@inheritDoc}
     */
    public void run(L left, R right) {
        procedure.run(left);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof IgnoreRightProcedure<?, ?> && equals((IgnoreRightProcedure<?, ?>) that));
    }

    /**
     * Learn whether another IgnoreRightProcedure is equal to this.
     * @param that IgnoreRightProcedure to test
     * @return boolean
     */
    public boolean equals(IgnoreRightProcedure<?, ?> that) {
        return null != that && (null == procedure ? null == that.procedure : procedure.equals(that.procedure));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "IgnoreRightProcedure".hashCode();
        if (null != procedure) {
            hash ^= procedure.hashCode();
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "IgnoreRightProcedure<" + procedure + ">";
    }

    /**
     * Adapt a UnaryProcedure to the BinaryProcedure interface.
     * @param <L> left type
     * @param <R> right type
     * @param procedure UnaryProcedure to adapt
     * @return IgnoreRightProcedure
     */
    public static <L, R> IgnoreRightProcedure<L, R> adapt(UnaryProcedure<? super L> procedure) {
        return null == procedure ? null : new IgnoreRightProcedure<L, R>(procedure);
    }

}
