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

import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.adapter.UnaryPredicateUnaryFunction;

/**
 * A {@link UnaryPredicate UnaryPredicate}
 * representing the composition of
 * {@link UnaryFunction UnaryFunctions},
 * "chaining" the output of one to the input
 * of another.  For example,
 * <pre>new CompositeUnaryPredicate(p).of(f)</code>
 * {@link #test tests} to
 * <code>p.test(f.evaluate(obj))</code>, and
 * <pre>new CompositeUnaryPredicate(p).of(f).of(g)</pre>
 * {@link #test tests} to
 * <code>p.test(f.evaluate(g.evaluate(obj)))</code>.
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
public final class CompositeUnaryPredicate<A> implements UnaryPredicate<A>, Serializable {
    // attributes
    // ------------------------------------------------------------------------
    private CompositeUnaryFunction<? super A, Boolean> function = null;

    // constructor
    // ------------------------------------------------------------------------
    /**
     * Create a new CompositeUnaryPredicate.
     * @param predicate UnaryPredicate against which the composite functions' output will be tested
     */
    public CompositeUnaryPredicate(UnaryPredicate<? super A> predicate) {
        if (null == predicate) {
            throw new IllegalArgumentException("predicate must not be null");
        }
        this.function = new CompositeUnaryFunction<A, Boolean>(new UnaryPredicateUnaryFunction<A>(predicate));
    }

    /**
     * Create a new CompositeUnaryPredicate.
     * @param function delegate
     */
    private CompositeUnaryPredicate(CompositeUnaryFunction<? super A, Boolean> function) {
        this.function = function;
    }

    // modifiers
    // ------------------------------------------------------------------------
    /**
     * Fluently obtain a CompositeUnaryPredicate that applies our predicate to the result of the preceding function.
     * @param preceding UnaryFunction
     * @return CompositeUnaryPredicate<P>
     */
    public <P> CompositeUnaryPredicate<P> of(UnaryFunction<? super P, ? extends A> preceding) {
        return new CompositeUnaryPredicate<P>(function.of(preceding));
    }

    // predicate interface
    // ------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public boolean test(A obj) {
        return function.evaluate(obj);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object that) {
        return that == this || (that instanceof CompositeUnaryPredicate<?> && equals((CompositeUnaryPredicate<?>) that));
    }

    /**
     * Learn whether another CompositeUnaryPredicate is equal to this.
     * @param that CompositeUnaryPredicate to test
     * @return boolean
     */
    public boolean equals(CompositeUnaryPredicate<?> that) {
        return null != that && function.equals(that.function);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "CompositeUnaryPredicate".hashCode();
        hash <<= 2;
        hash ^= function.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "CompositeUnaryFunction<" + function + ">";
    }

}
