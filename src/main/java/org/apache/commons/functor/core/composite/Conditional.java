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

import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.BinaryPredicate;
import org.apache.commons.functor.BinaryProcedure;
import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.UnaryProcedure;

/**
 * Utility methods for creating conditional functors.
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class Conditional {

    // constructor - for beanish apis
    // ------------------------------------------------------------------------

    /**
     * Create a new Conditional.
     */
    public Conditional() { }

    // ------------------------------------------------------------------------

    /**
     * Create a conditional procedure.
     * @param q if
     * @param r then
     * @param s else
     * @return UnaryProcedure<A>
     */
    public static final <A> UnaryProcedure<A> procedure(UnaryPredicate<? super A> q, UnaryProcedure<? super A> r,
            UnaryProcedure<? super A> s) {
        return new ConditionalUnaryProcedure<A>(q, r, s);
    }

    /**
     * Create a conditional function.
     * @param q if
     * @param r then
     * @param s else
     * @return UnaryFunction<A, T>
     */
    public static final <A, T> UnaryFunction<A, T> function(UnaryPredicate<? super A> q,
            UnaryFunction<? super A, ? extends T> r, UnaryFunction<? super A, ? extends T> s) {
        return new ConditionalUnaryFunction<A, T>(q, r, s);
    }

    /**
     * Create a conditional predicate.
     * @param q if
     * @param r then
     * @param s else
     * @return UnaryPredicate<A>
     */
    public static final <A> UnaryPredicate<A> predicate(UnaryPredicate<? super A> q, UnaryPredicate<? super A> r,
            UnaryPredicate<? super A> s) {
        return new ConditionalUnaryPredicate<A>(q, r, s);
    }

    /**
     * Create a conditional binary procedure.
     * @param q if
     * @param r then
     * @param s else
     * @return BinaryProcedure<L, R>
     */
    public static final <L, R> BinaryProcedure<L, R> procedure(BinaryPredicate<? super L, ? super R> q,
            BinaryProcedure<? super L, ? super R> r, BinaryProcedure<? super L, ? super R> s) {
        return new ConditionalBinaryProcedure<L, R>(q, r, s);
    }

    /**
     * Create a conditional binary function.
     * @param q if
     * @param r then
     * @param s else
     * @return BinaryFunction<L, R, T>
     */
    public static final <L, R, T> BinaryFunction<L, R, T> function(BinaryPredicate<? super L, ? super R> q,
            BinaryFunction<? super L, ? super R, ? extends T> r, BinaryFunction<? super L, ? super R, ? extends T> s) {
        return new ConditionalBinaryFunction<L, R, T>(q, r, s);
    }

    /**
     * Create a conditional binary predicate.
     * @param q if
     * @param r then
     * @param s else
     * @return BinaryPredicate<L, R>
     */
    public static final <L, R> BinaryPredicate<L, R> predicate(BinaryPredicate<? super L, ? super R> q,
            BinaryPredicate<? super L, ? super R> r, BinaryPredicate<? super L, ? super R> s) {
        return new ConditionalBinaryPredicate<L, R>(q, r, s);
    }

}
