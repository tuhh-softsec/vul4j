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
package org.apache.commons.functor.core;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.functor.BaseFunctorTest;
import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.BinaryPredicate;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestRightIdentity extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestRightIdentity(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestRightIdentity.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new RightIdentity();
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
        BinaryFunction f = new RightIdentity();
        assertNull(f.evaluate(null,null));
        assertNull(f.evaluate("xyzzy",null));
        assertEquals("xyzzy",f.evaluate("abcdefg","xyzzy"));
        assertEquals("xyzzy",f.evaluate(null,"xyzzy"));
        assertEquals(new Integer(3),f.evaluate(null,new Integer(3)));
        Object obj = new Long(12345L);
        assertSame(obj,f.evaluate(null,obj));
        assertSame(obj,f.evaluate(obj,obj));
    }
    
    public void testTest() throws Exception {
        BinaryPredicate p = new RightIdentity();
        assertTrue(p.test(null,Boolean.TRUE));
        assertTrue(!p.test(null,Boolean.FALSE));
        try {
            p.test(null,"true");
            fail("Expected ClassCastException");
        } catch(ClassCastException e) {
            // expected
        }
        try {
            p.test(null,null);
            fail("Expected NullPointerException");
        } catch(NullPointerException e) {
            // expected
        }
    }
    
    public void testEquals() throws Exception {
        BinaryFunction f = new RightIdentity();
        assertEquals(f,f);
        assertObjectsAreEqual(f,new RightIdentity());
        assertObjectsAreEqual(f,RightIdentity.instance());
        assertObjectsAreNotEqual(f,new Identity());
        assertObjectsAreNotEqual(f,new LeftIdentity());
        assertObjectsAreNotEqual(f,new Constant(true));
        assertObjectsAreNotEqual(f,new Constant("abcde"));
    }
    
    public void testConstant() throws Exception {
        assertEquals(RightIdentity.instance(),RightIdentity.instance());
        assertSame(RightIdentity.instance(),RightIdentity.instance());
    }
}
