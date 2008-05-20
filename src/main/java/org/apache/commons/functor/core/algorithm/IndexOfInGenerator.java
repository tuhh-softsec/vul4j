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

import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.UnaryProcedure;
import org.apache.commons.functor.generator.Generator;

/**
 * Return the index of the first Object in a {@link Generator} matching a {@link UnaryPredicate}, or -1 if not found.
 *
 * @version $Revision$ $Date$
 */
public final class IndexOfInGenerator implements BinaryFunction, Serializable {
    private static final IndexOfInGenerator INSTANCE = new IndexOfInGenerator();

    /**
     * Helper procedure.
     */
    private class IndexProcedure implements UnaryProcedure {
        private int index = -1;
        private int current = 0;
        private UnaryPredicate pred;

        /**
         * Create a new IndexProcedure.
         * @pred test
         */
        public IndexProcedure(UnaryPredicate pred) {
            this.pred = pred;
        }

        /**
         * {@inheritDoc}
         */
        public void run(Object obj) {
            if (index < 0 && pred.test(obj)) {
                index = current;
            }
            current++;
        }
    }

    /**
     * {@inheritDoc}
     * @param left Generator
     * @param right UnaryPredicate
     */
    public Object evaluate(Object left, Object right) {
        IndexProcedure findProcedure = new IndexProcedure((UnaryPredicate) right);
        ((Generator) left).run(findProcedure);
        return new Integer(findProcedure.index);
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
     * Get a static {@link IndexOfInGenerator} instance.
     * @return {@link IndexOfInGenerator}
     */
    public static IndexOfInGenerator instance() {
        return INSTANCE;
    }
}
