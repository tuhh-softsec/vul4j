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
import junit.framework.TestSuite;

import org.apache.commons.functor.BaseFunctorTest;
import org.apache.commons.functor.Function;
import org.apache.commons.functor.core.Constant;
import org.apache.commons.functor.core.Identity;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestBoundFunction extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestBoundFunction(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestBoundFunction.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new BoundFunction(new Identity(),"xyzzy");
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
        Function f = new BoundFunction(new Identity(),"xyzzy");
        assertEquals("xyzzy",f.evaluate());
    }

    public void testEquals() throws Exception {
        Function f = new BoundFunction(new Identity(),"xyzzy");
        assertEquals(f,f);
        assertObjectsAreEqual(f,new BoundFunction(new Identity(),"xyzzy"));
        assertObjectsAreNotEqual(f,new Constant("xyzzy"));
        assertObjectsAreNotEqual(f,new BoundFunction(new Identity(),"foo"));
        assertObjectsAreNotEqual(f,new BoundFunction(new Constant("xyzzy"),"foo"));
        assertObjectsAreNotEqual(f,new BoundFunction(null,"xyzzy"));
        assertObjectsAreNotEqual(f,new BoundFunction(new Identity(),null));
        assertObjectsAreEqual(new BoundFunction(null,null),new BoundFunction(null,null));
    }

    public void testAdaptNull() throws Exception {
        assertNull(BoundFunction.bind(null,"xyzzy"));
    }

    public void testAdapt() throws Exception {
        assertNotNull(BoundFunction.bind(new Identity(),"xyzzy"));
        assertNotNull(BoundFunction.bind(new Identity(),null));
    }
}
