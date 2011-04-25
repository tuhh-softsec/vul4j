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

import org.apache.commons.functor.BinaryPredicate;

/**
 * Abstract base class for {@link BinaryPredicate BinaryPredicates}
 * composed of a list of {@link BinaryPredicate BinaryPredicates}.
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
abstract class BaseBinaryPredicateList<L, R> implements BinaryPredicate<L, R>, Serializable {

    // attributes
    // ------------------------------------------------------------------------
    private List<BinaryPredicate<? super L, ? super R>> list = new ArrayList<BinaryPredicate<? super L, ? super R>>();

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new BaseBinaryPredicateList.
     */
    protected BaseBinaryPredicateList() {
        super();
    }

    /**
     * Create a new BaseBinaryPredicateList instance.
     *
     * @param predicates to add
     */
    protected BaseBinaryPredicateList(BinaryPredicate<? super L, ? super R>... predicates) {
        this();
        if (predicates != null) {
            for (BinaryPredicate<? super L, ? super R> p : predicates) {
                addBinaryPredicate(p);
            }
        }
    }

    /**
     * Create a new BaseBinaryPredicateList instance.
     *
     * @param predicates to add
     */
    protected BaseBinaryPredicateList(Iterable<BinaryPredicate<? super L, ? super R>> predicates) {
        this();
        if (predicates != null) {
            for (BinaryPredicate<? super L, ? super R> p : predicates) {
                addBinaryPredicate(p);
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
     * Add a BinaryPredicate to the list
     * @param p BinaryPredicate to add
     */
    protected void addBinaryPredicate(BinaryPredicate<? super L, ? super R> p) {
        if (p == null) {
            throw new IllegalArgumentException("Cannot add null BinaryPredicate");
        }
        list.add(p);
    }

    // protected
    // ------------------------------------------------------------------------
    /**
     * Get the "live" list of contained {@link BinaryPredicate} instances.
     * @return List
     */
    protected List<BinaryPredicate<? super L, ? super R>> getBinaryPredicateList() {
        return list;
    }

    /**
     * Learn whether another list is equal to this one.
     * @param that BaseBinaryPredicateList to test
     * @return boolean
     */
    protected boolean getBinaryPredicateListEquals(BaseBinaryPredicateList<?, ?> that) {
        return (null != that && this.list.equals(that.list));
    }

    /**
     * Get a hashCode for the list.
     * @return int
     */
    protected int getBinaryPredicateListHashCode() {
        return list.hashCode();
    }

    /**
     * Get a toString for the list.
     * @return String
     */
    protected String getBinaryPredicateListToString() {
        return String.valueOf(list);
    }

}
