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

import org.apache.commons.functor.BinaryPredicate;
import org.apache.commons.functor.UnaryPredicate;

/**
 * Adapts a
 * {@link UnaryPredicate UnaryPredicate}
 * to the
 * {@link BinaryPredicate BinaryPredicate} interface
 * by ignoring the second binary argument.
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
public final class IgnoreRightPredicate<A> implements BinaryPredicate<A, Object>, Serializable {
    /** The {@link UnaryPredicate UnaryPredicate} I'm wrapping. */
    private UnaryPredicate<? super A> predicate;

    /**
     * Create a new IgnoreRightPredicate.
     * @param predicate left
     */
    public IgnoreRightPredicate(UnaryPredicate<? super A> predicate) {
        this.predicate = predicate;
    }

    /**
     * {@inheritDoc}
     */
    public boolean test(A left, Object right) {
        return predicate.test(left);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof IgnoreRightPredicate && equals((IgnoreRightPredicate<?>) that));
    }

    /**
     * Learn whether a given IgnoreRightPredicate is equal to this.
     * @param that IgnoreRightPredicate to test
     * @return boolean
     */
    public boolean equals(IgnoreRightPredicate<?> that) {
        return null != that && (null == predicate ? null == that.predicate : predicate.equals(that.predicate));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "IgnoreRightPredicate".hashCode();
        if (null != predicate) {
            hash ^= predicate.hashCode();
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "IgnoreRightPredicate<" + predicate + ">";
    }

    /**
     * Adapt a UnaryPredicate as an IgnoreRightPredicate
     * @param predicate to adapt
     * @return IgnoreRightPredicate
     */
    public static <A> IgnoreRightPredicate<A> adapt(UnaryPredicate<? super A> predicate) {
        return null == predicate ? null : new IgnoreRightPredicate<A>(predicate);
    }

}
