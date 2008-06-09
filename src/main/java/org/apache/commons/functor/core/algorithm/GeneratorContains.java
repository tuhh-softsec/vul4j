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

import org.apache.commons.functor.BinaryPredicate;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.UnaryProcedure;
import org.apache.commons.functor.generator.Generator;

/**
 * Tests whether a {@link Generator} contains an element that matches a {@link UnaryPredicate}.
 *
 * @version $Revision$ $Date$
 */
public final class GeneratorContains<T> implements BinaryPredicate<Generator<? extends T>, UnaryPredicate<? super T>>,
        Serializable {
    private static final GeneratorContains<Object> INSTANCE = new GeneratorContains<Object>();

    /**
     * Helper procedure.
     */
    private class ContainsProcedure implements UnaryProcedure<T> {
        private boolean found;
        private UnaryPredicate<? super T> pred;

        /**
         * Create a new ContainsProcedure.
         * @pred test
         */
        public ContainsProcedure(UnaryPredicate<? super T> pred) {
            this.pred = pred;
        }

        /**
         * {@inheritDoc}
         */
        public void run(T obj) {
            found |= pred.test(obj);
        }
    }

    /**
     * {@inheritDoc}
     * @param left Generator
     * @param right UnaryPredicate
     */
    public boolean test(Generator<? extends T> left, UnaryPredicate<? super T> right) {
        ContainsProcedure findProcedure = new ContainsProcedure(right);
        left.run(findProcedure);
        return findProcedure.found;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        return obj == this || obj != null && obj.getClass().equals(getClass());
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return System.identityHashCode(INSTANCE);
    }

    /**
     * Get a static {@link GeneratorContains} instance.
     * @return {@link GeneratorContains}
     */
    public static GeneratorContains<Object> instance() {
        return INSTANCE;
    }
}
