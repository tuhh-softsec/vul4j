/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/core/composite/Attic/TestOrPredicate.java,v 1.1 2003/01/27 19:33:43 rwaldhoff Exp $
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
import org.apache.commons.functor.Predicate;
import org.apache.commons.functor.core.ConstantPredicate;

/**
 * @version $Revision: 1.1 $ $Date: 2003/01/27 19:33:43 $
 * @author Rodney Waldhoff
 */
public class TestOrPredicate extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestOrPredicate(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestOrPredicate.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new OrPredicate(new ConstantPredicate(false),new ConstantPredicate(true));
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
        assertTrue((new OrPredicate(new ConstantPredicate(true))).test());
        assertTrue((new OrPredicate(new ConstantPredicate(false),new ConstantPredicate(true))).test());
        assertTrue((new OrPredicate(new ConstantPredicate(false),new ConstantPredicate(false),new ConstantPredicate(true))).test());
        
        OrPredicate p = new OrPredicate(new ConstantPredicate(true));
        assertTrue(p.test());        
        for(int i=0;i<10;i++) {
            p.or(new ConstantPredicate(i%2==0));
            assertTrue(p.test());        
        }
        
        OrPredicate q = new OrPredicate(new ConstantPredicate(true));
        assertTrue(q.test());        
        for(int i=0;i<10;i++) {
            q.or(new ConstantPredicate(i%2==0));
            assertTrue(q.test());        
        }
        
        OrPredicate r = new OrPredicate(p,q);
        assertTrue(r.test());        
    }
    
    public void testFalse() throws Exception {
        assertTrue(!(new OrPredicate()).test());
        assertTrue(!(new OrPredicate(new ConstantPredicate(false))).test());
        assertTrue(!(new OrPredicate(new ConstantPredicate(false),new ConstantPredicate(false))).test());
        assertTrue(!(new OrPredicate(new ConstantPredicate(false),new ConstantPredicate(false),new ConstantPredicate(false))).test());
        
        OrPredicate p = new OrPredicate(new ConstantPredicate(false));
        assertTrue(!p.test());        
        for(int i=0;i<10;i++) {
            p.or(new ConstantPredicate(false));
            assertTrue(!p.test());        
        }
        
        OrPredicate q = new OrPredicate(new ConstantPredicate(false));
        assertTrue(!q.test());        
        for(int i=0;i<10;i++) {
            q.or(new ConstantPredicate(false));
            assertTrue(!q.test());        
        }
        
        OrPredicate r = new OrPredicate(p,q);
        assertTrue(!r.test());        
    }
        
    public void testDuplicateAdd() throws Exception {
        Predicate p = new ConstantPredicate(true);
        OrPredicate q = new OrPredicate(p,p);
        assertTrue(q.test());
        for(int i=0;i<10;i++) {
            q.or(p);
            assertTrue(q.test());        
        }
    }
        
    public void testEquals() throws Exception {
        OrPredicate p = new OrPredicate();
        assertEquals(p,p);

        OrPredicate q = new OrPredicate();
        assertObjectsAreEqual(p,q);

        AndPredicate r = new AndPredicate();
        assertObjectsAreNotEqual(p,r);
        
        for(int i=0;i<3;i++) {
            p.or(ConstantPredicate.getTruePredicate());
            assertObjectsAreNotEqual(p,q);
            q.or(ConstantPredicate.getTruePredicate());
            assertObjectsAreEqual(p,q);
            r.and(ConstantPredicate.getTruePredicate());
            assertObjectsAreNotEqual(p,r);

            p.or(new OrPredicate(ConstantPredicate.getTruePredicate(),ConstantPredicate.getFalsePredicate()));
            assertObjectsAreNotEqual(p,q);            
            q.or(new OrPredicate(ConstantPredicate.getTruePredicate(),ConstantPredicate.getFalsePredicate()));
            assertObjectsAreEqual(p,q);            
            r.and(new OrPredicate(ConstantPredicate.getTruePredicate(),ConstantPredicate.getFalsePredicate()));
            assertObjectsAreNotEqual(p,r);
        }
        
        assertObjectsAreNotEqual(p,ConstantPredicate.getTruePredicate());
    }

}
