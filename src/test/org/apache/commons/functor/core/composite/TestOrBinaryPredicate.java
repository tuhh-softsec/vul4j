/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/core/composite/Attic/TestOrBinaryPredicate.java,v 1.1 2003/01/27 19:33:43 rwaldhoff Exp $
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
import org.apache.commons.functor.BinaryPredicate;
import org.apache.commons.functor.core.ConstantPredicate;

/**
 * @version $Revision: 1.1 $ $Date: 2003/01/27 19:33:43 $
 * @author Rodney Waldhoff
 */
public class TestOrBinaryPredicate extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestOrBinaryPredicate(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestOrBinaryPredicate.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new OrBinaryPredicate(new ConstantPredicate(false),new ConstantPredicate(true));
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
        assertTrue((new OrBinaryPredicate(new ConstantPredicate(true))).test("xyzzy",new Integer(3)));
        assertTrue((new OrBinaryPredicate(new ConstantPredicate(false),new ConstantPredicate(true))).test("xyzzy",new Integer(3)));
        assertTrue((new OrBinaryPredicate(new ConstantPredicate(false),new ConstantPredicate(false),new ConstantPredicate(true))).test("xyzzy",new Integer(3)));
        
        OrBinaryPredicate p = new OrBinaryPredicate(new ConstantPredicate(true));
        assertTrue(p.test("xyzzy",new Integer(3)));        
        for(int i=0;i<10;i++) {
            p.or(new ConstantPredicate(i%2==0));
            assertTrue(p.test("xyzzy",new Integer(3)));        
        }
        
        OrBinaryPredicate q = new OrBinaryPredicate(new ConstantPredicate(true));
        assertTrue(q.test("xyzzy",new Integer(3)));        
        for(int i=0;i<10;i++) {
            q.or(new ConstantPredicate(i%2==0));
            assertTrue(q.test("xyzzy",new Integer(3)));        
        }
        
        OrBinaryPredicate r = new OrBinaryPredicate(p,q);
        assertTrue(r.test("xyzzy",new Integer(3)));        
    }
    
    public void testFalse() throws Exception {
        assertTrue(!(new OrBinaryPredicate()).test("xyzzy",new Integer(3)));
        assertTrue(!(new OrBinaryPredicate(new ConstantPredicate(false))).test("xyzzy",new Integer(3)));
        assertTrue(!(new OrBinaryPredicate(new ConstantPredicate(false),new ConstantPredicate(false))).test("xyzzy",new Integer(3)));
        assertTrue(!(new OrBinaryPredicate(new ConstantPredicate(false),new ConstantPredicate(false),new ConstantPredicate(false))).test("xyzzy",new Integer(3)));
        
        OrBinaryPredicate p = new OrBinaryPredicate(new ConstantPredicate(false));
        assertTrue(!p.test("xyzzy",new Integer(3)));        
        for(int i=0;i<10;i++) {
            p.or(new ConstantPredicate(false));
            assertTrue(!p.test("xyzzy",new Integer(3)));        
        }
        
        OrBinaryPredicate q = new OrBinaryPredicate(new ConstantPredicate(false));
        assertTrue(!q.test("xyzzy",new Integer(3)));        
        for(int i=0;i<10;i++) {
            q.or(new ConstantPredicate(false));
            assertTrue(!q.test("xyzzy",new Integer(3)));        
        }
        
        OrBinaryPredicate r = new OrBinaryPredicate(p,q);
        assertTrue(!r.test("xyzzy",new Integer(3)));        
    }
        
    public void testDuplicateAdd() throws Exception {
        BinaryPredicate p = new ConstantPredicate(true);
        OrBinaryPredicate q = new OrBinaryPredicate(p,p);
        assertTrue(q.test("xyzzy",new Integer(3)));
        for(int i=0;i<10;i++) {
            q.or(p);
            assertTrue(q.test("xyzzy",new Integer(3)));        
        }
    }
        
    public void testEquals() throws Exception {
        OrBinaryPredicate p = new OrBinaryPredicate();
        assertEquals(p,p);

        OrBinaryPredicate q = new OrBinaryPredicate();
        assertObjectsAreEqual(p,q);

        AndBinaryPredicate r = new AndBinaryPredicate();
        assertObjectsAreNotEqual(p,r);
        
        for(int i=0;i<3;i++) {
            p.or(ConstantPredicate.getTruePredicate());
            assertObjectsAreNotEqual(p,q);
            q.or(ConstantPredicate.getTruePredicate());
            assertObjectsAreEqual(p,q);
            r.and(ConstantPredicate.getTruePredicate());
            assertObjectsAreNotEqual(p,r);

            p.or(new OrBinaryPredicate(ConstantPredicate.getTruePredicate(),ConstantPredicate.getFalsePredicate()));
            assertObjectsAreNotEqual(p,q);            
            q.or(new OrBinaryPredicate(ConstantPredicate.getTruePredicate(),ConstantPredicate.getFalsePredicate()));
            assertObjectsAreEqual(p,q);            
            r.and(new OrBinaryPredicate(ConstantPredicate.getTruePredicate(),ConstantPredicate.getFalsePredicate()));
            assertObjectsAreNotEqual(p,r);
        }
        
        assertObjectsAreNotEqual(p,ConstantPredicate.getTruePredicate());
    }

}
