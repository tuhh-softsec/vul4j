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
package org.apache.commons.functor.core.algorithm;

import java.io.Serializable;
import java.util.NoSuchElementException;

import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.UnaryProcedure;
import org.apache.commons.functor.generator.Generator;

/**
 * Return the first Object in a {@link Generator} matching a {@link UnaryPredicate}.
 *
 * @version $Revision$ $Date$
 */
public final class FindWithinGenerator<E> implements BinaryFunction<Generator<E>, UnaryPredicate<E>, E>, Serializable {
    /**
     * Basic instance.
     */
    public static final FindWithinGenerator<Object> INSTANCE = new FindWithinGenerator<Object>();

    /**
     * Helper procedure.
     */
    private static class FindProcedure<T> implements UnaryProcedure<T> {
        private T found;
        private boolean wasFound;
        private UnaryPredicate<T> pred;

        /**
         * Create a new FindProcedure.
         * @pred test
         */
        public FindProcedure(UnaryPredicate<T> pred) {
            this.pred = pred;
        }

        /**
         * {@inheritDoc}
         */
        public void run(T obj) {
            if (!wasFound && pred.test(obj)) {
                wasFound = true;
                found = obj;
            }
        }
    }

    private boolean useIfNone;
    private E ifNone;

    /**
     * Create a new FindWithinGenerator.
     */
    public FindWithinGenerator() {
        super();
    }

    /**
     * Create a new FindWithinGenerator.
     * @param ifNone object to return if the Generator contains no matches.
     */
    public FindWithinGenerator(E ifNone) {
        this();
        this.ifNone = ifNone;
        useIfNone = true;
    }

    /**
     * {@inheritDoc}
     * @param left Generator
     * @param right UnaryPredicate
     */
    public E evaluate(Generator<E> left, UnaryPredicate<E> right) {
        FindProcedure<E> findProcedure = new FindProcedure<E>(right);
        left.run(findProcedure);
        if (!findProcedure.wasFound) {
            if (useIfNone) {
                return ifNone;
            }
            throw new NoSuchElementException("No element matching " + right + " was found.");
        }
        return findProcedure.found;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof FindWithinGenerator<?> == false) {
            return false;
        }
        FindWithinGenerator<?> other = (FindWithinGenerator<?>) obj;
        return other.useIfNone == useIfNone && !useIfNone
                || (other.ifNone == this.ifNone || other.ifNone != null && other.ifNone.equals(this.ifNone));
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        if (!this.useIfNone) {
            return System.identityHashCode(INSTANCE);
        }
        int result = "FindWithinGenerator".hashCode(); 
        result ^= this.ifNone == null ? 0 : this.ifNone.hashCode();
        return result;
    }

    /**
     * Get a static {@link FindWithinGenerator} instance.
     * @return {@link FindWithinGenerator}
     */
    public static FindWithinGenerator<Object> instance() {
        return INSTANCE;
    }
}
