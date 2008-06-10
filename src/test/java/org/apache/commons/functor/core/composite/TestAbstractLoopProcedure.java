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
import junit.framework.TestSuite;

import org.apache.commons.functor.BaseFunctorTest;
import org.apache.commons.functor.Predicate;
import org.apache.commons.functor.Procedure;
import org.apache.commons.functor.core.Constant;
import org.apache.commons.functor.core.NoOp;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestAbstractLoopProcedure extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestAbstractLoopProcedure(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestAbstractLoopProcedure.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
		return new MockLoopProcedure(Constant.FALSE, NoOp.INSTANCE);
    }

	// tests
	// ------------------------------------------------------------------------
	public void testEqualsAndHashCodeWithNullArgs() {
		Procedure p = new MockLoopProcedure(null,null);
		assertNotNull(p.toString());
		assertFalse(p.equals(null));
		assertTrue(p.equals(p));
		assertEquals(p.hashCode(),p.hashCode());
	}

}

class MockLoopProcedure extends AbstractLoopProcedure {
	public MockLoopProcedure(Predicate condition, Procedure action) {
		super(condition,action);
	}

	public void run() {
	}
}
