/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/core/composite/Attic/TestAndUnaryPredicate.java,v 1.1 2003/01/27 19:33:43 rwaldhoff Exp $
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived 
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.commons.functor.core.composite;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.functor.BaseFunctorTest;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.core.ConstantPredicate;

/**
 * @version $Revision: 1.1 $ $Date: 2003/01/27 19:33:43 $
 * @author Rodney Waldhoff
 */
public class TestAndUnaryPredicate extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestAndUnaryPredicate(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestAndUnaryPredicate.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new AndUnaryPredicate(new ConstantPredicate(true),new ConstantPredicate(true));
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
        assertTrue((new AndUnaryPredicate()).test("xyzzy"));
        assertTrue((new AndUnaryPredicate(new ConstantPredicate(true))).test("xyzzy"));
        assertTrue((new AndUnaryPredicate(new ConstantPredicate(true),new ConstantPredicate(true))).test("xyzzy"));
        assertTrue((new AndUnaryPredicate(new ConstantPredicate(true),new ConstantPredicate(true),new ConstantPredicate(true))).test("xyzzy"));
        
        AndUnaryPredicate p = new AndUnaryPredicate(new ConstantPredicate(true));
        assertTrue(p.test("xyzzy"));        
        for(int i=0;i<10;i++) {
            p.and(new ConstantPredicate(true));
            assertTrue(p.test("xyzzy"));        
        }
        
        AndUnaryPredicate q = new AndUnaryPredicate(new ConstantPredicate(true));
        assertTrue(q.test("xyzzy"));        
        for(int i=0;i<10;i++) {
            q.and(new ConstantPredicate(true));
            assertTrue(q.test("xyzzy"));        
        }
        
        AndUnaryPredicate r = new AndUnaryPredicate(p,q);
        assertTrue(r.test("xyzzy"));        
    }
    
    public void testFalse() throws Exception {
        assertTrue(!(new AndUnaryPredicate(new ConstantPredicate(false))).test("xyzzy"));
        assertTrue(!(new AndUnaryPredicate(new ConstantPredicate(true),new ConstantPredicate(false))).test("xyzzy"));
        assertTrue(!(new AndUnaryPredicate(new ConstantPredicate(true),new ConstantPredicate(true),new ConstantPredicate(false))).test("xyzzy"));
        
        AndUnaryPredicate p = new AndUnaryPredicate(new ConstantPredicate(false));
        assertTrue(!p.test("xyzzy"));        
        for(int i=0;i<10;i++) {
            p.and(new ConstantPredicate(false));
            assertTrue(!p.test("xyzzy"));        
        }
        
        AndUnaryPredicate q = new AndUnaryPredicate(new ConstantPredicate(true));
        assertTrue(q.test("xyzzy"));        
        for(int i=0;i<10;i++) {
            q.and(new ConstantPredicate(true));
            assertTrue(q.test("xyzzy"));        
        }
        
        AndUnaryPredicate r = new AndUnaryPredicate(p,q);
        assertTrue(!r.test("xyzzy"));        
    }
        
    public void testDuplicateAdd() throws Exception {
        UnaryPredicate p = new ConstantPredicate(true);
        AndUnaryPredicate q = new AndUnaryPredicate(p,p);
        assertTrue(q.test("xyzzy"));
        for(int i=0;i<10;i++) {
            q.and(p);
            assertTrue(q.test("xyzzy"));        
        }
    }
        
    public void testEquals() throws Exception {
        AndUnaryPredicate p = new AndUnaryPredicate();
        assertEquals(p,p);
        AndUnaryPredicate q = new AndUnaryPredicate();
        assertObjectsAreEqual(p,q);

        for(int i=0;i<3;i++) {
            p.and(ConstantPredicate.getTruePredicate());
            assertObjectsAreNotEqual(p,q);
            q.and(ConstantPredicate.getTruePredicate());
            assertObjectsAreEqual(p,q);
            p.and(new AndUnaryPredicate(ConstantPredicate.getTruePredicate(),ConstantPredicate.getFalsePredicate()));
            assertObjectsAreNotEqual(p,q);            
            q.and(new AndUnaryPredicate(ConstantPredicate.getTruePredicate(),ConstantPredicate.getFalsePredicate()));
            assertObjectsAreEqual(p,q);            
        }
        
        assertObjectsAreNotEqual(p,ConstantPredicate.getTruePredicate());
    }

}
