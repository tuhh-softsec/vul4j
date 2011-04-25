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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.functor.UnaryPredicate;

/**
 * Abstract base class for {@link UnaryPredicate UnaryPredicates}
 * composed of a list of {@link UnaryPredicate UnaryPredicates}.
 * <p>
 * Note that although this class implements
 * {@link Serializable}, a given instance will
 * only be truly <code>Serializable</code> if all the
 * underlying functors are.  Attempts to serialize
 * an instance whose delegates are not all
 * <code>Serializable</code> will result in an exception.
 * </p>
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
abstract class BaseUnaryPredicateList<A> implements UnaryPredicate<A>, Serializable {

    // attributes
    // ------------------------------------------------------------------------
    private List<UnaryPredicate<? super A>> list = new ArrayList<UnaryPredicate<? super A>>();

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new BaseUnaryPredicateList.
     */
    protected BaseUnaryPredicateList() {
        super();
    }

    /**
     * Create a new BaseUnaryPredicateList instance.
     *
     * @param predicates to add
     */
    protected BaseUnaryPredicateList(UnaryPredicate<? super A>... predicates) {
        this();
        if (predicates != null) {
            for (UnaryPredicate<? super A> p : predicates) {
                addUnaryPredicate(p);
            }
        }
    }

    /**
     * Create a new BaseUnaryPredicateList instance.
     *
     * @param predicates to add
     */
    protected BaseUnaryPredicateList(Iterable<UnaryPredicate<? super A>> predicates) {
        this();
        if (predicates != null) {
            for (UnaryPredicate<? super A> p : predicates) {
                addUnaryPredicate(p);
            }
        }
    }

    // abstract
    // ------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public abstract boolean equals(Object that);

    /**
     * {@inheritDoc}
     */
    public abstract int hashCode();

    /**
     * {@inheritDoc}
     */
    public abstract String toString();

    // modifiers
    // ------------------------------------------------------------------------
    /**
     * Add a UnaryPredicate to the list
     * @param p UnaryPredicate to add
     */
    protected void addUnaryPredicate(UnaryPredicate<? super A> p) {
        if (p == null) {
            throw new IllegalArgumentException("Cannot add null UnaryPredicate");
        }
        list.add(p);
    }

    // protected
    // ------------------------------------------------------------------------

    /**
     * Get the "live" list of contained {@link UnaryPredicate}s.
     * @return List
     */
    protected List<UnaryPredicate<? super A>> getUnaryPredicateList() {
        return list;
    }

    /**
     * Learn whether another BaseUnaryPredicateList has content equal to this
     * @param that the BaseUnaryPredicateList to test
     * @return boolean
     */
    protected boolean getUnaryPredicateListEquals(BaseUnaryPredicateList<?> that) {
        return (null != that && this.list.equals(that.list));
    }

    /**
     * Get a hashCode for the list.
     * @return int
     */
    protected int getUnaryPredicateListHashCode() {
        return list.hashCode();
    }

    /**
     * Get a toString for the list.
     * @return String
     */
    protected String getUnaryPredicateListToString() {
        return String.valueOf(list);
    }

}
