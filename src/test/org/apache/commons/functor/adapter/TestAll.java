/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/adapter/TestAll.java,v 1.4 2003/03/04 21:33:56 rwaldhoff Exp $
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
package org.apache.commons.functor.adapter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @version $Revision: 1.4 $ $Date: 2003/03/04 21:33:56 $
 * @author Rodney Waldhoff
 */
public class TestAll extends TestCase {
    public TestAll(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        
        suite.addTest(TestFunctionProcedure.suite());
        suite.addTest(TestUnaryFunctionUnaryProcedure.suite());
        suite.addTest(TestBinaryFunctionBinaryProcedure.suite());
        
        suite.addTest(TestProcedureFunction.suite());
        suite.addTest(TestUnaryProcedureUnaryFunction.suite());
        suite.addTest(TestBinaryProcedureBinaryFunction.suite());

        suite.addTest(TestFunctionPredicate.suite());
        suite.addTest(TestUnaryFunctionUnaryPredicate.suite());
        suite.addTest(TestBinaryFunctionBinaryPredicate.suite());

        suite.addTest(TestPredicateFunction.suite());
        suite.addTest(TestUnaryPredicateUnaryFunction.suite());
        suite.addTest(TestBinaryPredicateBinaryFunction.suite());

        suite.addTest(TestFunctionUnaryFunction.suite());
        suite.addTest(TestIgnoreRightFunction.suite());
        suite.addTest(TestIgnoreLeftFunction.suite());
        
        suite.addTest(TestPredicateUnaryPredicate.suite());
        suite.addTest(TestIgnoreRightPredicate.suite());
        suite.addTest(TestIgnoreLeftPredicate.suite());
        
        suite.addTest(TestProcedureUnaryProcedure.suite());
        suite.addTest(TestIgnoreRightProcedure.suite());
        suite.addTest(TestIgnoreLeftProcedure.suite());
        
        suite.addTest(TestBoundFunction.suite());
        suite.addTest(TestLeftBoundFunction.suite());
        suite.addTest(TestRightBoundFunction.suite());
        
        suite.addTest(TestBoundPredicate.suite());
        suite.addTest(TestLeftBoundPredicate.suite());
        suite.addTest(TestRightBoundPredicate.suite());

        suite.addTest(TestBoundProcedure.suite());
        suite.addTest(TestLeftBoundProcedure.suite());
        suite.addTest(TestRightBoundProcedure.suite());
        
        return suite;
    }
}
