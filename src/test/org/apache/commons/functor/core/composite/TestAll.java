/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/test/org/apache/commons/functor/core/composite/TestAll.java,v 1.8 2003/12/03 01:04:11 rwaldhoff Exp $
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
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @version $Revision: 1.8 $ $Date: 2003/12/03 01:04:11 $
 * @author Rodney Waldhoff
 */
public class TestAll extends TestCase {
    public TestAll(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();

        suite.addTest(TestAnd.suite());
        suite.addTest(TestUnaryAnd.suite());
        suite.addTest(TestBinaryAnd.suite());

        suite.addTest(TestOr.suite());
        suite.addTest(TestUnaryOr.suite());
        suite.addTest(TestBinaryOr.suite());
        
        suite.addTest(TestNot.suite());
        suite.addTest(TestUnaryNot.suite());
        suite.addTest(TestBinaryNot.suite());

        suite.addTest(TestSequence.suite());
        suite.addTest(TestUnarySequence.suite());
        suite.addTest(TestBinarySequence.suite());

        suite.addTest(TestComposite.suite());
        
        suite.addTest(TestCompositeUnaryFunction.suite());
        suite.addTest(TestCompositeUnaryPredicate.suite());
        suite.addTest(TestCompositeUnaryProcedure.suite());
        suite.addTest(TestUnaryCompositeBinaryFunction.suite());
        suite.addTest(TestUnaryCompositeBinaryPredicate.suite());
        suite.addTest(TestBinaryCompositeBinaryFunction.suite());

        suite.addTest(TestTransposedFunction.suite());
        suite.addTest(TestTransposedPredicate.suite());
        suite.addTest(TestTransposedProcedure.suite());

        suite.addTest(TestConditional.suite());

        suite.addTest(TestConditionalPredicate.suite());
        suite.addTest(TestConditionalUnaryPredicate.suite());
        suite.addTest(TestConditionalBinaryPredicate.suite());

        suite.addTest(TestConditionalFunction.suite());
        suite.addTest(TestConditionalUnaryFunction.suite());
        suite.addTest(TestConditionalBinaryFunction.suite());

        suite.addTest(TestConditionalProcedure.suite());
        suite.addTest(TestConditionalUnaryProcedure.suite());
        suite.addTest(TestConditionalBinaryProcedure.suite());
        
		suite.addTest(TestAbstractLoopProcedure.suite());
		suite.addTest(TestWhileDoProcedure.suite());
		suite.addTest(TestDoWhileProcedure.suite());
        
        return suite;
    }
}
