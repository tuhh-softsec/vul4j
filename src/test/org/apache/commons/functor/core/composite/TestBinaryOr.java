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

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestBinaryOr extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestBinaryOr(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestBinaryOr.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new BinaryOr(new Constant(false),new Constant(true));
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
    
    public void testTrue() throws Exception {
        assertTrue((new BinaryOr(new Constant(true))).test("xyzzy",new Integer(3)));
        assertTrue((new BinaryOr(new Constant(false),new Constant(true))).test("xyzzy",new Integer(3)));
        assertTrue((new BinaryOr(new Constant(false),new Constant(false),new Constant(true))).test("xyzzy",new Integer(3)));
        
        BinaryOr p = new BinaryOr(new Constant(true));
        assertTrue(p.test("xyzzy",new Integer(3)));        
        for(int i=0;i<10;i++) {
            p.or(new Constant(i%2==0));
            assertTrue(p.test("xyzzy",new Integer(3)));        
        }
        
        BinaryOr q = new BinaryOr(new Constant(true));
        assertTrue(q.test("xyzzy",new Integer(3)));        
        for(int i=0;i<10;i++) {
            q.or(new Constant(i%2==0));
            assertTrue(q.test("xyzzy",new Integer(3)));        
        }
        
        BinaryOr r = new BinaryOr(p,q);
        assertTrue(r.test("xyzzy",new Integer(3)));        
    }
    
    public void testFalse() throws Exception {
        assertTrue(!(new BinaryOr()).test("xyzzy",new Integer(3)));
        assertTrue(!(new BinaryOr(new Constant(false))).test("xyzzy",new Integer(3)));
        assertTrue(!(new BinaryOr(new Constant(false),new Constant(false))).test("xyzzy",new Integer(3)));
        assertTrue(!(new BinaryOr(new Constant(false),new Constant(false),new Constant(false))).test("xyzzy",new Integer(3)));
        
        BinaryOr p = new BinaryOr(new Constant(false));
        assertTrue(!p.test("xyzzy",new Integer(3)));        
        for(int i=0;i<10;i++) {
            p.or(new Constant(false));
            assertTrue(!p.test("xyzzy",new Integer(3)));        
        }
        
        BinaryOr q = new BinaryOr(new Constant(false));
        assertTrue(!q.test("xyzzy",new Integer(3)));        
        for(int i=0;i<10;i++) {
            q.or(new Constant(false));
            assertTrue(!q.test("xyzzy",new Integer(3)));        
        }
        
        BinaryOr r = new BinaryOr(p,q);
        assertTrue(!r.test("xyzzy",new Integer(3)));        
    }
        
    public void testDuplicateAdd() throws Exception {
        BinaryPredicate p = new Constant(true);
        BinaryOr q = new BinaryOr(p,p);
        assertTrue(q.test("xyzzy",new Integer(3)));
        for(int i=0;i<10;i++) {
            q.or(p);
            assertTrue(q.test("xyzzy",new Integer(3)));        
        }
    }
        
    public void testEquals() throws Exception {
        BinaryOr p = new BinaryOr();
        assertEquals(p,p);

        BinaryOr q = new BinaryOr();
        assertObjectsAreEqual(p,q);

        BinaryAnd r = new BinaryAnd();
        assertObjectsAreNotEqual(p,r);
        
        for(int i=0;i<3;i++) {
            p.or(Constant.truePredicate());
            assertObjectsAreNotEqual(p,q);
            q.or(Constant.truePredicate());
            assertObjectsAreEqual(p,q);
            r.and(Constant.truePredicate());
            assertObjectsAreNotEqual(p,r);

            p.or(new BinaryOr(Constant.truePredicate(),Constant.falsePredicate()));
            assertObjectsAreNotEqual(p,q);            
            q.or(new BinaryOr(Constant.truePredicate(),Constant.falsePredicate()));
            assertObjectsAreEqual(p,q);            
            r.and(new BinaryOr(Constant.truePredicate(),Constant.falsePredicate()));
            assertObjectsAreNotEqual(p,r);
        }
        
        assertObjectsAreNotEqual(p,Constant.truePredicate());
    }

}
