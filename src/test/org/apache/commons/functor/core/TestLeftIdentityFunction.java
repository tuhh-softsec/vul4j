/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/core/Attic/TestLeftIdentityFunction.java,v 1.1 2003/01/27 19:33:42 rwaldhoff Exp $
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
package org.apache.commons.functor.core;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.functor.BaseFunctorTest;
import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.UnaryFunction;

/**
 * @version $Revision: 1.1 $ $Date: 2003/01/27 19:33:42 $
 * @author Rodney Waldhoff
 */
public class TestLeftIdentityFunction extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestLeftIdentityFunction(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestLeftIdentityFunction.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new LeftIdentityFunction();
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
        BinaryFunction f = new LeftIdentityFunction();
        assertNull(f.evaluate(null,null));
        assertNull(f.evaluate(null,"xyzzy"));
        assertEquals("xyzzy",f.evaluate("xyzzy","abcdefg"));
        assertEquals("xyzzy",f.evaluate("xyzzy",null));
        assertEquals(new Integer(3),f.evaluate(new Integer(3),null));
        Object obj = new Long(12345L);
        assertSame(obj,f.evaluate(obj,null));
        assertSame(obj,f.evaluate(obj,obj));
    }
    
    public void testEquals() throws Exception {
        BinaryFunction f = new LeftIdentityFunction();
        assertEquals(f,f);
        assertObjectsAreEqual(f,new LeftIdentityFunction());
        assertObjectsAreEqual(f,LeftIdentityFunction.getLeftIdentityFunction());
        assertObjectsAreNotEqual(f,new ConstantFunction("abcde"));
    }
    
    public void testConstant() throws Exception {
        assertEquals(LeftIdentityFunction.getLeftIdentityFunction(),LeftIdentityFunction.getLeftIdentityFunction());
        assertSame(LeftIdentityFunction.getLeftIdentityFunction(),LeftIdentityFunction.getLeftIdentityFunction());
    }
}
