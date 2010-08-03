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

import org.apache.commons.functor.BinaryProcedure;

/**
 * Transposes (swaps) the arguments to some other
 * {@link BinaryProcedure procedure}.
 * For example, given a procedure <i>p</i>
 * and the ordered pair of arguments <i>a</i>,
 * <i>b</i>.
 * {@link #run runs}
 * <code>p.run(b,a)</code>.
 * <p>
 * Note that although this class implements
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if the
 * underlying functor is.  Attempts to serialize
 * an instance whose delegate is not
 * <code>Serializable</code> will result in an exception.
 * </p>
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TransposedProcedure<L, R> implements BinaryProcedure<L, R>, Serializable {
    // attributes
    // ------------------------------------------------------------------------
    private BinaryProcedure<? super R, ? super L> procedure = null;

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new TransposedProcedure.
     * @param procedure BinaryProcedure to transpose
     */
    public TransposedProcedure(BinaryProcedure<? super R, ? super L> procedure) {
        if (procedure == null) {
            throw new IllegalArgumentException("BinaryProcedure argument was null");
        }
        this.procedure = procedure;
    }

    // functor interface
    // ------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public void run(L left, R right) {
        procedure.run(right, left);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof TransposedProcedure<?, ?> && equals((TransposedProcedure<?, ?>) that));
    }

    /**
     * Learn whether another TransposedProcedure is equal to this.
     * @param that TransposedPredicate to test
     * @return boolean
     */
    public boolean equals(TransposedProcedure<?, ?> that) {
        return null != that && (null == procedure ? null == that.procedure : procedure.equals(that.procedure));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "TransposedProcedure".hashCode();
        if (null != procedure) {
            hash ^= procedure.hashCode();
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "TransposedProcedure<" + procedure + ">";
    }

    // static
    // ------------------------------------------------------------------------
    /**
     * Transpose a BinaryProcedure.
     * @param p to transpose
     * @return TransposedProcedure
     */
    public static <L, R> TransposedProcedure<R, L> transpose(BinaryProcedure<? super L, ? super R> p) {
        return null == p ? null : new TransposedProcedure<R, L>(p);
    }

}
