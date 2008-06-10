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

import org.apache.commons.functor.Function;
import org.apache.commons.functor.Predicate;

/**
 * Adapts a <code>Boolean</code>-valued
 * {@link Function Function} to the
 * {@link Predicate Predicate} interface.
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
public final class FunctionPredicate implements Predicate, Serializable {

    /** The {@link Function Function} I'm wrapping. */
    private Function<Boolean> function;

    /**
     * Create a new FunctionPredicate.
     * @param function to adapt
     */
    public FunctionPredicate(Function<Boolean> function) {
        if (function == null) {
            throw new IllegalArgumentException("Function argument was null");
        }
        this.function = function;
    }

    /**
     * Returns the <code>boolean</code> value of the non-<code>null</code>
     * <code>Boolean</code> returned by the {@link Function#evaluate evaluate}
     * method of my underlying function.
     * {@inheritDoc}
     * @throws NullPointerException if my underlying function returns <code>null</code>
     * @throws ClassCastException if my underlying function returns a non-<code>Boolean</code>
     */
    public boolean test() {
        return function.evaluate();
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof FunctionPredicate && equals((FunctionPredicate) that));
    }

    /**
     * Learn whether another FunctionPredicate is equal to this.
     * @param that FunctionPredicate to test
     * @return boolean
     */
    public boolean equals(FunctionPredicate that) {
        return null != that && (null == function ? null == that.function : function.equals(that.function));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "FunctionPredicate".hashCode();
        if (null != function) {
            hash ^= function.hashCode();
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "FunctionPredicate<" + function + ">";
    }

    /**
     * Adapt a Function as a Predicate.
     * @param function to adapt
     * @return FunctionPredicate
     */
    public static FunctionPredicate adapt(Function<Boolean> function) {
        return null == function ? null : new FunctionPredicate(function);
    }
}
