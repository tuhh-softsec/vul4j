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
    public Composite() { }

    // ------------------------------------------------------------------------

    public static final CompositeUnaryProcedure procedure(UnaryProcedure p, UnaryFunction f) {
        return new CompositeUnaryProcedure(p,f);
    }

    public static final CompositeUnaryPredicate predicate(UnaryPredicate p, UnaryFunction f) {
        return new CompositeUnaryPredicate(p,f);
    }

    public static final BinaryPredicate predicate(BinaryPredicate p, UnaryFunction f, UnaryFunction g) {
        return new UnaryCompositeBinaryPredicate(p,f,g);
    }

    public static final CompositeUnaryFunction function(UnaryFunction f, UnaryFunction g) {
        return new CompositeUnaryFunction(f,g);
    }

    public static final BinaryFunction function(BinaryFunction f, UnaryFunction g, UnaryFunction h) {
        return new UnaryCompositeBinaryFunction(f,g,h);
    }

    public static final BinaryFunction function(BinaryFunction f, BinaryFunction g, BinaryFunction h) {
        return new BinaryCompositeBinaryFunction(f,g,h);
    }
}
