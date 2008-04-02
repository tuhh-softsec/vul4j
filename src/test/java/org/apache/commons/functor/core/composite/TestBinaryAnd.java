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
import org.apache.commons.functor.BinaryPredicate;
import org.apache.commons.functor.core.Constant;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestBinaryAnd extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestBinaryAnd(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestBinaryAnd.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new BinaryAnd(new Constant(true),new Constant(true));
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
        assertTrue((new BinaryAnd()).test("xyzzy",new Integer(3)));
        assertTrue((new BinaryAnd(new Constant(true))).test("xyzzy",new Integer(3)));
        assertTrue((new BinaryAnd(new Constant(true),new Constant(true))).test("xyzzy",new Integer(3)));
        assertTrue((new BinaryAnd(new Constant(true),new Constant(true),new Constant(true))).test("xyzzy",new Integer(3)));
        
        BinaryAnd p = new BinaryAnd(new Constant(true));
        assertTrue(p.test("xyzzy",new Integer(3)));        
        for(int i=0;i<10;i++) {
            p.and(new Constant(true));
            assertTrue(p.test("xyzzy",new Integer(3)));        
        }
        
        BinaryAnd q = new BinaryAnd(new Constant(true));
        assertTrue(q.test("xyzzy",new Integer(3)));        
        for(int i=0;i<10;i++) {
            q.and(new Constant(true));
            assertTrue(q.test("xyzzy",new Integer(3)));        
        }
        
        BinaryAnd r = new BinaryAnd(p,q);
        assertTrue(r.test("xyzzy",new Integer(3)));        
    }
    
    public void testFalse() throws Exception {
        assertTrue(!(new BinaryAnd(new Constant(false))).test("xyzzy",new Integer(3)));
        assertTrue(!(new BinaryAnd(new Constant(true),new Constant(false))).test("xyzzy",new Integer(3)));
        assertTrue(!(new BinaryAnd(new Constant(true),new Constant(true),new Constant(false))).test("xyzzy",new Integer(3)));
        
        BinaryAnd p = new BinaryAnd(new Constant(false));
        assertTrue(!p.test("xyzzy",new Integer(3)));        
        for(int i=0;i<10;i++) {
            p.and(new Constant(false));
            assertTrue(!p.test("xyzzy",new Integer(3)));        
        }
        
        BinaryAnd q = new BinaryAnd(new Constant(true));
        assertTrue(q.test("xyzzy",new Integer(3)));        
        for(int i=0;i<10;i++) {
            q.and(new Constant(true));
            assertTrue(q.test("xyzzy",new Integer(3)));        
        }
        
        BinaryAnd r = new BinaryAnd(p,q);
        assertTrue(!r.test("xyzzy",new Integer(3)));        
    }
        
    public void testDuplicateAdd() throws Exception {
        BinaryPredicate p = new Constant(true);
        BinaryAnd q = new BinaryAnd(p,p);
        assertTrue(q.test("xyzzy",new Integer(3)));
        for(int i=0;i<10;i++) {
            q.and(p);
            assertTrue(q.test("xyzzy",new Integer(3)));        
        }
    }
        
    public void testEquals() throws Exception {
        BinaryAnd p = new BinaryAnd();
        assertEquals(p,p);
        BinaryAnd q = new BinaryAnd();
        assertObjectsAreEqual(p,q);
        BinaryOr r = new BinaryOr();
        assertObjectsAreNotEqual(p,r);

        for(int i=0;i<3;i++) {
            p.and(Constant.truePredicate());
            assertObjectsAreNotEqual(p,q);
            q.and(Constant.truePredicate());
            assertObjectsAreEqual(p,q);
            p.and(new BinaryAnd(Constant.truePredicate(),Constant.falsePredicate()));
            assertObjectsAreNotEqual(p,q);            
            q.and(new BinaryAnd(Constant.truePredicate(),Constant.falsePredicate()));
            assertObjectsAreEqual(p,q);            
        }
        
        assertObjectsAreNotEqual(p,Constant.truePredicate());
    }

}
