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
package org.apache.commons.functor.core.composite;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.functor.BaseFunctorTest;
import org.apache.commons.functor.BinaryPredicate;
import org.apache.commons.functor.core.Constant;
import org.apache.commons.functor.core.Identity;
import org.apache.commons.functor.core.LeftIdentity;
import org.apache.commons.functor.core.RightIdentity;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestUnaryCompositeBinaryPredicate extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestUnaryCompositeBinaryPredicate(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestUnaryCompositeBinaryPredicate.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new UnaryCompositeBinaryPredicate(
            new RightIdentity(),
            new Constant(Boolean.FALSE),
            new Identity());
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
        BinaryPredicate f = new UnaryCompositeBinaryPredicate(
            new RightIdentity(),
            new Constant(Boolean.FALSE),
            new Identity());
        assertEquals(true,f.test(Boolean.TRUE,Boolean.TRUE));
        assertEquals(true,f.test(null,Boolean.TRUE));
    }
    
    public void testEquals() throws Exception {
        BinaryPredicate f = new UnaryCompositeBinaryPredicate(
            new LeftIdentity(),
            new Constant(true),
            new Constant(false));
        assertEquals(f,f);
        assertObjectsAreEqual(f,new UnaryCompositeBinaryPredicate(
            new LeftIdentity(),
            new Constant(true),
            new Constant(false)));
        assertObjectsAreNotEqual(f,new UnaryCompositeBinaryPredicate(
            new RightIdentity(),
            new Constant(true),
            new Constant(false)));
        assertObjectsAreNotEqual(f,new UnaryCompositeBinaryPredicate(
            new LeftIdentity(),
            new Identity(),
            new Constant(true)));
        assertObjectsAreNotEqual(f,new UnaryCompositeBinaryPredicate(null,null,null));
        assertObjectsAreEqual(
            new UnaryCompositeBinaryPredicate(null,null,null),
            new UnaryCompositeBinaryPredicate(null,null,null));
    }

}
