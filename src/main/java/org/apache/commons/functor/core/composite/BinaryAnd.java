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

import org.apache.commons.functor.BinaryPredicate;

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
public final class BinaryAnd<L, R> extends BaseBinaryPredicateList<L, R> {

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new BinaryAnd.
     */
    public BinaryAnd() {
        super();
    }

    /**
     * Create a new BinaryAnd.
     * @param p BinaryPredicate to add
     */
    public BinaryAnd(BinaryPredicate<? super L, ? super R> p) {
        super(p);
    }

    /**
     * Create a new BinaryAnd.
     * @param p BinaryPredicate to add
     * @param q BinaryPredicate to add
     */
    public BinaryAnd(BinaryPredicate<? super L, ? super R> p, BinaryPredicate<? super L, ? super R> q) {
        super(p, q);
    }

    /**
     * Create a new BinaryAnd.
     * @param p BinaryPredicate to add
     * @param q BinaryPredicate to add
     * @param r BinaryPredicate to add
     */
    public BinaryAnd(BinaryPredicate<? super L, ? super R> p, BinaryPredicate<? super L, ? super R> q,
            BinaryPredicate<? super L, ? super R> r) {
        super(p, q, r);
    }

    // modifiers
    // ------------------------------------------------------------------------
    /**
     * And in a BinaryPredicate.
     * @param p BinaryPredicate to add
     * @return this
     */
    public BinaryAnd<L, R> and(BinaryPredicate<? super L, ? super R> p) {
        super.addBinaryPredicate(p);
        return this;
    }

    // predicate interface
    // ------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public boolean test(L a, R b) {
        for (Iterator<BinaryPredicate<? super L, ? super R>> iter = getBinaryPredicateIterator(); iter.hasNext();) {
            if (!iter.next().test(a, b)) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof BinaryAnd<?, ?> && equals((BinaryAnd<?, ?>) that));
    }

    /**
     * Learn whether another BinaryAnd is equal to this.
     * @param that the BinaryAnd to test
     * @return boolean
     */
    public boolean equals(BinaryAnd<?, ?> that) {
        return getBinaryPredicateListEquals(that);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return "BinaryAnd".hashCode() ^ getBinaryPredicateListHashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "BinaryAnd<" + getBinaryPredicateListToString() + ">";
    }

}
