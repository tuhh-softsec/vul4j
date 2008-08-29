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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.functor.UnaryProcedure;

/**
 * A {@link UnaryProcedure UnaryProcedure}
 * that {@link UnaryProcedure#run runs} an ordered
 * sequence of {@link UnaryProcedure UnaryProcedures}.
 * When the sequence is empty, this procedure is does
 * nothing.
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
public class UnarySequence<A> implements UnaryProcedure<A>, Serializable {

    // attributes
    // ------------------------------------------------------------------------
    private List<UnaryProcedure<? super A>> list = new ArrayList<UnaryProcedure<? super A>>();

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new UnarySequence.
     */
    public UnarySequence() {
    }

    /**
     * Create a new UnarySequence.
     * @param p UnaryProcedure to add
     */
    public UnarySequence(UnaryProcedure<? super A> p) {
        then(p);
    }

    /**
     * Create a new UnarySequence.
     * @param p UnaryProcedure to add
     * @param q UnaryProcedure to add
     */
    public UnarySequence(UnaryProcedure<? super A> p, UnaryProcedure<? super A> q) {
        then(p);
        then(q);
    }

    // modifiers
    // ------------------------------------------------------------------------
    /**
     * Fluently add a UnaryProcedure to the sequence.
     * @param p UnaryProcedure to add
     * @return this
     */
    public UnarySequence<A> then(UnaryProcedure<? super A> p) {
        list.add(p);
        return this;
    }

    // predicate interface
    // ------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public void run(A obj) {
        for (Iterator<UnaryProcedure<? super A>> iter = list.iterator(); iter.hasNext();) {
            iter.next().run(obj);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof UnarySequence && equals((UnarySequence<?>) that));
    }

    /**
     * Learn whether another UnarySequence is equal to this.
     * @param that UnarySequence to test
     * @return boolean
     */
    public boolean equals(UnarySequence<?> that) {
        // by construction, list is never null
        return null != that && list.equals(that.list);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        // by construction, list is never null
        return "UnarySequence".hashCode() ^ list.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "UnarySequence<" + list + ">";
    }

}
