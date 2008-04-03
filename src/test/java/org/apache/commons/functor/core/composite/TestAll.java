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

        suite.addTest(TestAnd.suite());
        suite.addTest(TestUnaryAnd.suite());
        suite.addTest(TestBinaryAnd.suite());

        suite.addTest(TestOr.suite());
        suite.addTest(TestUnaryOr.suite());
        suite.addTest(TestBinaryOr.suite());

        suite.addTest(TestNot.suite());
        suite.addTest(TestUnaryNot.suite());
        suite.addTest(TestBinaryNot.suite());

        suite.addTest(TestSequence.suite());
        suite.addTest(TestUnarySequence.suite());
        suite.addTest(TestBinarySequence.suite());

        suite.addTest(TestComposite.suite());

        suite.addTest(TestCompositeUnaryFunction.suite());
        suite.addTest(TestCompositeUnaryPredicate.suite());
        suite.addTest(TestCompositeUnaryProcedure.suite());
        suite.addTest(TestUnaryCompositeBinaryFunction.suite());
        suite.addTest(TestUnaryCompositeBinaryPredicate.suite());
        suite.addTest(TestBinaryCompositeBinaryFunction.suite());

        suite.addTest(TestTransposedFunction.suite());
        suite.addTest(TestTransposedPredicate.suite());
        suite.addTest(TestTransposedProcedure.suite());

        suite.addTest(TestConditional.suite());

        suite.addTest(TestConditionalPredicate.suite());
        suite.addTest(TestConditionalUnaryPredicate.suite());
        suite.addTest(TestConditionalBinaryPredicate.suite());

        suite.addTest(TestConditionalFunction.suite());
        suite.addTest(TestConditionalUnaryFunction.suite());
        suite.addTest(TestConditionalBinaryFunction.suite());

        suite.addTest(TestConditionalProcedure.suite());
        suite.addTest(TestConditionalUnaryProcedure.suite());
        suite.addTest(TestConditionalBinaryProcedure.suite());

		suite.addTest(TestAbstractLoopProcedure.suite());
		suite.addTest(TestWhileDoProcedure.suite());
		suite.addTest(TestDoWhileProcedure.suite());

        return suite;
    }
}
