/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/core/composite/TestCompositeUnaryPredicate.java,v 1.3 2003/12/03 15:24:46 rwaldhoff Exp $
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
import org.apache.commons.functor.core.Constant;
import org.apache.commons.functor.core.Identity;

/**
 * @version $Revision: 1.3 $ $Date: 2003/12/03 15:24:46 $
 * @author Rodney Waldhoff
 */
public class TestCompositeUnaryPredicate extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestCompositeUnaryPredicate(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestCompositeUnaryPredicate.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new CompositeUnaryPredicate(new Identity(),new Constant(true));
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
    
    public void testTest() throws Exception {
        assertEquals(true,(new CompositeUnaryPredicate(new Constant(true))).test(null));        
        assertEquals(true,(new CompositeUnaryPredicate(new Constant(true),new Constant(new Integer(3)))).test("xyzzy"));
        assertEquals(false,(new CompositeUnaryPredicate(new Constant(false),new Constant(new Integer(4)))).test("xyzzy"));
    }

    public void testNullNotAllowed() throws Exception {
        try {
            new CompositeUnaryPredicate(null);
            fail("Expected NullPointerException");
        } catch(NullPointerException e) {
            // expected
        }
        try {
            new CompositeUnaryPredicate(null,null);
            fail("Expected NullPointerException");
        } catch(NullPointerException e) {
            // expected
        }
        try {
            new CompositeUnaryPredicate(Constant.truePredicate(),null);
            fail("Expected NullPointerException");
        } catch(NullPointerException e) {
            // expected
        }
    }
        
    public void testOf() throws Exception {
        CompositeUnaryPredicate f = new CompositeUnaryPredicate(new Constant(true));
        assertTrue(f.test(null));
        for(int i=0;i<10;i++) {
            f.of(new Constant(false));
            assertEquals(true,f.test(null));
        }
    }
    
    public void testEquals() throws Exception {
        CompositeUnaryPredicate f = new CompositeUnaryPredicate(new Constant(true));
        assertEquals(f,f);
        CompositeUnaryPredicate g = new CompositeUnaryPredicate(new Constant(true));
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
