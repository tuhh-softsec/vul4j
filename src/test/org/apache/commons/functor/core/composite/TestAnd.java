/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/core/composite/TestAnd.java,v 1.4 2003/12/03 01:04:11 rwaldhoff Exp $
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
import org.apache.commons.functor.core.Constant;

/**
 * @version $Revision: 1.4 $ $Date: 2003/12/03 01:04:11 $
 * @author Rodney Waldhoff
 */
public class TestAnd extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestAnd(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestAnd.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new And(new Constant(true),new Constant(true));
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
        assertTrue((new And()).test());
        assertTrue((new And(new Constant(true))).test());
        assertTrue((new And(new Constant(true),new Constant(true))).test());
        assertTrue((new And(new Constant(true),new Constant(true),new Constant(true))).test());
        
        And p = new And(new Constant(true));
        assertTrue(p.test());        
        for(int i=0;i<10;i++) {
            p.and(new Constant(true));
            assertTrue(p.test());        
        }
        
        And q = new And(new Constant(true));
        assertTrue(q.test());        
        for(int i=0;i<10;i++) {
            q.and(new Constant(true));
            assertTrue(q.test());        
        }
        
        And r = new And(p,q);
        assertTrue(r.test());        
    }
    
    public void testFalse() throws Exception {
        assertTrue(!(new And(new Constant(false))).test());
        assertTrue(!(new And(new Constant(true),new Constant(false))).test());
        assertTrue(!(new And(new Constant(true),new Constant(true),new Constant(false))).test());
        
        And p = new And(new Constant(false));
        assertTrue(!p.test());        
        for(int i=0;i<10;i++) {
            p.and(new Constant(false));
            assertTrue(!p.test());        
        }
        
        And q = new And(new Constant(true));
        assertTrue(q.test());        
        for(int i=0;i<10;i++) {
            q.and(new Constant(true));
            assertTrue(q.test());        
        }
        
        And r = new And(p,q);
        assertTrue(!r.test());        
    }
        
    public void testDuplicateAdd() throws Exception {
        Predicate p = new Constant(true);
        And q = new And(p,p);
        assertTrue(q.test());
        for(int i=0;i<10;i++) {
            q.and(p);
            assertTrue(q.test());        
        }
    }
        
    public void testEquals() throws Exception {
        And p = new And();
        assertEquals(p,p);
        And q = new And();
        assertObjectsAreEqual(p,q);

        for(int i=0;i<3;i++) {
            p.and(Constant.truePredicate());
            assertObjectsAreNotEqual(p,q);
            q.and(Constant.truePredicate());
            assertObjectsAreEqual(p,q);
            p.and(new And(Constant.truePredicate(),Constant.falsePredicate()));
            assertObjectsAreNotEqual(p,q);            
            q.and(new And(Constant.truePredicate(),Constant.falsePredicate()));
            assertObjectsAreEqual(p,q);            
        }
        
        assertObjectsAreNotEqual(p,Constant.truePredicate());
    }

}
