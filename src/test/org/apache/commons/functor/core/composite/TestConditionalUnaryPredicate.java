/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/core/composite/TestConditionalUnaryPredicate.java,v 1.2 2003/01/29 23:24:52 rwaldhoff Exp $
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
import org.apache.commons.functor.core.ConstantPredicate;
import org.apache.commons.functor.core.IdentityPredicate;

/**
 * @version $Revision: 1.2 $ $Date: 2003/01/29 23:24:52 $
 * @author Rodney Waldhoff
 */
public class TestConditionalUnaryPredicate extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestConditionalUnaryPredicate(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestConditionalUnaryPredicate.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new ConditionalUnaryPredicate(
            new ConstantPredicate(true),
            new ConstantPredicate(false),
            new ConstantPredicate(true));
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
        ConditionalUnaryPredicate p = new ConditionalUnaryPredicate(
            new IdentityPredicate(),
            new ConstantPredicate(true),
            new ConstantPredicate(false));
        assertTrue(p.test(Boolean.TRUE));
        assertTrue(!p.test(Boolean.FALSE));
    }
    
    public void testEquals() throws Exception {
        ConditionalUnaryPredicate p = new ConditionalUnaryPredicate(
            new IdentityPredicate(),
            new ConstantPredicate(true),
            new ConstantPredicate(true));
        assertEquals(p,p);
        assertObjectsAreEqual(p,new ConditionalUnaryPredicate(
            new IdentityPredicate(),
            new ConstantPredicate(true),
            new ConstantPredicate(true)));
        assertObjectsAreNotEqual(p,new ConditionalUnaryPredicate(
            new IdentityPredicate(),
            new ConstantPredicate(false),
            new ConstantPredicate(true)));
        assertObjectsAreNotEqual(p,new ConditionalUnaryPredicate(
            new ConstantPredicate(true),
            new ConstantPredicate(true),
            new ConstantPredicate(true)));
        assertObjectsAreNotEqual(p,new ConditionalUnaryPredicate(
            null,
            new ConstantPredicate(true),
            new ConstantPredicate(true)));
        assertObjectsAreNotEqual(p,new ConditionalUnaryPredicate(
            new ConstantPredicate(true),
            null,
            new ConstantPredicate(true)));
        assertObjectsAreNotEqual(p,new ConditionalUnaryPredicate(
            new ConstantPredicate(true),
            new ConstantPredicate(true),
            null));
    }
}
