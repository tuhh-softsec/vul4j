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

import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.UnaryProcedure;

/**
 * Generator that transforms the elements of another Generator.
 *
 * @version $Revision$ $Date$
 */
public class TransformedGenerator<I, E> extends BaseGenerator<E> {
    private UnaryFunction<? super I, ? extends E> func;

    /**
     * Create a new TransformedGenerator.
     * @param wrapped Generator to transform
     * @param func UnaryFunction to apply to each element
     */
    public TransformedGenerator(Generator<? extends I> wrapped, UnaryFunction<? super I, ? extends E> func) {
        super(wrapped);
        if (wrapped == null) {
            throw new IllegalArgumentException("Generator argument was null");
        }
        if (func == null) {
            throw new IllegalArgumentException("UnaryFunction argument was null");
        }
        this.func = func;
    }

    /**
     * {@inheritDoc}
     */
    public void run(final UnaryProcedure<? super E> proc) {
        getWrappedGenerator().run(new UnaryProcedure<I>() {
            public void run(I obj) {
                proc.run(func.evaluate(obj));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Generator<? extends I> getWrappedGenerator() {
        return (Generator<? extends I>) super.getWrappedGenerator();
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof TransformedGenerator<?, ?> == false) {
            return false;
        }
        TransformedGenerator<?, ?> other = (TransformedGenerator<?, ?>) obj;
        return other.getWrappedGenerator().equals(getWrappedGenerator()) && other.func == func;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int result = "TransformedGenerator".hashCode();
        result <<= 2;
        result ^= getWrappedGenerator().hashCode();
        result <<= 2;
        result ^= func.hashCode();
        return result;
    }
}
