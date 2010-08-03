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
import org.apache.commons.functor.BinaryProcedure;
import org.apache.commons.functor.core.NoOp;

/**
 * A {@link BinaryProcedure BinaryProcedure}
 * similiar to Java's "ternary"
 * or "conditional" operator (<code>&#x3F; &#x3A;</code>).
 * Given a {@link BinaryPredicate predicate}
 * <i>p</i> and {@link BinaryProcedure procedures}
 * <i>q</i> and <i>r</i>, {@link #run runs}
 * <code>if (p.test(x,y)) { q.run(x,y); } else { r.run(x,y); }</code>.
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
public final class ConditionalBinaryProcedure<L, R> implements BinaryProcedure<L, R>, Serializable {

    // attributes
    // ------------------------------------------------------------------------
    private BinaryPredicate<? super L, ? super R> ifPred;
    private BinaryProcedure<? super L, ? super R> thenProc;
    private BinaryProcedure<? super L, ? super R> elseProc;

    // constructor
    // ------------------------------------------------------------------------

    /**
     * Create a new ConditionalBinaryProcedure.
     * @param ifPred to evaluate
     * @param thenProc if <code>ifPred</code> yields <code>true</code>
     */
    public ConditionalBinaryProcedure(BinaryPredicate<? super L, ? super R> ifPred,
            BinaryProcedure<? super L, ? super R> thenProc) {
        this(ifPred, thenProc, NoOp.instance());
    }

    /**
     * Create a new ConditionalBinaryProcedure.
     * @param ifPred to evaluate
     * @param thenProc if <code>ifPred</code> yields <code>true</code>
     * @param elseProc if <code>ifPred</code> yields <code>false</code>
     */
    public ConditionalBinaryProcedure(BinaryPredicate<? super L, ? super R> ifPred,
            BinaryProcedure<? super L, ? super R> thenProc, BinaryProcedure<? super L, ? super R> elseProc) {
        if (ifPred == null) {
            throw new IllegalArgumentException("BinaryPredicate argument was null");
        }
        this.ifPred = ifPred;
        if (thenProc == null || elseProc == null) {
            throw new IllegalArgumentException("One or more BinaryProcedure arguments was null");
        }
        this.thenProc = thenProc;
        this.elseProc = elseProc;
    }

    // predicate interface
    // ------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void run(L left, R right) {
        if (ifPred.test(left, right)) {
            thenProc.run(left, right);
        } else {
            elseProc.run(left, right);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this
                || (that instanceof ConditionalBinaryProcedure<?, ?> && equals((ConditionalBinaryProcedure<?, ?>) that));
    }

    /**
     * Learn whether a given ConditionalBinaryProcedure is equal to this.
     * @param that compared object
     * @return boolean
     */
    public boolean equals(ConditionalBinaryProcedure<?, ?> that) {
        return null != that
                && (null == ifPred ? null == that.ifPred : ifPred.equals(that.ifPred))
                && (null == thenProc ? null == that.thenProc : thenProc.equals(that.thenProc))
                && (null == elseProc ? null == that.elseProc : elseProc.equals(that.elseProc));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "ConditionalBinaryProcedure".hashCode();
        if (null != ifPred) {
            hash <<= 4;
            hash ^= ifPred.hashCode();
        }
        if (null != thenProc) {
            hash <<= 4;
            hash ^= thenProc.hashCode();
        }
        if (null != elseProc) {
            hash <<= 4;
            hash ^= elseProc.hashCode();
        }
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "ConditionalBinaryProcedure<" + ifPred + "?" + thenProc + ":" + elseProc + ">";
    }
}
