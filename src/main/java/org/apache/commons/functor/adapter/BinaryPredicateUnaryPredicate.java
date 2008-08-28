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

import org.apache.commons.functor.BinaryPredicate;
import org.apache.commons.functor.UnaryPredicate;

/**
 * Adapts a BinaryPredicate as a UnaryPredicate by sending the same argument to both sides of the BinaryPredicate.
 * @version $Revision$ $Date$
 * @author Matt Benson
 */
public final class BinaryPredicateUnaryPredicate<A> implements UnaryPredicate<A> {
    private BinaryPredicate<? super A, ? super A> predicate;

    /**
     * Create a new BinaryPredicateUnaryPredicate.
     * @param predicate to adapt
     */
    public BinaryPredicateUnaryPredicate(BinaryPredicate<? super A, ? super A> predicate) {
        if (null == predicate) {
            throw new IllegalArgumentException("BinaryPredicate argument was null");
        }
        this.predicate = predicate;
    }

    /**
     * {@inheritDoc}
     */
    public boolean test(A obj) {
        return predicate.test(obj, obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof BinaryPredicateUnaryPredicate
                && equals((BinaryPredicateUnaryPredicate<?>) obj);
    }

    /**
     * Learn whether another BinaryPredicateUnaryPredicate is equal to
     * <code>this</code>.
     * 
     * @param that BinaryPredicateUnaryPredicate to check
     * 
     * @return whether equal
     */
    public boolean equals(BinaryPredicateUnaryPredicate<?> that) {
        return that != null && that.predicate.equals(this.predicate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return ("BinaryPredicateUnaryPredicate".hashCode() << 2) | predicate.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "BinaryPredicateUnaryPredicate<" + predicate + ">";
    }

    /**
     * Adapt a BinaryFunction as a UnaryFunction.
     * @param <A>
     * @param predicate BinaryPredicate to adapt
     * @return UnaryPredicate<A>
     */
    public static <A> UnaryPredicate<A> adapt(BinaryPredicate<? super A, ? super A> predicate) {
        return null == predicate ? null : new BinaryPredicateUnaryPredicate<A>(predicate);
    }

}
