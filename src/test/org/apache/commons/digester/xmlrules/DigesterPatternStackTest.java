/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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

package org.apache.commons.digester.xmlrules;


import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * This test case tests the behavior of
 * DigesterRuleParser.PatternStack, a specialized stack whose
 * toString() method returns a /-separated representation of the
 * stack's elements. The tests ensure that
 * DigesterRuleParser.PatternStack.toString() returns the properly
 * formatted string.
 */
public class DigesterPatternStackTest extends TestCase {

    public DigesterPatternStackTest(String testName) {
        super(testName);
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static junit.framework.Test suite() {
        TestSuite suite = new TestSuite(DigesterPatternStackTest.class);

        return suite;
    }

    private DigesterRuleParser.PatternStack stack;

    public void setUp() {
        DigesterRuleParser parser = new DigesterRuleParser();
        stack = parser.patternStack;
    }

    public void test1() throws Exception {
        assertEquals("", stack.toString());
    }

    public void test2() throws Exception {
        stack.push("A");
        assertEquals("A", stack.toString());
        stack.pop();
        assertEquals("", stack.toString());
    }

    public void test3() throws Exception {
        stack.push("A");
        stack.push("B");
        assertEquals("A/B", stack.toString());

        stack.pop();
        assertEquals("A", stack.toString());
    }

    public void test4() throws Exception {
        stack.push("");
        assertEquals("", stack.toString());

        stack.push("");
        assertEquals("", stack.toString());
    }

    public void test5() throws Exception {
        stack.push("A");
        assertEquals("A", stack.toString());

        stack.push("");
        stack.push("");
        assertEquals("A", stack.toString());

    }

    public void test6() throws Exception {
        stack.push("A");
        stack.push("B");
        stack.clear();
        assertEquals("", stack.toString());
    }

    public void test7() throws Exception {
        stack.push("///");
        assertEquals("///", stack.toString());

        stack.push("/");
        assertEquals("/////", stack.toString());

        stack.pop();
        assertEquals("///", stack.toString());

        stack.pop();
        assertEquals("", stack.toString());
    }

}
