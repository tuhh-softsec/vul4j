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
     * @return UnaryProcedure
     */
    public static final UnaryProcedure procedure(UnaryPredicate q, UnaryProcedure r, UnaryProcedure s) {
        return new ConditionalUnaryProcedure(q, r, s);
    }

    /**
     * Create a conditional function.
     * @param q if
     * @param r then
     * @param s else
     * @return UnaryFunction
     */
    public static final UnaryFunction function(UnaryPredicate q, UnaryFunction r, UnaryFunction s) {
        return new ConditionalUnaryFunction(q, r, s);
    }

    /**
     * Create a conditional predicate.
     * @param q if
     * @param r then
     * @param s else
     * @return UnaryPredicate
     */
    public static final UnaryPredicate predicate(UnaryPredicate q, UnaryPredicate r, UnaryPredicate s) {
        return new ConditionalUnaryPredicate(q, r, s);
    }

    /**
     * Create a conditional binary procedure.
     * @param q if
     * @param r then
     * @param s else
     * @return BinaryProcedure
     */
    public static final BinaryProcedure procedure(BinaryPredicate q, BinaryProcedure r, BinaryProcedure s) {
        return new ConditionalBinaryProcedure(q, r, s);
    }

    /**
     * Create a conditional binary function.
     * @param q if
     * @param r then
     * @param s else
     * @return BinaryFunction
     */
    public static final BinaryFunction function(BinaryPredicate q, BinaryFunction r, BinaryFunction s) {
        return new ConditionalBinaryFunction(q, r, s);
    }

    /**
     * Create a conditional binary predicate.
     * @param q if
     * @param r then
     * @param s else
     * @return BinaryPredicate
     */
    public static final BinaryPredicate predicate(BinaryPredicate q, BinaryPredicate r, BinaryPredicate s) {
        return new ConditionalBinaryPredicate(q, r, s);
    }

}
