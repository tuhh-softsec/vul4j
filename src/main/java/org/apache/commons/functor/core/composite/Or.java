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

import org.apache.commons.functor.Predicate;

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
public final class Or extends BasePredicateList {

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new Or.
     */
    public Or() {
        super();
    }

    /**
     * Create a new Or.
     * @param p Predicate to add
     */
    public Or(Predicate p) {
        super(p);
    }

    /**
     * Create a new Or.
     * @param p Predicate to add
     * @param q Predicate to add
     */
    public Or(Predicate p, Predicate q) {
        super(p, q);
    }

    /**
     * Create a new Or.
     * @param p Predicate to add
     * @param q Predicate to add
     * @param r Predicate to add
     */
    public Or(Predicate p, Predicate q, Predicate r) {
        super(p, q, r);
    }

    /**
     * Fluently add a Predicate.
     * @param p Predicate to add
     * @return this
     */
    public Or or(Predicate p) {
        super.addPredicate(p);
        return this;
    }

    // predicate interface
    // ------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public boolean test() {
        for (Iterator<Predicate> iter = getPredicateIterator(); iter.hasNext();) {
            if (iter.next().test()) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof Or && equals((Or) that));
    }

    /**
     * Learn whether another Or is equal to this.
     * @param that Or to test
     * @return boolean
     */
    public boolean equals(Or that) {
        return getPredicateListEquals(that);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return "Or".hashCode() ^ getPredicateListHashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "Or<" + getPredicateListToString() + ">";
    }

}
