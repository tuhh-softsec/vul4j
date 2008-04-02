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
import org.apache.commons.functor.core.Constant;
import org.apache.commons.functor.core.Identity;
import org.apache.commons.functor.core.NoOp;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestCompositeUnaryProcedure extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestCompositeUnaryProcedure(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestCompositeUnaryProcedure.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new CompositeUnaryProcedure(new NoOp(),new Constant(true));
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
    
    public void testRun() throws Exception {
        new CompositeUnaryProcedure(new NoOp(),new Identity()).run(null);
    }
    
    public void testNullNotAllowed() throws Exception {
        try {
            new CompositeUnaryProcedure(null);
            fail("Expected NullPointerException");
        } catch(NullPointerException e) {
            // expected
        }
        try {
            new CompositeUnaryProcedure(null,null);
            fail("Expected NullPointerException");
        } catch(NullPointerException e) {
            // expected
        }
        try {
            new CompositeUnaryProcedure(NoOp.instance(),null);
            fail("Expected NullPointerException");
        } catch(NullPointerException e) {
            // expected
        }
    }
    public void testOf() throws Exception {
        new CompositeUnaryProcedure(new NoOp()).of(new Identity()).run(null);
    }
    
    public void testEquals() throws Exception {
        CompositeUnaryProcedure f = new CompositeUnaryProcedure(new NoOp());
        assertEquals(f,f);
        CompositeUnaryProcedure g = new CompositeUnaryProcedure(new NoOp());
        assertObjectsAreEqual(f,g);

        for(int i=0;i<3;i++) {
            f.of(new Constant("x"));
            assertObjectsAreNotEqual(f,g);
            g.of(new Constant("x"));
            assertObjectsAreEqual(f,g);
            f.of(new CompositeUnaryFunction(new Constant("y"),new Constant("z")));
            assertObjectsAreNotEqual(f,g);            
            g.of(new CompositeUnaryFunction(new Constant("y"),new Constant("z")));
            assertObjectsAreEqual(f,g);            
        }
                
        assertObjectsAreNotEqual(f,new Constant(false));
    }

}
