/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/core/composite/TestTransposedProcedure.java,v 1.2 2003/02/24 11:48:09 rwaldhoff Exp $
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
import org.apache.commons.functor.BinaryProcedure;
import org.apache.commons.functor.adapter.BinaryFunctionBinaryProcedure;
import org.apache.commons.functor.core.LeftIdentityFunction;
import org.apache.commons.functor.core.NoOp;

/**
 * @version $Revision: 1.2 $ $Date: 2003/02/24 11:48:09 $
 * @author Rodney Waldhoff
 */
public class TestTransposedProcedure extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestTransposedProcedure(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestTransposedProcedure.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new TransposedProcedure(NoOp.getNoOpProcedure());
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
        LeftNotNullCounter counter = new LeftNotNullCounter();
        BinaryProcedure p = new TransposedProcedure(counter);
        assertEquals(0,counter.count);
        p.run(null,"not null");
        assertEquals(1,counter.count);
        p.run("not null",null);
        assertEquals(1,counter.count);
    }
        
    public void testEquals() throws Exception {
        BinaryProcedure p = new TransposedProcedure(NoOp.getNoOpProcedure());
        assertEquals(p,p);
        assertObjectsAreEqual(p,new TransposedProcedure(NoOp.getNoOpProcedure()));
        assertObjectsAreEqual(p,TransposedProcedure.transpose(NoOp.getNoOpProcedure()));
        assertObjectsAreNotEqual(p,new TransposedProcedure(new TransposedProcedure(NoOp.getNoOpProcedure())));
        assertObjectsAreNotEqual(p,new TransposedProcedure(null));
        assertObjectsAreNotEqual(p,new NoOp());
    }

    public void testTransposeNull() throws Exception {
        assertNull(TransposedProcedure.transpose(null));
    }

    public void testTranspose() throws Exception {
        assertNotNull(TransposedProcedure.transpose(new NoOp()));
    }

    // Classes
    // ------------------------------------------------------------------------
    
    static class LeftNotNullCounter implements BinaryProcedure {        
        public void run(Object a, Object b) {
            if(null != a) {
                count++;    
            }
        }        
        public int count = 0;
    }
    
}
