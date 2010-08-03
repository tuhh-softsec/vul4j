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

import org.apache.commons.functor.BinaryPredicate;

/**
 * A {@link BinaryPredicate BinaryPredicate}
 * similiar to Java's "ternary"
 * or "conditional" operator (<code>&#x3F; &#x3A;</code>).
 * Given three {@link BinaryPredicate predicates}
 * <i>p</i>, <i>q</i>, <i>r</i>,
 * {@link #test tests}
 * to
 * <code>p.test(x,y) ? q.test(x,y) : r.test(x,y)</code>.
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
public final class ConditionalBinaryPredicate<L, R> implements BinaryPredicate<L, R>, Serializable {
    // attributes
    // ------------------------------------------------------------------------
    private BinaryPredicate<? super L, ? super R> ifPred = null;
    private BinaryPredicate<? super L, ? super R> thenPred = null;
    private BinaryPredicate<? super L, ? super R> elsePred = null;

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new ConditionalBinaryPredicate.
     * @param ifPred if
     * @param thenPred then
     * @param elsePred else
     */
    public ConditionalBinaryPredicate(BinaryPredicate<? super L, ? super R> ifPred,
            BinaryPredicate<? super L, ? super R> thenPred, BinaryPredicate<? super L, ? super R> elsePred) {
        if (ifPred == null || thenPred == null || elsePred == null) {
            throw new IllegalArgumentException("One or more BinaryPredicate arguments was null");
        }
        this.ifPred = ifPred;
        this.thenPred = thenPred;
        this.elsePred = elsePred;
    }

    // predicate interface
    // ------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public boolean test(L left, R right) {
        return ifPred.test(left, right) ? thenPred.test(left, right) : elsePred.test(left, right);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this
                || (that instanceof ConditionalBinaryPredicate<?, ?> && equals((ConditionalBinaryPredicate<?, ?>) that));
    }

    /**
     * Learn whether another ConditionalBinaryPredicate is equal to this.
     * @param that ConditionalBinaryPredicate to test
     * @return boolean
     */
    public boolean equals(ConditionalBinaryPredicate<?, ?> that) {
        return null != that
                && (null == ifPred ? null == that.ifPred : ifPred.equals(that.ifPred))
                && (null == thenPred ? null == that.thenPred : thenPred.equals(that.thenPred))
                && (null == elsePred ? null == that.elsePred : elsePred.equals(that.elsePred));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "ConditionalBinaryPredicate".hashCode();
        if (null != ifPred) {
            hash <<= 4;
            hash ^= ifPred.hashCode();
        }
        if (null != thenPred) {
            hash <<= 4;
            hash ^= thenPred.hashCode();
        }
        if (null != elsePred) {
            hash <<= 4;
            hash ^= elsePred.hashCode();
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "ConditionalBinaryPredicate<" + ifPred + "?" + thenPred + ":" + elsePred + ">";
    }

}
