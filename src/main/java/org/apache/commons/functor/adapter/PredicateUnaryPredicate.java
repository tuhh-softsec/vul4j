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

import org.apache.commons.functor.Predicate;
import org.apache.commons.functor.UnaryPredicate;

/**
 * Adapts a
 * {@link Predicate Predicate}
 * to the
 * {@link UnaryPredicate UnaryPredicate} interface
 * by ignoring the given argument.
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
public final class PredicateUnaryPredicate<A> implements UnaryPredicate<A>, Serializable {
    /** The {@link Predicate Predicate} I'm wrapping. */
    private Predicate predicate;

    /**
     * Create a new PredicateUnaryPredicate.
     * @param predicate to adapt
     */
    public PredicateUnaryPredicate(Predicate predicate) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate argument was null");
        }
        this.predicate = predicate;
    }

    /**
     * {@inheritDoc}
     */
    public boolean test(Object obj) {
        return predicate.test();
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof PredicateUnaryPredicate<?> && equals((PredicateUnaryPredicate<?>) that));
    }

    /**
     * Learn whether a given PredicateUnaryPredicate is equal to this.
     * @param that PredicateUnaryPredicate to test
     * @return boolean
     */
    public boolean equals(PredicateUnaryPredicate<?> that) {
        return null != that && (null == predicate ? null == that.predicate : predicate.equals(that.predicate));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "PredicateUnaryPredicate".hashCode();
        if (null != predicate) {
            hash ^= predicate.hashCode();
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "PredicateUnaryPredicate<" + predicate + ">";
    }

    /**
     * Adapt a Predicate to the UnaryPredicate interface.
     * @param predicate to adapt
     * @return PredicateUnaryPredicate<A
     */
    public static <A> PredicateUnaryPredicate<A> adapt(Predicate predicate) {
        return null == predicate ? null : new PredicateUnaryPredicate<A>(predicate);
    }

}
