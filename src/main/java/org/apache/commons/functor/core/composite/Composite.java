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
import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.UnaryProcedure;

/**
 * Utility methods for creating composite functors.
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public final class Composite {
    // constructor - for beanish apis
    // ------------------------------------------------------------------------
    /**
     * Create a new Composite.
     */
    public Composite() { }

    /**
     * Create a composite UnaryProcedure.
     * @param p UnaryProcedure to execute against output of <code>f</code>
     * @param f UnaryFunction to apply
     * @return UnaryProcedure
     */
    public static final UnaryProcedure procedure(UnaryProcedure p, UnaryFunction f) {
        return new CompositeUnaryProcedure(p, f);
    }

    /**
     * Create a composite UnaryPredicate.
     * @param p UnaryPredicate to test the output of <code>f</code>
     * @param f UnaryFunction to apply
     * @return UnaryPredicate
     */
    public static final UnaryPredicate predicate(UnaryPredicate p, UnaryFunction f) {
        return new CompositeUnaryPredicate(p, f);
    }

    /**
     * Create a composite BinaryPredicate.
     * @param p BinaryPredicate to test <i>output(</i><code>f</code><i>), output(</i><code>g</code><i>)</i>
     * @param f left UnaryFunction
     * @param g right UnaryFunction
     * @return BinaryPredicate
     */
    public static final BinaryPredicate predicate(BinaryPredicate p, UnaryFunction f, UnaryFunction g) {
        return new UnaryCompositeBinaryPredicate(p, f, g);
    }

    /**
     * Create a composite UnaryFunction.
     * @param f UnaryFunction to apply to the output of <code>g</code>
     * @param g UnaryFunction to apply first
     * @return UnaryFunction
     */
    public static final UnaryFunction function(UnaryFunction f, UnaryFunction g) {
        return new CompositeUnaryFunction(f, g);
    }

    /**
     * Create a composite<UnaryFunction> BinaryFunction.
     * @param f BinaryFunction to apply to <i>output(</i><code>f</code><i>), output(</i><code>g</code><i>)</i>
     * @param g left UnaryFunction
     * @param h right UnaryFunction
     * @return BinaryFunction
     */
    public static final BinaryFunction function(BinaryFunction f, UnaryFunction g, UnaryFunction h) {
        return new UnaryCompositeBinaryFunction(f, g, h);
    }

    /**
     * Create a composite<BinaryFunction> BinaryFunction.
     * @param f BinaryFunction to apply to <i>output(</i><code>f</code><i>), output(</i><code>g</code><i>)</i>
     * @param g left BinaryFunction
     * @param h right BinaryFunction
     * @return BinaryFunction
     */
    public static final BinaryFunction function(BinaryFunction f, BinaryFunction g, BinaryFunction h) {
        return new BinaryCompositeBinaryFunction(f, g, h);
    }
}
