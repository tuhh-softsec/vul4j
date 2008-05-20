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
import java.util.Stack;

import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.UnaryProcedure;
import org.apache.commons.functor.generator.Generator;

/**
 * Functional right-fold algorithm against the elements of a {@link Generator}.
 * Uses the seed object (if supplied) as the initial right-side argument to the {@link BinaryFunction},
 * then uses the result of that evaluation as the next right-side argument, until the {@link Generator}'s
 * elements have been expended.
 * @version $Revision$ $Date$
 */
public class FoldRight implements UnaryFunction, BinaryFunction, Serializable {

    private BinaryFunction func;

    /**
     * Create a new FoldLeft.
     * @param func {@link BinaryFunction} to apply to each (seed, next)
     */
    public FoldRight(BinaryFunction func) {
        this.func = func;
    }

    /**
     * {@inheritDoc}
     * @param obj {@link Generator} to transform
     */
    public Object evaluate(Object obj) {
        FoldRightHelper helper = new FoldRightHelper(func);
        ((Generator) obj).run(helper);
        return helper.getResult();
    }

    /**
     * {@inheritDoc}
     * @param left {@link Generator} to transform
     * @param right seed object
     */
    public Object evaluate(Object left, Object right) {
        FoldRightHelper helper = new FoldRightHelper(func, right);
        ((Generator) left).run(helper);
        return helper.getResult();
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof FoldRight == false) {
            return false;
        }
        return ((FoldRight) obj).func.equals(func);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return "FoldRight".hashCode() << 2 ^ func.hashCode();
    }

    /**
     * Helper class
     */
    private static class FoldRightHelper implements UnaryProcedure {
        private BinaryFunction function;
        private Object seed;
        private Stack stk = new Stack();
        private boolean hasSeed;

        /**
         * Create a seedless FoldRightHelper.
         * @param function to apply
         */
        public FoldRightHelper(BinaryFunction function) {
            this.function = function;
        }

        /**
         * Create a new FoldRightHelper.
         * @param function to apply
         * @param seed initial left argument
         */
        FoldRightHelper(BinaryFunction function, Object seed) {
            this(function);
            this.seed = seed;
            hasSeed = true;
        }

        /**
         * {@inheritDoc}
         */
        public void run(Object obj) {
            stk.push(obj);
        }

        /**
         * Get result after processing.
         * Get current result.
         * @return Object
         */
        Object getResult() {
            Object right = seed;
            if (!hasSeed) {
                if (stk.isEmpty()) {
                    return null;
                }
                right = stk.pop();
            }
            while (!stk.isEmpty()) {
                right = function.evaluate(stk.pop(), right);
            }
            return right;
        }

    }
}
