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

import java.util.Iterator;

import org.apache.commons.functor.UnaryPredicate;

/**
 * {@link #test Tests} <code>true</code> iff
 * none of its children test <code>false</code>.
 * Note that by this definition, the "and" of
 * an empty collection of predicates tests <code>true</code>.
 * <p>
 * Note that although this class implements
 * {@link java.io.Serializable Serializable}, a given instance will
 * only be truly <code>Serializable</code> if all the
 * underlying functors are.  Attempts to serialize
 * an instance whose delegates are not all
 * <code>Serializable</code> will result in an exception.
 * </p>
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class UnaryAnd<A> extends BaseUnaryPredicateList<A> {

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new UnaryAnd.
     */
    public UnaryAnd() {
        super();
    }

    /**
     * Create a new UnaryAnd.
     * @param p UnaryPredicate to add
     */
    public UnaryAnd(UnaryPredicate<? super A> p) {
        super(p);
    }

    /**
     * Create a new UnaryAnd.
     * @param p UnaryPredicate to add
     * @param q UnaryPredicate to add
     */
    public UnaryAnd(UnaryPredicate<? super A> p, UnaryPredicate<? super A> q) {
        super(p, q);
    }

    /**
     * Create a new UnaryAnd.
     * @param p UnaryPredicate to add
     * @param q UnaryPredicate to add
     * @param r UnaryPredicate to add
     */
    public UnaryAnd(UnaryPredicate<? super A> p, UnaryPredicate<? super A> q, UnaryPredicate<? super A> r) {
        super(p, q, r);
    }

    // modifiers
    // ------------------------------------------------------------------------
    /**
     * Fluently add a UnaryPredicate.
     * @param p UnaryPredicate to add
     * @return this
     */
    public UnaryAnd<A> and(UnaryPredicate<? super A> p) {
        super.addUnaryPredicate(p);
        return this;
    }

    // predicate interface
    // ------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public boolean test(A obj) {
        for (Iterator<UnaryPredicate<? super A>> iter = getUnaryPredicateIterator(); iter.hasNext();) {
            if (!iter.next().test(obj)) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof UnaryAnd<?> && equals((UnaryAnd<?>) that));
    }

    /**
     * Learn whether another UnaryAnd is equal to this.
     * @param that UnaryAnd to test
     * @return boolean
     */
    public boolean equals(UnaryAnd<?> that) {
        return getUnaryPredicateListEquals(that);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return "UnaryAnd".hashCode() ^ getUnaryPredicateListHashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "UnaryAnd<" + getUnaryPredicateListToString() + ">";
    }

}
