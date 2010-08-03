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

import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.UnaryProcedure;
import org.apache.commons.functor.core.NoOp;

/**
 * A {@link UnaryProcedure UnaryProcedure}
 * similiar to Java's "ternary"
 * or "conditional" operator (<code>&#x3F; &#x3A;</code>).
 * Given a {@link UnaryPredicate predicate}
 * <i>p</i> and {@link UnaryProcedure procedures}
 * <i>q</i> and <i>r</i>, {@link #run runs}
 * <code>if (p.test(x)) { q.run(x); } else { r.run(x); }</code>.
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
public final class ConditionalUnaryProcedure<A> implements UnaryProcedure<A>, Serializable {
    // attributes
    // ------------------------------------------------------------------------
    private UnaryPredicate<? super A> ifPred = null;
    private UnaryProcedure<? super A> thenProc = null;
    private UnaryProcedure<? super A> elseProc = null;

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new ConditionalUnaryProcedure.
     * @param ifPred if
     * @param thenProc then
     */
    public ConditionalUnaryProcedure(UnaryPredicate<? super A> ifPred, UnaryProcedure<? super A> thenProc) {
    	this(ifPred, thenProc, NoOp.instance());
	}

    /**
     * Create a new ConditionalUnaryProcedure.
     * @param ifPred if
     * @param thenProc then
     * @param elseProc else
     */
    public ConditionalUnaryProcedure(UnaryPredicate<? super A> ifPred, UnaryProcedure<? super A> thenProc, UnaryProcedure<? super A> elseProc) {
        if (ifPred == null) {
            throw new IllegalArgumentException("UnaryPredicate argument was null");
        }
        this.ifPred = ifPred;
        if (thenProc == null || elseProc == null) {
            throw new IllegalArgumentException("One or more UnaryProcedure arguments was null");
        }
        this.thenProc = thenProc;
        this.elseProc = elseProc;
    }

    // predicate interface
    // ------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public void run(A obj) {
        if (ifPred.test(obj)) {
            thenProc.run(obj);
        } else {
            elseProc.run(obj);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof ConditionalUnaryProcedure<?> && equals((ConditionalUnaryProcedure<?>) that));
    }

    /**
     * Learn whether another ConditionalUnaryProcedure is equal to this.
     * @param that ConditionalUnaryProcedure to test
     * @return boolean
     */
    public boolean equals(ConditionalUnaryProcedure<?> that) {
        return null != that
                && (null == ifPred ? null == that.ifPred : ifPred.equals(that.ifPred))
                && (null == thenProc ? null == that.thenProc : thenProc.equals(that.thenProc))
                && (null == elseProc ? null == that.elseProc : elseProc.equals(that.elseProc));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "ConditionalUnaryProcedure".hashCode();
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
        return "ConditionalUnaryProcedure<" + ifPred + "?" + thenProc + ":" + elseProc + ">";
    }

}
