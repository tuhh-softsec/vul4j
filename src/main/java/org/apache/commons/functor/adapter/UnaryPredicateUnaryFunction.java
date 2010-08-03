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
import org.apache.commons.functor.UnaryPredicate;

/**
 * Adapts a
 * {@link UnaryPredicate UnaryPredicate}
 * to the
 * {@link UnaryFunction UnaryFunction} interface.
 * <p/>
 * Note that although this class implements
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if the
 * underlying predicate is.  Attempts to serialize
 * an instance whose delegate is not
 * <code>Serializable</code> will result in an exception.
 *
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class UnaryPredicateUnaryFunction<A> implements UnaryFunction<A, Boolean>, Serializable {
    /** The {@link UnaryPredicate UnaryPredicate} I'm wrapping. */
    private UnaryPredicate<? super A> predicate;

    /**
     * Create a new UnaryPredicateUnaryFunction.
     * @param predicate to adapt
     */
    public UnaryPredicateUnaryFunction(UnaryPredicate<? super A> predicate) {
        if (predicate == null) {
            throw new IllegalArgumentException("UnaryPredicate argument was null");
        }
        this.predicate = predicate;
    }

    /**
     * {@inheritDoc}
     * Returns <code>Boolean.TRUE</code> (<code>Boolean.FALSE</code>)
     * when the {@link UnaryPredicate#test test} method of my underlying
     * predicate returns <code>true</code> (<code>false</code>).
     *
     * @return a non-<code>null</code> <code>Boolean</code> instance
     */
    public Boolean evaluate(A obj) {
        return predicate.test(obj);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this
                || (that instanceof UnaryPredicateUnaryFunction<?> && equals((UnaryPredicateUnaryFunction<?>) that));
    }

    /**
     * Learn whether another UnaryPredicateUnaryFunction is equal to this.
     * @param that UnaryPredicateUnaryFunction to test
     * @return boolean
     */
    public boolean equals(UnaryPredicateUnaryFunction<?> that) {
        return null != that && (null == predicate ? null == that.predicate : predicate.equals(that.predicate));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "UnaryPredicateUnaryFunction".hashCode();
        if (null != predicate) {
            hash ^= predicate.hashCode();
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "UnaryPredicateUnaryFunction<" + predicate + ">";
    }

    /**
     * Adapt a UnaryPredicate to the UnaryFunction interface.
     * @param predicate to adapt
     * @return UnaryPredicateUnaryFunction
     */
    public static <A> UnaryPredicateUnaryFunction<A> adapt(UnaryPredicate<? super A> predicate) {
        return null == predicate ? null : new UnaryPredicateUnaryFunction<A>(predicate);
    }

}
