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
package org.apache.commons.functor.generator;

import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.UnaryProcedure;

/**
 * Wrap another {@link Generator} such that {@link #run(UnaryProcedure)} continues
 * as long as a condition is true (test before).
 *
 * @version $Revision$ $Date$
 */
public class WhileGenerate<E> extends BaseGenerator<E> {
    private UnaryPredicate<? super E> test;

    /**
     * Create a new WhileGenerate.
     * @param test {@link UnaryPredicate}
     * @param wrapped {@link Generator}
     */
    public WhileGenerate(UnaryPredicate<? super E> test, Generator<? extends E> wrapped) {
        super(wrapped);
        if (wrapped == null) {
            throw new IllegalArgumentException("Generator argument was null");
        }
        if (test == null) {
            throw new IllegalArgumentException("UnaryPredicate argument was null");
        }
        this.test = test;
    }

    /**
     * {@inheritDoc}
     */
    public void run(final UnaryProcedure<? super E> proc) {
        getWrappedGenerator().run(new UnaryProcedure<E>() {
            public void run(E obj) {
                if (!test.test(obj)) {
                    getWrappedGenerator().stop();
                } else {
                    proc.run(obj);
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Generator<? extends E> getWrappedGenerator() {
        return (Generator<? extends E>) super.getWrappedGenerator();
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof WhileGenerate<?> == false) {
            return false;
        }
        WhileGenerate<?> other = (WhileGenerate<?>) obj;
        return other.getWrappedGenerator().equals(getWrappedGenerator()) && other.test.equals(test);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int result = "WhileGenerate".hashCode();
        result <<= 2;
        Generator<?> gen = getWrappedGenerator();
        result ^= gen == null ? 0 : gen.hashCode();
        result <<= 2;
        result ^= test == null ? 0 : test.hashCode();
        return result;
    }
}
