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
package org.apache.commons.functor.core;

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

        suite.addTest(TestNoOp.suite());
        suite.addTest(TestConstant.suite());
        suite.addTest(TestIdentity.suite());
        suite.addTest(TestLeftIdentity.suite());
        suite.addTest(TestRightIdentity.suite());

        suite.addTest(TestIsInstance.suite());
        suite.addTest(TestIsNull.suite());
        suite.addTest(TestIsNotNull.suite());
        suite.addTest(TestIsEqual.suite());
        suite.addTest(TestIsNotEqual.suite());
        suite.addTest(TestIsSame.suite());
        suite.addTest(TestIsNotSame.suite());

        suite.addTest(TestLimit.suite());
        suite.addTest(TestOffset.suite());

        suite.addTest(org.apache.commons.functor.core.composite.TestAll.suite());
        suite.addTest(org.apache.commons.functor.core.collection.TestAll.suite());
        suite.addTest(org.apache.commons.functor.core.comparator.TestAll.suite());

        return suite;
    }
}
