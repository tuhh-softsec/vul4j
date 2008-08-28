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

import org.apache.commons.functor.BinaryProcedure;
import org.apache.commons.functor.UnaryProcedure;

/**
 * Adapts a BinaryProcedure as a UnaryProcedure by sending the same argument to both sides of the BinaryProcedure.
 * @version $Revision$ $Date$
 * @author Matt Benson
 */
public final class BinaryProcedureUnaryProcedure<A> implements UnaryProcedure<A> {
    private BinaryProcedure<? super A, ? super A> procedure;

    /**
     * Create a new BinaryProcedureUnaryProcedure.
     * @param procedure to adapt
     */
    public BinaryProcedureUnaryProcedure(BinaryProcedure<? super A, ? super A> procedure) {
        if (null == procedure) {
            throw new IllegalArgumentException("BinaryProcedure argument was null");
        }
        this.procedure = procedure;
    }

    /**
     * {@inheritDoc}
     */
    public void run(A obj) {
        procedure.run(obj, obj);
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof BinaryProcedureUnaryProcedure
                && equals((BinaryProcedureUnaryProcedure<?>) obj);
    }

    /**
     * Learn whether another BinaryProcedureUnaryProcedure is equal to
     * <code>this</code>.
     * 
     * @param that BinaryProcedureUnaryProcedure to check
     * 
     * @return whether equal
     */
    public boolean equals(BinaryProcedureUnaryProcedure<?> that) {
        return that != null && that.procedure.equals(this.procedure);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return ("BinaryProcedureUnaryProcedure".hashCode() << 2) | procedure.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "BinaryProcedureUnaryProcedure<" + procedure + ">";
    }

    /**
     * Adapt a BinaryProcedure as a UnaryProcedure.
     * @param <A>
     * @param predicate BinaryProcedure to adapt
     * @return UnaryProcedure<A>
     */
    public static <A> UnaryProcedure<A> adapt(BinaryProcedure<? super A, ? super A> procedure) {
        return null == procedure ? null : new BinaryProcedureUnaryProcedure<A>(procedure);
    }

}
