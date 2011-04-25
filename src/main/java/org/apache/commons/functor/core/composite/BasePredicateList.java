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

import org.apache.commons.functor.Predicate;

/**
 * Abstract base class for {@link Predicate Predicates}
 * composed of a list of {@link Predicate Predicates}.
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
abstract class BasePredicateList implements Predicate, Serializable {
    // attributes
    // ------------------------------------------------------------------------
    private final List<Predicate> list = new ArrayList<Predicate>();

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new BasePredicateList instance.
     */
    protected BasePredicateList() {
        super();
    }

    /**
     * Create a new BasePredicateList instance.
     *
     * @param predicates to add
     */
    protected BasePredicateList(Predicate... predicates) {
        this();
        if (predicates != null) {
            for (Predicate p : predicates) {
                addPredicate(p);
            }
        }
    }

    /**
     * Create a new BasePredicateList instance.
     *
     * @param predicates to add
     */
    protected BasePredicateList(Iterable<Predicate> predicates) {
        this();
        if (predicates != null) {
            for (Predicate p : predicates) {
                addPredicate(p);
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
     * Add a Predicate to the list.
     * @param p Predicate to add
     */
    protected void addPredicate(Predicate p) {
        if (p == null) {
            throw new IllegalArgumentException("Cannot add null Predicate");
        }
        list.add(p);
    }

    // protected
    // ------------------------------------------------------------------------

    /**
     * Get the "live" list of {@link Predicate}s.
     * @return List<Predicate>
     */
    protected List<Predicate> getPredicateList() {
        return list;
    }

    /**
     * Learn whether the list of another BasePredicateList is equal to my list.
     * @param that BasePredicateList to test
     * @return boolean
     */
    protected boolean getPredicateListEquals(BasePredicateList that) {
        return (null != that && this.list.equals(that.list));
    }

    /**
     * Get a hashCode for my list.
     * @return int
     */
    protected int getPredicateListHashCode() {
        return list.hashCode();
    }

    /**
     * Get a toString for my list.
     * @return String
     */
    protected String getPredicateListToString() {
        return String.valueOf(list);
    }

}
