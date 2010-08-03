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

import org.apache.commons.functor.UnaryPredicate;

/**
 * {@link #test Tests} to the logical inverse
 * of some other predicate.
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
public final class UnaryNot<A> implements UnaryPredicate<A>, Serializable {
    // attributes
    // ------------------------------------------------------------------------
    private UnaryPredicate<? super A> predicate = null;

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new UnaryNot.
     * @param predicate UnaryPredicate to negate
     */
    public UnaryNot(UnaryPredicate<? super A> predicate) {
        if (predicate == null) {
            throw new IllegalArgumentException("UnaryPredicate argument was null");
        }
        this.predicate = predicate;
    }

    // predicate interface
    // ------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public boolean test(A obj) {
        return !(predicate.test(obj));
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof UnaryNot<?> && equals((UnaryNot<?>) that));
    }

    /**
     * Learn whether another UnaryNot is equal to this.
     * @param that UnaryNot to test
     * @return boolean
     */
    public boolean equals(UnaryNot<?> that) {
        return null != that && (null == predicate ? null == that.predicate : predicate.equals(that.predicate));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "UnaryNot".hashCode();
        if (null != predicate) {
            hash ^= predicate.hashCode();
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "UnaryNot<" + predicate + ">";
    }

    // static
    // ------------------------------------------------------------------------
    /**
     * Invert a UnaryPredicate.
     * @param pred UnaryPredicate to invert
     * @return UnaryPredicate<A
     */
    public static <A> UnaryPredicate<A> not(UnaryPredicate<? super A> pred) {
        return null == pred ? null : new UnaryNot<A>(pred);
    }

}
