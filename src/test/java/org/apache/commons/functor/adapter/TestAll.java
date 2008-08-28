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
package org.apache.commons.functor.adapter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestAll extends TestCase {
    public TestAll(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();

        suite.addTest(TestFunctionProcedure.suite());
        suite.addTest(TestUnaryFunctionUnaryProcedure.suite());
        suite.addTest(TestBinaryFunctionBinaryProcedure.suite());

        suite.addTest(TestProcedureFunction.suite());
        suite.addTest(TestUnaryProcedureUnaryFunction.suite());
        suite.addTest(TestBinaryProcedureBinaryFunction.suite());
        suite.addTest(TestBinaryFunctionUnaryFunction.suite());

        suite.addTest(TestFunctionPredicate.suite());
        suite.addTest(TestUnaryFunctionUnaryPredicate.suite());
        suite.addTest(TestBinaryFunctionBinaryPredicate.suite());

        suite.addTest(TestPredicateFunction.suite());
        suite.addTest(TestUnaryPredicateUnaryFunction.suite());
        suite.addTest(TestBinaryPredicateBinaryFunction.suite());

        suite.addTest(TestFunctionUnaryFunction.suite());
        suite.addTest(TestIgnoreRightFunction.suite());
        suite.addTest(TestIgnoreLeftFunction.suite());

        suite.addTest(TestPredicateUnaryPredicate.suite());
        suite.addTest(TestIgnoreRightPredicate.suite());
        suite.addTest(TestIgnoreLeftPredicate.suite());

        suite.addTest(TestProcedureUnaryProcedure.suite());
        suite.addTest(TestIgnoreRightProcedure.suite());
        suite.addTest(TestIgnoreLeftProcedure.suite());

        suite.addTest(TestBoundFunction.suite());
        suite.addTest(TestLeftBoundFunction.suite());
        suite.addTest(TestRightBoundFunction.suite());
        suite.addTest(TestFullyBoundFunction.suite());

        suite.addTest(TestBoundPredicate.suite());
        suite.addTest(TestLeftBoundPredicate.suite());
        suite.addTest(TestRightBoundPredicate.suite());
        suite.addTest(TestFullyBoundPredicate.suite());

        suite.addTest(TestBoundProcedure.suite());
        suite.addTest(TestLeftBoundProcedure.suite());
        suite.addTest(TestRightBoundProcedure.suite());
        suite.addTest(TestFullyBoundProcedure.suite());

        return suite;
    }
}
