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
 * at least one of its children test <code>true</code>.
 * Note that by this definition, the "or" of
 * an empty collection of predicates tests <code>false</code>.
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
public final class BinaryOr<L, R> extends BaseBinaryPredicateList<L, R> {

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new BinaryOr.
     */
    public BinaryOr() {
        super();
    }

    /**
     * Create a new BinaryOr.
     * @param p BinaryPredicate to add
     */
    public BinaryOr(BinaryPredicate<? super L, ? super R> p) {
        super(p);
    }

    /**
     * Create a new BinaryOr.
     * @param p BinaryPredicate to add
     * @param q BinaryPredicate to add
     */
    public BinaryOr(BinaryPredicate<? super L, ? super R> p, BinaryPredicate<? super L, ? super R> q) {
        super(p, q);
    }

    /**
     * Create a new BinaryOr.
     * @param p BinaryPredicate to add
     * @param q BinaryPredicate to add
     * @param r BinaryPredicate to add
     */
    public BinaryOr(BinaryPredicate<? super L, ? super R> p, BinaryPredicate<? super L, ? super R> q,
            BinaryPredicate<? super L, ? super R> r) {
        super(p, q, r);
    }

    // modifiers
    // ------------------------------------------------------------------------
    /**
     * Fluently add a BinaryPredicate.
     * @param p BinaryPredicate to add
     * @return this
     */
    public BinaryOr<L, R> or(BinaryPredicate<? super L, ? super R> p) {
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
            if (iter.next().test(a, b)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof BinaryOr<?, ?> && equals((BinaryOr<?, ?>) that));
    }

    /**
     * Learn whether another BinaryOr is equal to this.
     * @param that BinaryOr to test
     * @return boolean
     */
    public boolean equals(BinaryOr<?, ?> that) {
        return getBinaryPredicateListEquals(that);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return "BinaryOr".hashCode() ^ getBinaryPredicateListHashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "BinaryOr<" + getBinaryPredicateListToString() + ">";
    }

}
