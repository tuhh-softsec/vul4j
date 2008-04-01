/*
 * Copyright 2003,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import junit.framework.TestSuite;

import org.apache.commons.functor.BaseFunctorTest;
import org.apache.commons.functor.UnaryProcedure;
import org.apache.commons.functor.core.Constant;
import org.apache.commons.functor.core.NoOp;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestProcedureUnaryProcedure extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestProcedureUnaryProcedure(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestProcedureUnaryProcedure.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new ProcedureUnaryProcedure(new NoOp());
    }

    // Lifecycle
    // ------------------------------------------------------------------------

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    // Tests
    // ------------------------------------------------------------------------    

    public void testEvaluate() throws Exception {
        UnaryProcedure p = new ProcedureUnaryProcedure(new FunctionProcedure(new Constant(null)));
        p.run(Boolean.TRUE);
    }
    
    public void testEquals() throws Exception {
        UnaryProcedure p = new ProcedureUnaryProcedure(new NoOp());
        assertEquals(p,p);
        assertObjectsAreEqual(p,new ProcedureUnaryProcedure(new NoOp()));
        assertObjectsAreNotEqual(p,new NoOp());
        assertObjectsAreNotEqual(p,new ProcedureUnaryProcedure(null));
        assertObjectsAreEqual(new ProcedureUnaryProcedure(null),new ProcedureUnaryProcedure(null));
    }

    public void testAdaptNull() throws Exception {
        assertNull(ProcedureUnaryProcedure.adapt(null));
    }

    public void testAdapt() throws Exception {
        assertNotNull(ProcedureUnaryProcedure.adapt(new NoOp()));
    }
}
